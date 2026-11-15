package com.alphasoft.studyhub.security;

import com.alphasoft.studyhub.config.StartupSecretValidator;
import com.alphasoft.studyhub.config.StudyHubProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Sprint 4 (S4-14) — Unit tests for StartupSecretValidator.
 *
 * Tests the COOKIE_SECURE enforcement added in Sprint 4, alongside
 * existing JWT secret validation, to prevent production regressions.
 *
 * These tests run without Spring context — the validator is instantiated
 * directly with mock properties and a MockEnvironment.
 */
class StartupSecretValidatorTest {

    private static final String VALID_SECRET_32 = "valid_secret_exactly_32_chars_xx";
    private static final String PLACEHOLDER     = "CHANGE_ME_IN_PRODUCTION_min32chars_accesssecret";

    // ── helpers ───────────────────────────────────────────────────────────────

    private StudyHubProperties validProps(boolean cookieSecure) {
        return new StudyHubProperties(
            new StudyHubProperties.Jwt(
                VALID_SECRET_32, 900_000L,
                VALID_SECRET_32, 604_800_000L
            ),
            new StudyHubProperties.Cookie("localhost", cookieSecure, "Strict"),
            new StudyHubProperties.Cors(List.of("http://localhost:5173")),
            new StudyHubProperties.Pilot(10),
            new StudyHubProperties.Ai(BigDecimal.valueOf(25), BigDecimal.valueOf(20))
        );
    }

    private StudyHubProperties propsWithSecret(String accessSecret, String refreshSecret) {
        return new StudyHubProperties(
            new StudyHubProperties.Jwt(accessSecret, 900_000L, refreshSecret, 604_800_000L),
            new StudyHubProperties.Cookie("localhost", false, "Strict"),
            new StudyHubProperties.Cors(List.of("http://localhost:5173")),
            new StudyHubProperties.Pilot(10),
            new StudyHubProperties.Ai(BigDecimal.valueOf(25), BigDecimal.valueOf(20))
        );
    }

    private StartupSecretValidator validator(StudyHubProperties props, String... activeProfiles) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(activeProfiles);
        return new StartupSecretValidator(props, env);
    }

    // ── JWT secret validation ─────────────────────────────────────────────────

    @Nested
    @DisplayName("JWT secret validation — existing Sprint 2 behavior")
    class JwtSecretValidation {

        @Test
        @DisplayName("Valid secrets pass — no exception thrown")
        void valid_secrets_pass() {
            assertThatCode(() -> validator(validProps(false)).validateSecrets())
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Placeholder access secret fails")
        void placeholder_access_secret_fails() {
            assertThatThrownBy(() ->
                validator(propsWithSecret(PLACEHOLDER, VALID_SECRET_32)).validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("CHANGE_ME")
             .hasMessageContaining("JWT_ACCESS_SECRET");
        }

        @Test
        @DisplayName("Placeholder refresh secret fails")
        void placeholder_refresh_secret_fails() {
            assertThatThrownBy(() ->
                validator(propsWithSecret(VALID_SECRET_32, PLACEHOLDER)).validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("CHANGE_ME")
             .hasMessageContaining("JWT_REFRESH_SECRET");
        }

        @Test
        @DisplayName("Access secret shorter than 32 characters fails")
        void short_access_secret_fails() {
            assertThatThrownBy(() ->
                validator(propsWithSecret("tooshort", VALID_SECRET_32)).validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("8 characters")
             .hasMessageContaining("JWT_ACCESS_SECRET");
        }

        @Test
        @DisplayName("Refresh secret shorter than 32 characters fails")
        void short_refresh_secret_fails() {
            assertThatThrownBy(() ->
                validator(propsWithSecret(VALID_SECRET_32, "also_too_short")).validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("JWT_REFRESH_SECRET");
        }
    }

    // ── COOKIE_SECURE validation — Sprint 4 addition ──────────────────────────

    @Nested
    @DisplayName("COOKIE_SECURE validation (Sprint 4 — S4-14)")
    class CookieSecureValidation {

        @Test
        @DisplayName("prod profile + COOKIE_SECURE=true — passes")
        void prod_profile_with_secure_cookie_passes() {
            assertThatCode(() ->
                validator(validProps(true), "prod").validateSecrets()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("prod profile + COOKIE_SECURE=false — fails with clear message")
        void prod_profile_with_insecure_cookie_fails() {
            assertThatThrownBy(() ->
                validator(validProps(false), "prod").validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("COOKIE_SECURE")
             .hasMessageContaining("prod")
             .hasMessageContaining("httpOnly refresh token cookie");
        }

        @Test
        @DisplayName("no active profile + COOKIE_SECURE=false — passes (local dev is fine)")
        void no_profile_with_insecure_cookie_passes() {
            assertThatCode(() ->
                validator(validProps(false) /* no profiles */).validateSecrets()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("'default' profile (not 'prod') + COOKIE_SECURE=false — passes")
        void default_profile_with_insecure_cookie_passes() {
            assertThatCode(() ->
                validator(validProps(false), "default").validateSecrets()
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("'prod' profile + COOKIE_SECURE=false — error message includes fix instructions")
        void prod_insecure_cookie_error_includes_fix() {
            assertThatThrownBy(() ->
                validator(validProps(false), "prod").validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("export COOKIE_SECURE=true")
             .hasMessageContaining("HTTPS");
        }

        @Test
        @DisplayName("multiple profiles including 'prod' + COOKIE_SECURE=false — fails")
        void multiple_profiles_including_prod_insecure_fails() {
            assertThatThrownBy(() ->
                validator(validProps(false), "default", "prod").validateSecrets()
            ).isInstanceOf(IllegalStateException.class)
             .hasMessageContaining("COOKIE_SECURE");
        }
    }
}
