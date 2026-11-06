-- V5: Create refresh_tokens table
-- Sprint 1 — Foundation schema
-- Server-side refresh token store for JWT rotation.
-- token_hash: SHA-256 of the actual token; raw token is never stored.
-- revoked: set to TRUE on logout or rotation; row retained for audit.
-- family_id: groups tokens issued from the same login; rotating
--            invalidates the whole family on reuse detection (Sprint 2).

CREATE TABLE refresh_tokens (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     BIGINT        NOT NULL,
    token_hash  VARCHAR(64)   NOT NULL,   -- hex-encoded SHA-256 (64 chars)
    family_id   VARCHAR(36)   NOT NULL,   -- UUID v4 assigned at login
    issued_at   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    expires_at  DATETIME(6)   NOT NULL,
    revoked     BOOLEAN       NOT NULL DEFAULT FALSE,
    revoked_at  DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user    (user_id),
    INDEX idx_refresh_tokens_family  (family_id),
    INDEX idx_refresh_tokens_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
