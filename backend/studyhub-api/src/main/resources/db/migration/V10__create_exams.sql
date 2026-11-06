-- V10: Create exams and exam_attempts tables (Sprint 3 — Assessment domain)
--
-- exams: certification practice exam definitions.
--   Linked to a course (course_id). Questions drawn from the shared question bank.
--   exam_question_pool: junction table linking exams to their question pool.
--   At exam start time, the backend draws question_count random questions from the pool.
--
-- exam_attempts: records each learner's attempt at an exam.
--   answers_json: the learner's submitted answers (array indexed by question order).
--   score: 0-100 percentage correct.
--   time_limit_minutes: NULL = no limit (self-paced); > 0 = timed exam.
--
-- DR-013 (mastery): mastery is computed from exam_attempts + question metadata
-- in the mastery algorithm sprint (Sprint 7-8). Tables here are the data source.
--
-- DR-015-NATIVE: pagination-ready endpoints from day one.

-- ── exams ────────────────────────────────────────────────────────────────────

CREATE TABLE exams (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    course_id           BIGINT       NOT NULL,
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    -- Total questions drawn at exam start (random selection from pool)
    question_count      INT          NOT NULL DEFAULT 100,
    -- NULL = no time limit; >0 = minutes
    time_limit_minutes  INT,
    -- Pass threshold as percentage (e.g. 70 = 70%)
    pass_score          INT          NOT NULL DEFAULT 70,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_exams_course
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT
);

CREATE INDEX idx_exams_course_id ON exams (course_id);
CREATE INDEX idx_exams_active    ON exams (active);

-- ── exam_question_pool ───────────────────────────────────────────────────────
-- Junction: which questions are eligible for a given exam.
-- At exam start, `question_count` questions are randomly sampled from this pool.

CREATE TABLE exam_question_pool (
    exam_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    PRIMARY KEY (exam_id, question_id),
    CONSTRAINT fk_eqpool_exam
        FOREIGN KEY (exam_id)     REFERENCES exams(id)     ON DELETE CASCADE,
    CONSTRAINT fk_eqpool_question
        FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX idx_exam_question_pool_question ON exam_question_pool (question_id);

-- ── exam_attempts ────────────────────────────────────────────────────────────

CREATE TABLE exam_attempts (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL,
    exam_id         BIGINT      NOT NULL,
    started_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    completed_at    DATETIME(6),
    -- Score as 0-100 integer percentage; NULL if not yet completed
    score           INT,
    -- passed: NULL until scored; TRUE/FALSE after completion
    passed          BOOLEAN,
    -- JSON: ordered array of submitted answers matching the drawn question set
    -- [{question_id, answer_index, answer_indices, is_correct}, ...]
    answers_json    MEDIUMTEXT,
    -- JSON: the ordered list of question IDs drawn for this attempt
    -- (needed to reconstruct the exam for review)
    question_ids_json TEXT,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_attempts_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_attempts_exam
        FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE RESTRICT
);

CREATE INDEX idx_exam_attempts_user_id    ON exam_attempts (user_id);
CREATE INDEX idx_exam_attempts_exam_id    ON exam_attempts (exam_id);
CREATE INDEX idx_exam_attempts_user_exam  ON exam_attempts (user_id, exam_id);
CREATE INDEX idx_exam_attempts_started_at ON exam_attempts (started_at);
