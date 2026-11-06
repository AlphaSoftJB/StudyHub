package com.alphasoft.studyhub.migration;

import com.alphasoft.studyhub.domain.identity.entity.InvitationCode;
import com.alphasoft.studyhub.domain.identity.entity.RefreshToken;
import com.alphasoft.studyhub.domain.identity.entity.Role;
import com.alphasoft.studyhub.domain.identity.entity.User;
import com.alphasoft.studyhub.domain.identity.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprint 1 — Schema and entity mapping validation.
 *
 * Verifies that all four domain entities can be persisted and retrieved,
 * confirming that:
 *  - The JPA column/constraint annotations match the Flyway schema
 *  - Relationships (ManyToMany, ManyToOne) resolve correctly
 *  - Audit timestamps are populated by @PrePersist
 *  - Business logic helpers (isUsable, isValid, etc.) work as expected
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SchemaValidationTest {

    @Autowired private UserRepository           userRepository;
    @Autowired private RoleRepository           roleRepository;
    @Autowired private InvitationCodeRepository invitationCodeRepository;
    @Autowired private RefreshTokenRepository   refreshTokenRepository;

    @Test
    @DisplayName("User entity persists with required fields and audit timestamps")
    void user_entity_persists() {
        User user = new User("test@example.com", "$2a$12$hashedPassword", "Test User");
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getDisplayName()).isEqualTo("Test User");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.isEmailVerified()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("User can be found by email (case-sensitive unique constraint)")
    void user_found_by_email() {
        userRepository.save(new User("find-me@example.com", "hash", "Find Me"));
        assertThat(userRepository.findByEmail("find-me@example.com")).isPresent();
        assertThat(userRepository.existsByEmail("find-me@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    @DisplayName("User role assignment persists through the user_roles join table")
    void user_role_assignment_persists() {
        Role learnerRole = roleRepository.findByName("ROLE_LEARNER").orElseThrow();
        User user = new User("learner@example.com", "hash", "Learner");
        user.addRole(learnerRole);
        User saved = userRepository.save(user);
        userRepository.flush();

        User fetched = userRepository.findById(saved.getId()).orElseThrow();
        // Initialize the lazy collection within the transaction
        assertThat(fetched.getRoles()).hasSize(1);
        assertThat(fetched.getRoles().iterator().next().getName()).isEqualTo("ROLE_LEARNER");
    }

    @Test
    @DisplayName("InvitationCode entity persists with usability logic")
    void invitation_code_persists_and_is_usable() {
        User admin = userRepository.save(new User("admin@example.com", "hash", "Admin"));
        Instant expires = Instant.now().plus(7, ChronoUnit.DAYS);

        InvitationCode code = new InvitationCode("test-uuid-1234", admin, expires);
        InvitationCode saved = invitationCodeRepository.save(code);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getCode()).isEqualTo("test-uuid-1234");
        assertThat(saved.isRedeemed()).isFalse();
        assertThat(saved.isExpired()).isFalse();
        assertThat(saved.isUsable()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("InvitationCode.findUsableByCode returns empty for expired codes")
    void invitation_code_expired_not_returned_as_usable() {
        User admin = userRepository.save(new User("admin2@example.com", "hash", "Admin2"));
        Instant alreadyExpired = Instant.now().minus(1, ChronoUnit.HOURS);

        invitationCodeRepository.save(new InvitationCode("expired-code", admin, alreadyExpired));
        invitationCodeRepository.flush();

        assertThat(invitationCodeRepository.findUsableByCode("expired-code")).isEmpty();
    }

    @Test
    @DisplayName("RefreshToken entity persists with valid/revoke logic")
    void refresh_token_persists_and_revoke_works() {
        User user = userRepository.save(new User("refresh@example.com", "hash", "Refresh User"));
        Instant expires = Instant.now().plus(7, ChronoUnit.DAYS);

        RefreshToken token = new RefreshToken(user, "abc123hash", "family-uuid", expires);
        RefreshToken saved = refreshTokenRepository.save(token);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.isExpired()).isFalse();
        assertThat(saved.isValid()).isTrue();
        assertThat(saved.getIssuedAt()).isNotNull();

        saved.revoke();
        RefreshToken revoked = refreshTokenRepository.save(saved);
        assertThat(revoked.isRevoked()).isTrue();
        assertThat(revoked.getRevokedAt()).isNotNull();
        assertThat(revoked.isValid()).isFalse();
    }

    @Test
    @DisplayName("revokeAllByFamilyId revokes all tokens in a family")
    void revoke_all_by_family_id() {
        User user = userRepository.save(new User("family@example.com", "hash", "Family User"));
        Instant expires = Instant.now().plus(7, ChronoUnit.DAYS);
        String familyId = "family-abc";

        refreshTokenRepository.save(new RefreshToken(user, "hash1", familyId, expires));
        refreshTokenRepository.save(new RefreshToken(user, "hash2", familyId, expires));
        refreshTokenRepository.flush();

        int count = refreshTokenRepository.revokeAllByFamilyId(familyId, Instant.now());
        assertThat(count).isEqualTo(2);

        refreshTokenRepository.findByFamilyId(familyId)
            .forEach(t -> assertThat(t.isRevoked()).isTrue());
    }
}
