package com.alphasoft.studyhub.curriculum;

import com.alphasoft.studyhub.curriculum.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sprint 3 — Flyway migration integration tests for curriculum domain (V7–V10).
 *
 * Verifies:
 *  - V7 (disciplines), V8 (courses, modules, lessons),
 *    V9 (questions, labs), V10 (exams, exam_question_pool, exam_attempts)
 *    all apply cleanly to the H2 test database.
 *  - All curriculum JPA repositories are wired correctly.
 *
 * The context-load assertion is intentional: a failed Flyway migration or
 * entity mapping mismatch raises an exception before any @Test runs.
 */
@SpringBootTest
@ActiveProfiles("test")
class CurriculumMigrationTest {

    @Autowired private DisciplineRepository   disciplineRepository;
    @Autowired private CourseRepository       courseRepository;
    @Autowired private ModuleRepository       moduleRepository;
    @Autowired private LessonRepository       lessonRepository;
    @Autowired private QuestionRepository     questionRepository;
    @Autowired private ExamRepository         examRepository;
    @Autowired private ExamAttemptRepository  examAttemptRepository;

    @Test
    @DisplayName("V7–V10 migrations apply cleanly — application context loads")
    void curriculum_migrations_apply() {
        // Context load succeeds → Flyway ran V7–V10 and Hibernate ddl-auto=validate passed
        assertThat(disciplineRepository).isNotNull();
        assertThat(courseRepository).isNotNull();
        assertThat(moduleRepository).isNotNull();
        assertThat(lessonRepository).isNotNull();
        assertThat(questionRepository).isNotNull();
        assertThat(examRepository).isNotNull();
        assertThat(examAttemptRepository).isNotNull();
    }

    @Test
    @DisplayName("disciplines table is empty on fresh schema (no seed data yet)")
    void disciplines_table_starts_empty() {
        assertThat(disciplineRepository.count()).isZero();
    }

    @Test
    @DisplayName("courses table is empty on fresh schema")
    void courses_table_starts_empty() {
        assertThat(courseRepository.count()).isZero();
    }

    @Test
    @DisplayName("lessons table is empty on fresh schema")
    void lessons_table_starts_empty() {
        assertThat(lessonRepository.count()).isZero();
    }

    @Test
    @DisplayName("questions table is empty on fresh schema")
    void questions_table_starts_empty() {
        assertThat(questionRepository.count()).isZero();
    }

    @Test
    @DisplayName("exams table is empty on fresh schema")
    void exams_table_starts_empty() {
        assertThat(examRepository.count()).isZero();
    }
}
