package com.alphasoft.studyhub.auth;

import com.alphasoft.studyhub.identity.domain.InvitationCode;
import com.alphasoft.studyhub.identity.domain.Role;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.InvitationCodeRepository;
import com.alphasoft.studyhub.identity.repository.RoleRepository;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import com.alphasoft.studyhub.identity.service.RegistrationException;
import com.alphasoft.studyhub.identity.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprint 2 — Concurrency tests for invite redemption and pilot capacity.
 *
 * These tests verify DB-level protections hold under concurrent load.
 * H2 in SERIALIZABLE mode (via @Transactional(isolation=SERIALIZABLE) in
 * RegistrationService) guarantees only one registration succeeds per invite code.
 *
 * Tests:
 *  1. Concurrent invite redemption: 5 threads try same code → exactly 1 succeeds.
 *  2. Pilot capacity: registrations that exceed max (10) are rejected.
 */
@SpringBootTest
@ActiveProfiles("test")
class ConcurrencyTest {

    @Autowired RegistrationService    registrationService;
    @Autowired UserRepository         userRepository;
    @Autowired RoleRepository         roleRepository;
    @Autowired InvitationCodeRepository inviteRepo;
    @Autowired PasswordEncoder        passwordEncoder;

    private User adminUser;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        // Each test gets a fresh admin so email doesn't conflict
        String adminEmail = "admin-" + UUID.randomUUID() + "@test.com";
        adminUser = new User(adminEmail, passwordEncoder.encode("Admin@1234"), "Admin");
        adminUser.addRole(adminRole);
        userRepository.save(adminUser);
    }

    // ── Test 1: Concurrent invite redemption ─────────────────────────────────

    @Test
    @DisplayName("Concurrent-1: 5 threads try to redeem the same invite → exactly 1 succeeds")
    void concurrent_invite_redemption_only_one_succeeds() throws Exception {
        String code = UUID.randomUUID().toString();
        InvitationCode invite = new InvitationCode(
                code, adminUser, Instant.now().plusSeconds(3600));
        inviteRepo.save(invite);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            tasks.add(() -> {
                try {
                    registrationService.register(
                        "concurrent-" + idx + "@test.com",
                        "Password@123",
                        "Concurrent " + idx,
                        code
                    );
                    successCount.incrementAndGet();
                } catch (RegistrationException | Exception ex) {
                    failureCount.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        // Wait for all tasks to complete
        for (Future<Void> f : futures) {
            try { f.get(); } catch (Exception ignored) {}
        }

        assertThat(successCount.get())
            .as("Exactly one registration should succeed for a single-use code")
            .isEqualTo(1);
        assertThat(failureCount.get())
            .as("All other attempts should fail")
            .isEqualTo(threadCount - 1);

        // Verify the code is now marked as redeemed
        InvitationCode used = inviteRepo.findByCode(code).orElseThrow();
        assertThat(used.isRedeemed()).isTrue();
    }

    // ── Test 2: Pilot capacity enforcement ────────────────────────────────────

    @Test
    @DisplayName("Concurrent-2: Registrations beyond pilot cap (10) are rejected")
    void registrations_beyond_pilot_cap_rejected() throws Exception {
        // Create 11 invite codes
        int inviteCount = 11;
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < inviteCount; i++) {
            String code = UUID.randomUUID().toString();
            inviteRepo.save(new InvitationCode(code, adminUser, Instant.now().plusSeconds(3600)));
            codes.add(code);
        }

        // Register the first 10 sequentially (to reach the cap)
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < inviteCount; i++) {
            try {
                registrationService.register(
                    "pilot-" + i + "-" + UUID.randomUUID() + "@test.com",
                    "Password@123",
                    "Pilot " + i,
                    codes.get(i)
                );
                successCount++;
            } catch (RegistrationException ex) {
                failureCount++;
            }
        }

        assertThat(successCount)
            .as("At most 10 registrations should succeed (pilot cap)")
            .isLessThanOrEqualTo(10);
        assertThat(failureCount)
            .as("At least 1 registration should be rejected (over cap)")
            .isGreaterThanOrEqualTo(1);

        // Verify active learner count does not exceed cap
        long activeLearners = userRepository.countActiveLearners();
        assertThat(activeLearners)
            .as("Active learner count must not exceed pilot max (10)")
            .isLessThanOrEqualTo(10);
    }
}
