package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Structured lab entry associated with a LAB-type lesson.
 *
 * <p>Steps are stored as a JSON array of strings in {@code stepsJson}.
 * The lab belongs to exactly one lesson and is deleted when the lesson is deleted
 * (ON DELETE RESTRICT in SQL — see V9 — so the lesson must be deprecated first).
 */
@Entity
@Table(name = "labs")
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(length = 100)
    private String tool;

    // 1 (easiest) to 5 (hardest)
    @Column(nullable = false)
    private int difficulty = 2;

    @Column(columnDefinition = "TEXT")
    private String objective;

    // JSON array of step strings: ["Step 1: ...", "Step 2: ..."]
    @Column(name = "steps_json", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String stepsJson = "[]";

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Lab() {}

    public Lab(Lesson lesson, String title, String tool, int difficulty, String objective, String stepsJson) {
        this.lesson     = lesson;
        this.title      = title;
        this.tool       = tool;
        this.difficulty = difficulty;
        this.objective  = objective;
        this.stepsJson  = stepsJson != null ? stepsJson : "[]";
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long    getId()               { return id; }
    public Lesson  getLesson()           { return lesson; }
    public String  getTitle()            { return title; }
    public String  getTool()             { return tool; }
    public int     getDifficulty()       { return difficulty; }
    public String  getObjective()        { return objective; }
    public String  getStepsJson()        { return stepsJson; }
    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public Instant getCreatedAt()        { return createdAt; }
    public Instant getUpdatedAt()        { return updatedAt; }

    public void setTitle(String title)                   { this.title            = title; }
    public void setTool(String tool)                     { this.tool             = tool; }
    public void setDifficulty(int difficulty)            { this.difficulty       = difficulty; }
    public void setObjective(String objective)           { this.objective        = objective; }
    public void setStepsJson(String stepsJson)           { this.stepsJson        = stepsJson; }
    public void setEstimatedMinutes(Integer minutes)     { this.estimatedMinutes = minutes; }
}
