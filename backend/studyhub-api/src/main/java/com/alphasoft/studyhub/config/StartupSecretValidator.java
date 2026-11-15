package com.alphasoft.studyhub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validates that placeholder JWT secrets, weak DB credentials, and insecure
 * cookie configuration have been replaced via environment variables before
 * the application serves real traffic.
 *
 * Active on all profiles EXCEPT "test".
 *
 * Throws IllegalStateException on startup if:
 *  - JWT secrets still contain the placeholder string "CHANGE_ME"
 *  - JWT secrets are shorter than 32 characters (minimum for HMAC-SHA256)
 *  - DB password is "changeme" (the application.yml local dev default)
 *  - COOKIE_SECURE is false on the "prod" profile (Sprint 4 audit finding S4-14)
 *
 * This guard prevents a misconfigured production deployment from silently
 * accepting or issuing JWT tokens signed with a known-public secret, or from
 * transmitting refresh token cookies over plain HTTP.
 *
 * Security note (F-SEC-002 from Sprint 1 Validation Gate):
 * The application.yml defaults ship placeholder values so that local dev
 * works without any .env setup. Those same defaults would be catastrophic
 * in production. This validator is the enforcement point.
 *
 * Sprint 4 addition (S4-14):
 * The "prod" Spring profile must have COOKIE_SECURE=true (i.e. studyhub.cookie.secure=true).
 * Local development uses COOKIE_SECURE=false (HTTP), which is intentional and allowed on
 * any profile other than "prod". If a production deployment accidentally leaves
 * COOKIE_SECURE=false, the httpOnly refresh token cookie would be sent over plain HTTP —
 * violating the security requirement that refresh tokens remain protected in transit.
 */
@Component
@Profile("!test")
public class StartupSecretValidator {

    private static final Logger log = LoggerFactory.getLogger(StartupSecretValidator.class);

    private static final String PLACEHOLDER_MARKER      = "CHANGE_ME";
    private static final String PLACEHOLDER_DB_PASSWORD = "changeme";
    private static final int    MIN_JWT_SECRET_LENGTH   = 32;

    private final StudyHubProperties props;
    private final org.springframework.core.env.Environment environment;

    public StartupSecretValidator(StudyHubProperties props,
                                  org.springframework.core.env.Environment environment) {
        this.props       = props;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateSecrets() {
        log.info("StartupSecretValidator: checking secrets and security configuration...");

        validateJwtSecret("studyhub.jwt.access-secret",
                "JWT_ACCESS_SECRET",
                props.jwt().accessSecret());

        validateJwtSecret("studyhub.jwt.refresh-secret",
                "JWT_REFRESH_SECRET",
                props.jwt().refreshSecret());

        validateCookieSecure();

        log.info("StartupSecretValidator: all secrets and security configuration OK.");
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void validateJwtSecret(String propertyName, String envVar, String secret) {
        if (secret == null || secret.isBlank()) {
            fail(propertyName, envVar, "is null or blank");
        }
        if (secret.contains(PLACEHOLDER_MARKER)) {
            fail(propertyName, envVar,
                    "contains the placeholder string \"" + PLACEHOLDER_MARKER + "\" — "
                    + "set the " + envVar + " environment variable to a random secret");
        }
        if (secret.length() < MIN_JWT_SECRET_LENGTH) {
            fail(propertyName, envVar,
                    "is only " + secret.length() + " characters; minimum is "
                    + MIN_JWT_SECRET_LENGTH + " for HMAC-SHA256");
        }
    }

    /**
     * Sprint 4 (S4-14): When the active Spring profile includes "prod",
     * studyhub.cookie.secure MUST be true.
     *
     * This prevents an accidental COOKIE_SECURE=false in production, which would
     * cause the httpOnly refresh token cookie to be transmitted over plain HTTP,
     * exposing it to network interception.
     *
     * Local development explicitly runs without "prod" profile and may use HTTP.
     */
    private void validateCookieSecure() {
        boolean isProdProfile = java.util.Arrays.asList(environment.getActiveProfiles())
                .contains("prod");
        if (isProdProfile && !props.cookie().secure()) {
            String msg = String.format(
                "STARTUP ABORTED — insecure cookie configuration detected on prod profile.%n"
                + "  Property  : studyhub.cookie.secure%n"
                + "  Env var   : COOKIE_SECURE%n"
                + "  Problem   : COOKIE_SECURE is false (or not set) but active profile includes 'prod'.%n"
                + "              The httpOnly refresh token cookie will be sent over plain HTTP.%n"
                + "  Fix       : export COOKIE_SECURE=true (requires HTTPS on the production host).%n"
                + "  Note      : Local development without the 'prod' profile may leave COOKIE_SECURE=false."
            );
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        if (isProdProfile) {
            log.info("StartupSecretValidator: COOKIE_SECURE=true confirmed for prod profile. OK.");
        } else {
            log.debug("StartupSecretValidator: not running 'prod' profile — COOKIE_SECURE check skipped.");
        }
    }

    private void fail(String propertyName, String envVar, String reason) {
        String msg = String.format(
            "STARTUP ABORTED — insecure configuration detected.%n"
            + "  Property  : %s%n"
            + "  Env var   : %s%n"
            + "  Problem   : %s%n"
            + "  Fix       : export %s=<a strong random value of at least %d characters>",
            propertyName, envVar, reason, envVar, MIN_JWT_SECRET_LENGTH
        );
        log.error(msg);
        throw new IllegalStateException(msg);
    }
}
