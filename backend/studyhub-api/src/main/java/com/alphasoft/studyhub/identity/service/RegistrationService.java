package com.alphasoft.studyhub.identity.service;

import com.alphasoft.studyhub.config.StudyHubProperties;
import com.alphasoft.studyhub.identity.domain.InvitationCode;
import com.alphasoft.studyhub.identity.domain.Role;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.InvitationCodeRepository;
import com.alphasoft.studyhub.identity.repository.RoleRepository;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Handles invite-only learner registration with pilot capacity enforcement.
 *
 * Security model (DR-024, DR-025):
 *  - Every registration requires a valid, non-expired, non-revoked, non-redeemed invite code.
 *  - The invite code row is locked with PESSIMISTIC_WRITE before validation, preventing
 *    two concurrent registrations from redeeming the same code.
 *  - The pilot learner cap (max 10) is checked inside the same serialisable transaction
 *    after the row lock is held, so the count cannot increase between the check and the
 *    user INSERT.
 *  - Passwords are hashed with BCrypt (work factor 12) before storage; plaintext never
 *    reaches the database.
 *  - Error messages do not distinguish "code not found" from "code expired/revoked" to
 *    avoid oracle attacks.
 */
@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private static final String ROLE_LEARNER = "ROLE_LEARNER";

    private final UserRepository           userRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final RoleRepository           roleRepository;
    private final PasswordEncoder          passwordEncoder;
    private final StudyHubProperties       props;

    public RegistrationService(
            UserRepository           userRepository,
            InvitationCodeRepository invitationCodeRepository,
            RoleRepository           roleRepository,
            PasswordEncoder          passwordEncoder,
            StudyHubProperties       props) {
        this.userRepository           = userRepository;
        this.invitationCodeRepository = invitationCodeRepository;
        this.roleRepository           = roleRepository;
        this.passwordEncoder          = passwordEncoder;
        this.props                    = props;
    }

    /**
     * Registers a new learner.
     *
     * <p>Runs at SERIALIZABLE isolation with a pessimistic write lock on the
     * invitation code row to prevent concurrent redemptions of the same code
     * and to prevent the pilot cap from being exceeded under concurrent load.
     *
     * @param email      new learner's email
     * @param password   plaintext password (hashed before storage)
     * @param displayName display name
     * @param inviteCode UUID invite code
     * @return the newly created {@link User}
     * @throws RegistrationException if any validation fails
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User register(String email, String password, String displayName, String inviteCode) {

        // 1. Acquire pessimistic write lock on the invite code row (DR-024 concurrency)
        InvitationCode code = invitationCodeRepository
                .findByCodeWithLock(inviteCode)
                .orElseThrow(() -> new RegistrationException("Invalid or expired invitation code"));

        // 2. Validate code state (re-check under lock — another thread may have just redeemed it)
        if (!code.isUsable()) {
            log.debug("Registration rejected: invite code not usable (code={})", inviteCode);
            throw new RegistrationException("Invalid or expired invitation code");
        }

        // 3. Email uniqueness check
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("An account with this email already exists");
        }

        // 4. Pilot capacity check (DR-025) — evaluated under lock so count is stable
        long currentLearners = userRepository.countActiveLearners();
        int  maxLearners     = props.pilot().maxLearners();
        if (currentLearners >= maxLearners) {
            log.warn("Registration rejected: pilot capacity reached ({}/{})", currentLearners, maxLearners);
            throw new RegistrationException(
                "The pilot programme is currently at capacity. Please contact your administrator.");
        }

        // 5. Create the user
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword, displayName);
        user.setEmailVerified(false);
        user.setActive(true);

        Role learnerRole = roleRepository.findByName(ROLE_LEARNER)
                .orElseThrow(() -> new IllegalStateException(
                    "ROLE_LEARNER not found in database — Flyway V1 may not have run"));
        user.addRole(learnerRole);
        userRepository.save(user);

        // 6. Mark code as redeemed (under the same lock / transaction)
        code.setUsedByUser(user);
        code.setUsedAt(Instant.now());
        invitationCodeRepository.save(code);

        log.info("New learner registered: userId={} email={}", user.getId(), email);
        return user;
    }
}
