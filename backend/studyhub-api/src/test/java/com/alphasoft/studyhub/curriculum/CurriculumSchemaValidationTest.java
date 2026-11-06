package com.alphasoft.studyhub.curriculum;

import com.alphasoft.studyhub.curriculum.domain.*;
import com.alphasoft.studyhub.curriculum.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Sprint 3 — Curriculum entity mapping and DR-012 lifecycle tests.
 *
 * Verifies:
 *  - Full Discipline → Course → Module → Lesson hierarchy can be persisted
 *  - DR-012 lifecycle: DRAFT → APPROVED → PUBLISHED transitions correctly
 *  - DR-012: PUBLISHED → DRAFT is an illegal transition
 *  - Only PUBLISHED lessons are returned by learner-facing query methods
 *  - ContentStatus.isLearnerVisible() only true for PUBLISHED
 *  - Question entity persists with nullable lesson FK (standalone pool entry)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CurriculumSchemaValidationTest {

    @Autowired private DisciplineRepository disciplineRepository;
    @Autowired private CourseRepository     courseRepository;
    @Autowired private ModuleRepository     moduleRepository;
    @Autowired private LessonRepository     lessonRepository;
    @Autowired private QuestionRepository   questionRepository;

    // ── Hierarchy persistence ─────────────────────────────────────────────────

    @Test
    @DisplayName("Discipline entity persists with active=true default and audit timestamps")
    void discipline_entity_persists() {
        Discipline d = disciplineRepository.save(
            new Discipline("Cybersecurity", "cyber", "Security fundamentals", "🔒"));

        assertThat(d.getId()).isPositive();
        assertThat(d.getSlug()).isEqualTo("cyber");
        assertThat(d.isActive()).isTrue();
        assertThat(d.getCreatedAt()).isNotNull();
        assertThat(d.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Full hierarchy Discipline → Course → Module → Lesson persists correctly")
    void full_hierarchy_persists() {
        Discipline d = disciplineRepository.save(
            new Discipline("Cybersecurity", "cyber-hierarchy-test", null, null));

        Course c = courseRepository.save(
            new Course(d, "Security+", "secplus", "CompTIA SY0-701"));

        Module m = moduleRepository.save(new Module(c, "Module 1 - Threats & Attacks"));

        Lesson l = lessonRepository.save(
            new Lesson(m, "Lesson 1.1 - Malware Types", LessonType.NOTES,
                       "# Malware Types\n\nContent here..."));

        lessonRepository.flush();

        assertThat(l.getId()).isPositive();
        assertThat(l.getModule().getId()).isEqualTo(m.getId());
        assertThat(l.getTitle()).isEqualTo("Lesson 1.1 - Malware Types");
        assertThat(l.getLessonType()).isEqualTo(LessonType.NOTES);
        assertThat(l.getContentStatus()).isEqualTo(ContentStatus.DRAFT);
        assertThat(l.getPublishedAt()).isNull();
        assertThat(l.getCreatedAt()).isNotNull();
    }

    // ── DR-012 lifecycle state machine ────────────────────────────────────────

    @Test
    @DisplayName("DR-012: DRAFT → APPROVED → PUBLISHED transitions apply and set publishedAt")
    void dr012_draft_to_published_lifecycle() {
        Discipline d = disciplineRepository.save(
            new Discipline("Test", "test-lifecycle-1", null, null));
        Course c = courseRepository.save(new Course(d, "Course A", "course-a-lc1", null));
        Module m = moduleRepository.save(new Module(c, "Module 1"));
        Lesson l = lessonRepository.save(
            new Lesson(m, "Lifecycle Test Lesson", LessonType.NOTES, "content"));

        assertThat(l.getContentStatus()).isEqualTo(ContentStatus.DRAFT);

        // DRAFT → APPROVED (skip IN_REVIEW — allowed per canTransitionTo)
        l.transitionTo(ContentStatus.APPROVED, 1L);
        assertThat(l.getContentStatus()).isEqualTo(ContentStatus.APPROVED);
        assertThat(l.getApprovedBy()).isEqualTo(1L);
        assertThat(l.getPublishedAt()).isNull();

        // APPROVED → PUBLISHED
        l.transitionTo(ContentStatus.PUBLISHED, 1L);
        assertThat(l.getContentStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(l.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("DR-012: PUBLISHED → DRAFT is an illegal transition — throws IllegalStateException")
    void dr012_published_to_draft_is_illegal() {
        Discipline d = disciplineRepository.save(
            new Discipline("Test", "test-lifecycle-2", null, null));
        Course c = courseRepository.save(new Course(d, "Course B", "course-b-lc2", null));
        Module m = moduleRepository.save(new Module(c, "Module 1"));
        Lesson l = lessonRepository.save(
            new Lesson(m, "Published Lesson", LessonType.NOTES, "content"));

        l.transitionTo(ContentStatus.APPROVED, 1L);
        l.transitionTo(ContentStatus.PUBLISHED, 1L);

        assertThatThrownBy(() -> l.transitionTo(ContentStatus.DRAFT, 1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PUBLISHED")
            .hasMessageContaining("DRAFT");
    }

    @Test
    @DisplayName("DR-012: DEPRECATED → any state is an illegal transition")
    void dr012_deprecated_is_terminal() {
        Discipline d = disciplineRepository.save(
            new Discipline("Test", "test-lifecycle-3", null, null));
        Course c = courseRepository.save(new Course(d, "Course C", "course-c-lc3", null));
        Module m = moduleRepository.save(new Module(c, "Module 1"));
        Lesson l = lessonRepository.save(
            new Lesson(m, "Deprecated Lesson", LessonType.NOTES, "content"));

        l.transitionTo(ContentStatus.APPROVED, 1L);
        l.transitionTo(ContentStatus.PUBLISHED, 1L);
        l.transitionTo(ContentStatus.DEPRECATED, 1L);

        assertThat(l.getDeprecatedAt()).isNotNull();
        assertThatThrownBy(() -> l.transitionTo(ContentStatus.DRAFT, 1L))
            .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> l.transitionTo(ContentStatus.PUBLISHED, 1L))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("ContentStatus.isLearnerVisible() — only PUBLISHED returns true")
    void content_status_learner_visibility() {
        assertThat(ContentStatus.DRAFT.isLearnerVisible()).isFalse();
        assertThat(ContentStatus.IN_REVIEW.isLearnerVisible()).isFalse();
        assertThat(ContentStatus.APPROVED.isLearnerVisible()).isFalse();
        assertThat(ContentStatus.PUBLISHED.isLearnerVisible()).isTrue();
        assertThat(ContentStatus.DEPRECATED.isLearnerVisible()).isFalse();
    }

    // ── Learner-facing repository query ───────────────────────────────────────

    @Test
    @DisplayName("findPublishedByModuleId returns only PUBLISHED lessons, not DRAFT")
    void find_published_by_module_id_excludes_draft() {
        Discipline d = disciplineRepository.save(
            new Discipline("Test", "test-query-vis", null, null));
        Course c = courseRepository.save(new Course(d, "Course Q", "course-q-vis", null));
        Module m = moduleRepository.save(new Module(c, "Module Q"));

        // PUBLISHED lesson
        Lesson published = new Lesson(m, "Published", LessonType.NOTES, "published content");
        published = lessonRepository.save(published);
        published.transitionTo(ContentStatus.APPROVED, 1L);
        published.transitionTo(ContentStatus.PUBLISHED, 1L);
        lessonRepository.save(published);

        // DRAFT lesson (should NOT appear in learner queries)
        lessonRepository.save(new Lesson(m, "Draft", LessonType.NOTES, "draft content"));

        lessonRepository.flush();

        List<Lesson> visible = lessonRepository.findPublishedByModuleId(m.getId());
        assertThat(visible).hasSize(1);
        assertThat(visible.get(0).getTitle()).isEqualTo("Published");
    }

    // ── Question standalone pool ───────────────────────────────────────────────

    @Test
    @DisplayName("Question can be persisted without a Lesson FK (standalone pool entry)")
    void question_persists_without_lesson_fk() {
        Question q = questionRepository.save(
            new Question(QuestionType.MCQ,
                         "What OSI layer does IP operate at?",
                         "cyber",
                         "osi-model",
                         2));

        assertThat(q.getId()).isPositive();
        assertThat(q.getLesson()).isNull();
        assertThat(q.getDomain()).isEqualTo("cyber");
        assertThat(q.getTopicTag()).isEqualTo("osi-model");
        assertThat(q.getDifficulty()).isEqualTo(2);
        assertThat(q.getContentStatus()).isEqualTo(ContentStatus.DRAFT);
    }
}
