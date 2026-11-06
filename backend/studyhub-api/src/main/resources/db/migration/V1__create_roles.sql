-- V1: Create roles table
-- Sprint 1 — Foundation schema
-- Roles are fixed at application startup; not user-editable.

CREATE TABLE roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed the two roles required for the pilot.
-- LEARNER  — standard enrolled learner
-- ADMIN    — platform administrator; can manage invitations and view usage
INSERT INTO roles (name, description) VALUES
    ('ROLE_LEARNER', 'Standard enrolled learner'),
    ('ROLE_ADMIN',   'Platform administrator');
