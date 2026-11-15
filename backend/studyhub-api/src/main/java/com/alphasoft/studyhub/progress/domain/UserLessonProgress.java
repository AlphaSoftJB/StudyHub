package com.alphasoft.studyhub.progress.domain;

import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.identity.domain.User;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Records a single learner's progress state for one lesson.
 *
 * <p>One row per (user, lesson) pair — enforced by the unique constraint
 * {@code uq_user_lesson}.  Created when a learner first opens or completes
 * a lesson.
 *
 * <p>DR-022: {@link LessonProgressStatus#COMPLETED} does not imply mastery.
 * Mastery is computed from exam results in a later sprint.
 */
@Entity
@Table(
    name = "user_lesson_progress",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_lesson",
        columnNames = {"user_id", "lesson_id"}
    )
)
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LessonProgressStatus status = LessonProgressStatus.IN_PROGRESS;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "xp_awarded", nullable = false)
    private int xpAwarded = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    // ── factory ──────────────────────────────────────────────────────────────

    public static UserLessonProgress startFor(User user, Lesson lesson) {
        var p = new UserLessonProgress();
        p.user = user;
        p.lesson = lesson;
        p.startedAt = Instant.now();
        return p;
    }

    // ── domain operations ────────────────────────────────────────────────────

    /**
     * Transitions the progress record to COMPLETED and records the XP awarded.
     * Idempotent — repeated calls do not re-award XP.
     *
     * @param xp XP to award for this completion (ignored if already completed)
     */
    public void complete(int xp) {
        if (status == LessonProgressStatus.COMPLETED) {
            return; // idempotent
        }
        status = LessonProgressStatus.COMPLETED;
        completedAt = Instant.now();
        xpAwarded = xp;
        updatedAt = Instant.now();
    }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Lesson getLesson() { return lesson; }
    public LessonProgressStatus getStatus() { return status; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public int getXpAwarded() { return xpAwarded; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
