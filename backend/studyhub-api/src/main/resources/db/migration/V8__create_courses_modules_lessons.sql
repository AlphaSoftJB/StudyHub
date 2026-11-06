-- V8: Create courses, modules, and lessons tables (Sprint 3 — Curriculum domain)
--
-- Hierarchy: Discipline → Course → Module → Lesson
--
-- DR-012 content lifecycle enforced at the lessons level:
--   content_status: DRAFT | IN_REVIEW | APPROVED | PUBLISHED | DEPRECATED
--   Only PUBLISHED lessons are visible to learners (enforced at the service layer).
--
-- lesson_type values: NOTES | VIDEO | LAB | QUIZ | CHEATSHEET
--   NOTES      — structured text/markdown lesson
--   VIDEO      — video lesson (content_body holds video URL/embed ref)
--   LAB        — hands-on lab (content_body holds structured lab JSON)
--   QUIZ       — embedded quiz lesson (content references question bank)
--   CHEATSHEET — reference card / summary sheet
--
-- DR-015-NATIVE: all columns are API-first compatible.
--   content_body stores markdown text. Lab structured data stored in labs table (V9).

-- ── courses ──────────────────────────────────────────────────────────────────

CREATE TABLE courses (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    discipline_id   BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    slug            VARCHAR(200) NOT NULL,
    description     TEXT,
    sequence_order  INT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uq_courses_discipline_slug UNIQUE (discipline_id, slug),
    CONSTRAINT fk_courses_discipline
        FOREIGN KEY (discipline_id) REFERENCES disciplines(id) ON DELETE RESTRICT
);

CREATE INDEX idx_courses_discipline_id    ON courses (discipline_id);
CREATE INDEX idx_courses_sequence_order   ON courses (discipline_id, sequence_order);

-- ── modules ──────────────────────────────────────────────────────────────────

CREATE TABLE modules (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    course_id       BIGINT       NOT NULL,
    title           VARCHAR(200) NOT NULL,
    sequence_order  INT          NOT NULL DEFAULT 0,
    created_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_modules_course
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT
);

CREATE INDEX idx_modules_course_id      ON modules (course_id);
CREATE INDEX idx_modules_sequence_order ON modules (course_id, sequence_order);

-- ── lessons ──────────────────────────────────────────────────────────────────

CREATE TABLE lessons (
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    module_id       BIGINT        NOT NULL,
    title           VARCHAR(300)  NOT NULL,
    -- lesson_type: NOTES | VIDEO | LAB | QUIZ | CHEATSHEET
    lesson_type     VARCHAR(20)   NOT NULL DEFAULT 'NOTES',
    -- content_status: DRAFT | IN_REVIEW | APPROVED | PUBLISHED | DEPRECATED
    content_status  VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    -- Markdown or structured text. LAB-type lessons have a companion labs row.
    content_body    MEDIUMTEXT,
    sequence_order  INT           NOT NULL DEFAULT 0,
    -- Audit columns for content lifecycle
    created_by      BIGINT,
    reviewed_by     BIGINT,
    approved_by     BIGINT,
    published_at    DATETIME(6),
    deprecated_at   DATETIME(6),
    created_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT chk_lesson_type   CHECK (lesson_type   IN ('NOTES','VIDEO','LAB','QUIZ','CHEATSHEET')),
    CONSTRAINT chk_content_status CHECK (content_status IN ('DRAFT','IN_REVIEW','APPROVED','PUBLISHED','DEPRECATED')),
    CONSTRAINT fk_lessons_module
        FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE RESTRICT
);

CREATE INDEX idx_lessons_module_id      ON lessons (module_id);
CREATE INDEX idx_lessons_content_status ON lessons (content_status);
CREATE INDEX idx_lessons_sequence_order ON lessons (module_id, sequence_order);
-- Partial-equivalent: learner query hits this index for fast PUBLISHED lookups
CREATE INDEX idx_lessons_published_at   ON lessons (published_at);
