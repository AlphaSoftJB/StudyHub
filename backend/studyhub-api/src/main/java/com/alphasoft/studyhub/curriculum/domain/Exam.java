package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Exam definition — belongs to a course and draws questions from the question pool.
 *
 * <p>The actual questions available to this exam are referenced via
 * {@code ExamQuestionPool} (the {@code exam_question_pool} junction table).
 * At runtime, the engine selects {@code questionCount} questions from that pool,
 * respecting difficulty distribution when enough questions are available.
 */
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    // How many questions to draw per attempt from the pool
    @Column(name = "question_count", nullable = false)
    private int questionCount = 65;

    // null = untimed
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    // Percentage (0–100) required to pass
    @Column(name = "pass_score", nullable = false)
    private int passScore = 70;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Pool of questions eligible for this exam
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "exam_question_pool",
        joinColumns        = @JoinColumn(name = "exam_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questionPool = new ArrayList<>();

    protected Exam() {}

    public Exam(Course course, String title, int questionCount, Integer timeLimitMinutes, int passScore) {
        this.course             = course;
        this.title              = title;
        this.questionCount      = questionCount;
        this.timeLimitMinutes   = timeLimitMinutes;
        this.passScore          = passScore;
        this.active             = true;
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long            getId()               { return id; }
    public Course          getCourse()           { return course; }
    public String          getTitle()            { return title; }
    public int             getQuestionCount()    { return questionCount; }
    public Integer         getTimeLimitMinutes() { return timeLimitMinutes; }
    public int             getPassScore()        { return passScore; }
    public boolean         isActive()            { return active; }
    public Instant         getCreatedAt()        { return createdAt; }
    public Instant         getUpdatedAt()        { return updatedAt; }
    public List<Question>  getQuestionPool()     { return questionPool; }

    public void setTitle(String title)                   { this.title            = title; }
    public void setQuestionCount(int count)              { this.questionCount    = count; }
    public void setTimeLimitMinutes(Integer minutes)     { this.timeLimitMinutes = minutes; }
    public void setPassScore(int passScore)              { this.passScore        = passScore; }
    public void setActive(boolean active)                { this.active           = active; }
}
