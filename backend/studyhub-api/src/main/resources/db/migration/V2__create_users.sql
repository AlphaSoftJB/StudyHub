-- V2: Create users table
-- Sprint 1 — Foundation schema
-- Stores learner and admin account data.
-- password_hash: BCrypt-encoded; never stored in plaintext.
-- email_verified: set to TRUE after the learner clicks the verification link (Phase 2).
-- active: soft-delete / account suspension flag; FALSE blocks login.

CREATE TABLE users (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    email          VARCHAR(255)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    display_name   VARCHAR(100)  NOT NULL,
    email_verified BOOLEAN       NOT NULL DEFAULT FALSE,
    active         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
