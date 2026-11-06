-- V9: Create questions and labs tables (Sprint 3 — Assessment domain)
--
-- questions: unified question bank, tagged by domain/topic.
--   Questions can be linked to a lesson (lesson_id NOT NULL) for inline quizzes,
--   or unlinked (lesson_id NULL) for standalone exam pools.
--   A question can appear in multiple exams without duplication.
--
-- question_type values: MCQ | MULTI | PBQ
--   MCQ   — single correct answer, options_json array
--   MULTI — multiple correct answers, correct_answers_json array of indices
--   PBQ   — performance-based question, tasks_json array of sub-tasks
--
-- difficulty: 1 (easiest) to 5 (hardest)
-- domain: free-text domain tag (e.g. "Network Security", "Machine Learning")
-- topic_tag: more specific tag for mastery algorithm (DR-013)
--
-- labs: structured hands-on lab content.
--   Linked to a lesson (lesson_id) with lesson_type=LAB.
--   steps_json: ordered array of step objects
--   tool: primary tool used (e.g. "Wireshark", "Python + scikit-learn")
--
-- DR-015-NATIVE: all fields are API-first (no web-only contracts).

-- ── questions ────────────────────────────────────────────────────────────────

CREATE TABLE questions (
    id                   BIGINT      NOT NULL AUTO_INCREMENT,
    -- Optional: null means standalone pool question, not tied to a specific lesson
    lesson_id            BIGINT,
    -- question_type: MCQ | MULTI | PBQ
    question_type        VARCHAR(10) NOT NULL DEFAULT 'MCQ',
    body                 TEXT        NOT NULL,
    -- For MCQ/MULTI: JSON array of option strings, e.g. ["A", "B", "C", "D"]
    options_json         TEXT,
    -- For MCQ: 0-based index of correct answer (integer)
    correct_answer       INT,
    -- For MULTI: JSON array of correct 0-based indices, e.g. [0, 2]
    correct_answers_json TEXT,
    -- For PBQ: JSON array of task objects
    --   [{instruction, options, answer, explanation}, ...]
    tasks_json           MEDIUMTEXT,
    explanation          TEXT,
    domain               VARCHAR(100),
    topic_tag            VARCHAR(100),
    -- difficulty 1 (easiest) to 5 (hardest)
    difficulty           TINYINT     NOT NULL DEFAULT 2,
    -- content_status mirrors lessons lifecycle (DR-012); questions start DRAFT
    content_status       VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT chk_question_type    CHECK (question_type   IN ('MCQ','MULTI','PBQ')),
    CONSTRAINT chk_q_content_status CHECK (content_status  IN ('DRAFT','IN_REVIEW','APPROVED','PUBLISHED','DEPRECATED')),
    CONSTRAINT chk_difficulty       CHECK (difficulty BETWEEN 1 AND 5),
    CONSTRAINT fk_questions_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE SET NULL
);

CREATE INDEX idx_questions_lesson_id      ON questions (lesson_id);
CREATE INDEX idx_questions_domain         ON questions (domain);
CREATE INDEX idx_questions_topic_tag      ON questions (topic_tag);
CREATE INDEX idx_questions_content_status ON questions (content_status);
CREATE INDEX idx_questions_difficulty     ON questions (difficulty);

-- ── labs ─────────────────────────────────────────────────────────────────────

CREATE TABLE labs (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    -- lesson_id must reference a lesson with lesson_type='LAB'
    lesson_id       BIGINT       NOT NULL,
    tool            VARCHAR(100),
    -- difficulty 1 (easiest) to 5 (hardest)
    difficulty      TINYINT      NOT NULL DEFAULT 2,
    objective       TEXT,
    -- JSON array of step objects: [{type: "instruction"|"command"|"code", content: "..."}]
    steps_json      MEDIUMTEXT   NOT NULL,
    -- Optional: estimated time in minutes
    estimated_minutes INT,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT chk_lab_difficulty CHECK (difficulty BETWEEN 1 AND 5),
    CONSTRAINT fk_labs_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE RESTRICT
);

CREATE INDEX idx_labs_lesson_id ON labs (lesson_id);
CREATE INDEX idx_labs_tool      ON labs (tool);
