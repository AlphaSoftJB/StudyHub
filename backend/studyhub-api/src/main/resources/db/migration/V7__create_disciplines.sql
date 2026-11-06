-- V7: Create disciplines table (Sprint 3 — Curriculum domain)
--
-- DR-012 content lifecycle: disciplines are top-level curriculum containers.
-- Only disciplines with active=true are exposed to learners.
-- Pilot disciplines: Cybersecurity (cyber), IBM Python+ML (ibm).
--
-- Note: Two separate ALTER statements used for H2 MySQL MODE compat (see V6 pattern).

CREATE TABLE disciplines (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100) NOT NULL,
    slug             VARCHAR(100) NOT NULL,
    description      TEXT,
    icon             VARCHAR(50),
    -- active=false hides the discipline from all learner-facing endpoints
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    sequence_order   INT          NOT NULL DEFAULT 0,
    created_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_disciplines_slug UNIQUE (slug)
);

CREATE INDEX idx_disciplines_active ON disciplines (active);
CREATE INDEX idx_disciplines_slug   ON disciplines (slug);
