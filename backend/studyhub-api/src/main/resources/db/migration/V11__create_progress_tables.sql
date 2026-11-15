-- V11: Create progress tables (Sprint 5 — Learner Progress Tracking)
--
-- user_lesson_progress: records each learner's lesson completion state.
--   One row per (user_id, lesson_id) pair.  Status is COMPLETED or IN_PROGRESS;
--   further states can be added in a later migration without breaking existing rows.
--
-- user_xp: cumulative experience-point ledger per learner.
--   A single row per user, incremented on each lesson completion.
--   XP is display-only in Sprint 5; mastery is tracked separately (DR-022).
--
-- DR-022 (progress ≠ mastery):
--   Completion of a lesson does NOT automatically establish mastery.
--   Mastery is computed in a later sprint from exam results.
--
-- DR-023 (GistSync coexistence):
--   These tables run in parallel with the existing localStorage / GistSync system;
--   neither replaces it.  Removal of GistSync requires a separate approval gate.
--
-- DR-015-NATIVE: all endpoints that query these tables support pagination from day one.

-- ── user_lesson_progress ─────────────────────────────────────────────────────

CREATE TABLE user_lesson_progress (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    lesson_id       BIGINT       NOT NULL,
    -- COMPLETED | IN_PROGRESS
    status          VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS',
    -- When the learner first opened the lesson (nullable until they do)
    started_at      DATETIME(6),
    -- When the learner marked it complete; NULL if not yet complete
    completed_at    DATETIME(6),
    -- XP awarded for this lesson completion (0 if not yet complete)
    xp_awarded      INT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    -- One progress row per learner per lesson
    UNIQUE KEY uq_user_lesson (user_id, lesson_id),
    CONSTRAINT fk_ulp_user
        FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE RESTRICT,
    CONSTRAINT fk_ulp_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE RESTRICT
);

CREATE INDEX idx_ulp_user_id    ON user_lesson_progress (user_id);
CREATE INDEX idx_ulp_lesson_id  ON user_lesson_progress (lesson_id);
CREATE INDEX idx_ulp_status     ON user_lesson_progress (user_id, status);

-- ── user_xp ──────────────────────────────────────────────────────────────────
-- One row per learner.  Total XP accumulated across all lesson completions.
-- Created lazily when the learner first completes a lesson.

CREATE TABLE user_xp (
    user_id         BIGINT      NOT NULL,
    total_xp        INT         NOT NULL DEFAULT 0,
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user_xp_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);
