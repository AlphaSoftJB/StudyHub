package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Unified question bank entry.
 *
 * <p>Questions are linked to a lesson (for inline quizzes) or unlinked (exam pool).
 * A question can appear in multiple exams without duplication.
 *
 * <p>DR-013 (mastery): {@code domain} and {@code topicTag} feed the mastery algorithm.
 * {@code difficulty} (1–5) is used for difficulty-weighted scoring.
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable — null means standalone pool question (not tied to a specific lesson)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 10)
    private QuestionType questionType = QuestionType.MCQ;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    // JSON array of option strings: ["A) ...", "B) ...", ...]
    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;

    // For MCQ: 0-based index of the correct answer
    @Column(name = "correct_answer")
    private Integer correctAnswer;

    // For MULTI: JSON array of correct 0-based indices: [0, 2]
    @Column(name = "correct_answers_json", columnDefinition = "TEXT")
    private String correctAnswersJson;

    // For PBQ: JSON array of task objects
    @Column(name = "tasks_json", columnDefinition = "MEDIUMTEXT")
    private String tasksJson;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(length = 100)
    private String domain;

    @Column(name = "topic_tag", length = 100)
    private String topicTag;

    // 1 (easiest) to 5 (hardest)
    @Column(nullable = false)
    private int difficulty = 2;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status", nullable = false, length = 20)
    private ContentStatus contentStatus = ContentStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Question() {}

    public Question(QuestionType questionType, String body, String domain, String topicTag, int difficulty) {
        this.questionType = questionType;
        this.body         = body;
        this.domain       = domain;
        this.topicTag     = topicTag;
        this.difficulty   = difficulty;
        this.contentStatus = ContentStatus.DRAFT;
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long         getId()                 { return id; }
    public Lesson       getLesson()             { return lesson; }
    public QuestionType getQuestionType()       { return questionType; }
    public String       getBody()               { return body; }
    public String       getOptionsJson()        { return optionsJson; }
    public Integer      getCorrectAnswer()      { return correctAnswer; }
    public String       getCorrectAnswersJson() { return correctAnswersJson; }
    public String       getTasksJson()          { return tasksJson; }
    public String       getExplanation()        { return explanation; }
    public String       getDomain()             { return domain; }
    public String       getTopicTag()           { return topicTag; }
    public int          getDifficulty()         { return difficulty; }
    public ContentStatus getContentStatus()     { return contentStatus; }
    public Instant      getCreatedAt()          { return createdAt; }
    public Instant      getUpdatedAt()          { return updatedAt; }

    public void setLesson(Lesson lesson)                      { this.lesson              = lesson; }
    public void setOptionsJson(String json)                   { this.optionsJson          = json; }
    public void setCorrectAnswer(Integer idx)                 { this.correctAnswer        = idx; }
    public void setCorrectAnswersJson(String json)            { this.correctAnswersJson   = json; }
    public void setTasksJson(String json)                     { this.tasksJson            = json; }
    public void setExplanation(String explanation)            { this.explanation          = explanation; }
    public void setContentStatus(ContentStatus contentStatus) { this.contentStatus        = contentStatus; }
}
