package com.alphasoft.studyhub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Sprint 2 preparation — validates that placeholder JWT secrets and weak DB credentials
 * have been replaced via environment variables before the application serves real traffic.
 *
 * Active on all profiles EXCEPT "test".
 *
 * Throws IllegalStateException on startup if:
 *  - JWT secrets still contain the placeholder string "CHANGE_ME"
 *  - JWT secrets are shorter than 32 characters (minimum for HMAC-SHA256)
 *  - DB password is "changeme" (the application.yml local dev default)
 *
 * This guard prevents a misconfigured production deployment from silently
 * accepting or issuing JWT tokens signed with a known-public secret.
 *
 * Security note (F-SEC-002 from Sprint 1 Validation Gate):
 * The application.yml defaults ship placeholder values so that local dev
 * works without any .env setup. Those same defaults would be catastrophic
 * in production. This validator is the enforcement point.
 */
@Component
@Profile("!test")
public class StartupSecretValidator {

    private static final Logger log = LoggerFactory.getLogger(StartupSecretValidator.class);

    private static final String PLACEHOLDER_MARKER      = "CHANGE_ME";
    private static final String PLACEHOLDER_DB_PASSWORD = "changeme";
    private static final int    MIN_JWT_SECRET_LENGTH   = 32;

    private final StudyHubProperties props;

    public StartupSecretValidator(StudyHubProperties props) {
        this.props = props;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateSecrets() {
        log.info("StartupSecretValidator: checking secrets...");

        validateJwtSecret("studyhub.jwt.access-secret",
                "JWT_ACCESS_SECRET",
                props.jwt().accessSecret());

        validateJwtSecret("studyhub.jwt.refresh-secret",
                "JWT_REFRESH_SECRET",
                props.jwt().refreshSecret());

        log.info("StartupSecretValidator: all secrets look non-default. OK.");
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
