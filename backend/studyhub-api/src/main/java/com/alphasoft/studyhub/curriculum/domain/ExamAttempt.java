package com.alphasoft.studyhub.curriculum.domain;

import com.alphasoft.studyhub.identity.domain.User;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * A single learner's attempt at an exam.
 *
 * <p>Immutable after completion — no update methods. The service layer writes
 * one row per attempt and never modifies it. {@code answersJson} stores a
 * JSON object mapping question-id → selected-answer-index (or array of indices
 * for MULTI questions). {@code questionIdsJson} records the specific question
 * set drawn for this attempt to support review.
 *
 * <p>DR-013: score feeds the mastery algorithm (deferred to Sprint 7–8).
 */
@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ON DELETE RESTRICT — user accounts may not be deleted while attempts exist
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    // Raw score (number of correct answers)
    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private boolean passed = false;

    // JSON object: { "questionId": answerIndex, ... }
    @Column(name = "answers_json", columnDefinition = "MEDIUMTEXT")
    private String answersJson;

    // JSON array of question IDs drawn for this attempt: [1, 5, 23, ...]
    @Column(name = "question_ids_json", columnDefinition = "TEXT")
    private String questionIdsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ExamAttempt() {}

    public ExamAttempt(User user, Exam exam, int score, boolean passed,
                       String answersJson, String questionIdsJson) {
        this.user           = user;
        this.exam           = exam;
        this.score          = score;
        this.passed         = passed;
        this.answersJson    = answersJson;
        this.questionIdsJson = questionIdsJson;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    // ── accessors (read-only after creation) ─────────────────────────────────

    public Long    getId()              { return id; }
    public User    getUser()            { return user; }
    public Exam    getExam()            { return exam; }
    public int     getScore()           { return score; }
    public boolean isPassed()           { return passed; }
    public String  getAnswersJson()     { return answersJson; }
    public String  getQuestionIdsJson() { return questionIdsJson; }
    public Instant getCreatedAt()       { return createdAt; }
}
