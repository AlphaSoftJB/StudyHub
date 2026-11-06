package com.alphasoft.studyhub.curriculum.service;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.curriculum.domain.Question;
import com.alphasoft.studyhub.curriculum.repository.LessonRepository;
import com.alphasoft.studyhub.curriculum.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DR-012: Content lifecycle transitions.
 *
 * <p>All status changes go through this service — never direct field mutation
 * from outside the domain. The service loads the entity, delegates the
 * transition to the domain method (which validates the state machine), and
 * persists.
 *
 * <p>AI-generated content control: AI-generated lessons enter as DRAFT and
 * require a human reviewer to advance to IN_REVIEW → APPROVED → PUBLISHED.
 * This service does not automatically advance status — every transition
 * requires an explicit call with an authenticated actor ID.
 */
@Service
@Transactional
public class ContentLifecycleService {

    private final LessonRepository   lessonRepository;
    private final QuestionRepository questionRepository;

    public ContentLifecycleService(LessonRepository lessonRepository,
                                   QuestionRepository questionRepository) {
        this.lessonRepository   = lessonRepository;
        this.questionRepository = questionRepository;
    }

    // ── Lesson transitions ───────────────────────────────────────────────────

    /**
     * Transition a lesson to the given status.
     *
     * @param lessonId   the lesson to transition
     * @param next       target status
     * @param actorUserId the authenticated user performing the action
     * @return updated lesson
     * @throws IllegalArgumentException if the lesson does not exist
     * @throws IllegalStateException    if the transition is not allowed by DR-012
     */
    public Lesson transitionLesson(Long lessonId, ContentStatus next, Long actorUserId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
        lesson.transitionTo(next, actorUserId);
        return lessonRepository.save(lesson);
    }

    /**
     * Submit a lesson for review (DRAFT → IN_REVIEW).
     */
    public Lesson submitLessonForReview(Long lessonId, Long actorUserId) {
        return transitionLesson(lessonId, ContentStatus.IN_REVIEW, actorUserId);
    }

    /**
     * Approve a lesson (IN_REVIEW → APPROVED).
     */
    public Lesson approveLesson(Long lessonId, Long actorUserId) {
        return transitionLesson(lessonId, ContentStatus.APPROVED, actorUserId);
    }

    /**
     * Publish an approved lesson (APPROVED → PUBLISHED).
     */
    public Lesson publishLesson(Long lessonId, Long actorUserId) {
        return transitionLesson(lessonId, ContentStatus.PUBLISHED, actorUserId);
    }

    /**
     * Deprecate a published lesson (PUBLISHED → DEPRECATED).
     */
    public Lesson deprecateLesson(Long lessonId, Long actorUserId) {
        return transitionLesson(lessonId, ContentStatus.DEPRECATED, actorUserId);
    }

    // ── Question transitions ─────────────────────────────────────────────────

    /**
     * Directly publish a question (DRAFT → PUBLISHED via APPROVED shortcut).
     * Questions don't have a full review audit trail — simpler path.
     */
    public Question publishQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        // Questions: DRAFT → APPROVED → PUBLISHED
        if (question.getContentStatus() == ContentStatus.DRAFT) {
            question.setContentStatus(ContentStatus.APPROVED);
        }
        if (question.getContentStatus() == ContentStatus.APPROVED) {
            question.setContentStatus(ContentStatus.PUBLISHED);
        }
        return questionRepository.save(question);
    }

    public Question setQuestionStatus(Long questionId, ContentStatus status) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        question.setContentStatus(status);
        return questionRepository.save(question);
    }
}
