-- V4: Create invitation_codes table
-- Sprint 1 — Foundation schema
-- Implements DR-024: invite-only pilot registration.
-- Each code is single-use, UUID-based, issued by an admin.
-- used_by_user_id: populated when a learner redeems the code.

CREATE TABLE invitation_codes (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    code            VARCHAR(36)   NOT NULL,            -- UUID v4
    created_by      BIGINT        NOT NULL,            -- admin user id
    used_by_user_id BIGINT,                           -- NULL until redeemed
    used_at         DATETIME(6),
    expires_at      DATETIME(6)   NOT NULL,
    created_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_invitation_codes_code UNIQUE (code),
    CONSTRAINT fk_invitation_codes_created_by    FOREIGN KEY (created_by)      REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_invitation_codes_used_by_user  FOREIGN KEY (used_by_user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
