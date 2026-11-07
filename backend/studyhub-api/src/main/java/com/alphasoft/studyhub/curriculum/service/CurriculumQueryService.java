package com.alphasoft.studyhub.curriculum.service;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import com.alphasoft.studyhub.curriculum.domain.Course;
import com.alphasoft.studyhub.curriculum.domain.Discipline;
import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.curriculum.domain.Question;
import com.alphasoft.studyhub.curriculum.domain.Module;
import com.alphasoft.studyhub.curriculum.repository.CourseRepository;
import com.alphasoft.studyhub.curriculum.repository.DisciplineRepository;
import com.alphasoft.studyhub.curriculum.repository.LessonRepository;
import com.alphasoft.studyhub.curriculum.repository.ModuleRepository;
import com.alphasoft.studyhub.curriculum.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Read-only curriculum queries — used by the content API controllers.
 *
 * <p>All learner-facing methods enforce {@link ContentStatus#PUBLISHED} filtering.
 * Admin-facing methods are marked with {@code // admin} and are called from
 * admin-only endpoints (authorization enforced at the controller layer).
 */
@Service
@Transactional(readOnly = true)
public class CurriculumQueryService {

    private final DisciplineRepository disciplineRepository;
    private final CourseRepository     courseRepository;
    private final ModuleRepository     moduleRepository;
    private final LessonRepository     lessonRepository;
    private final QuestionRepository   questionRepository;

    public CurriculumQueryService(DisciplineRepository disciplineRepository,
                                  CourseRepository courseRepository,
                                  ModuleRepository moduleRepository,
                                  LessonRepository lessonRepository,
                                  QuestionRepository questionRepository) {
        this.disciplineRepository = disciplineRepository;
        this.courseRepository     = courseRepository;
        this.moduleRepository     = moduleRepository;
        this.lessonRepository     = lessonRepository;
        this.questionRepository   = questionRepository;
    }

    // ── Disciplines ───────────────────────────────────────────────────────────

    /** Explore page: active disciplines only. */
    public List<Discipline> getActiveDisciplines() {
        return disciplineRepository.findByActiveTrueOrderBySequenceOrderAsc();
    }

    public Optional<Discipline> getDisciplineBySlug(String slug) {
        return disciplineRepository.findActiveBySlug(slug);
    }

    // admin
    public List<Discipline> getAllDisciplines() {
        return disciplineRepository.findAllByOrderBySequenceOrderAsc();
    }

    // ── Courses ───────────────────────────────────────────────────────────────

    public List<Course> getCoursesByDisciplineId(Long disciplineId) {
        return courseRepository.findByDisciplineIdOrderBySequenceOrderAsc(disciplineId);
    }

    public List<Course> getCoursesByDisciplineSlug(String slug) {
        return courseRepository.findByDisciplineSlugOrdered(slug);
    }

    public Optional<Course> getCourse(Long courseId) {
        return courseRepository.findById(courseId);
    }

    // ── Modules ───────────────────────────────────────────────────────────────

    public List<Module> getModulesByCourseId(Long courseId) {
        return moduleRepository.findByCourseIdOrderBySequenceOrderAsc(courseId);
    }

    // ── Lessons ───────────────────────────────────────────────────────────────

    /** Learner-facing: PUBLISHED lessons in a module. */
    public List<Lesson> getPublishedLessonsByModuleId(Long moduleId) {
        return lessonRepository.findPublishedByModuleId(moduleId);
    }

    /** Learner-facing: single PUBLISHED lesson. */
    public Optional<Lesson> getPublishedLesson(Long lessonId) {
        return lessonRepository.findPublishedById(lessonId);
    }

    // admin
    public Optional<Lesson> getLesson(Long lessonId) {
        return lessonRepository.findById(lessonId);
    }

    // admin
    public List<Lesson> getLessonsByStatus(ContentStatus status) {
        return lessonRepository.findByContentStatus(status);
    }

    // ── Questions ─────────────────────────────────────────────────────────────

    public List<Question> getPublishedQuestionsByLesson(Long lessonId) {
        return questionRepository.findPublishedByLessonId(lessonId);
    }

    public List<Question> getPublishedQuestionsByDomain(String domain) {
        return questionRepository.findPublishedByDomain(domain);
    }
}
