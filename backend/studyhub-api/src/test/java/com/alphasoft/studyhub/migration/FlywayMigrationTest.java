package com.alphasoft.studyhub.migration;

import com.alphasoft.studyhub.identity.domain.Role;
import com.alphasoft.studyhub.identity.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprint 1 — Flyway migration integration tests.
 *
 * Uses the "test" profile which runs against an in-memory H2 database
 * in MySQL compatibility mode (application-test.yml).
 *
 * Verifies:
 *  1. All V1–V5 migrations apply without error (context loads = migrations ran).
 *  2. The roles table is seeded with ROLE_LEARNER and ROLE_ADMIN.
 *  3. The users, user_roles, invitation_codes, and refresh_tokens tables exist
 *     (confirmed by schema validation passing and JPA entity mapping).
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FlywayMigrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("V1 migration seeds ROLE_LEARNER into the roles table")
    void v1_seeds_role_learner() {
        Optional<Role> learner = roleRepository.findByName("ROLE_LEARNER");
        assertThat(learner)
            .isPresent()
            .hasValueSatisfying(r -> {
                assertThat(r.getId()).isPositive();
                assertThat(r.getName()).isEqualTo("ROLE_LEARNER");
                assertThat(r.getDescription()).isEqualTo("Standard enrolled learner");
                assertThat(r.getCreatedAt()).isNotNull();
            });
    }

    @Test
    @DisplayName("V1 migration seeds ROLE_ADMIN into the roles table")
    void v1_seeds_role_admin() {
        Optional<Role> admin = roleRepository.findByName("ROLE_ADMIN");
        assertThat(admin)
            .isPresent()
            .hasValueSatisfying(r -> {
                assertThat(r.getId()).isPositive();
                assertThat(r.getName()).isEqualTo("ROLE_ADMIN");
                assertThat(r.getDescription()).isEqualTo("Platform administrator");
            });
    }

    @Test
    @DisplayName("Exactly two roles are seeded — no extras")
    void exactly_two_roles_seeded() {
        List<Role> all = roleRepository.findAll();
        assertThat(all).hasSize(2);
        assertThat(all)
            .extracting(Role::getName)
            .containsExactlyInAnyOrder("ROLE_LEARNER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Application context loads — all V1–V5 migrations applied successfully")
    void context_loads_means_migrations_succeeded() {
        // If context failed to load, this test would not run.
        // The fact that @SpringBootTest succeeded means Flyway ran V1–V5
        // and Hibernate's ddl-auto=validate confirmed the entity mapping matches the schema.
        assertThat(roleRepository).isNotNull();
    }
}
