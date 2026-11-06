package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * A single lesson within a module.
 *
 * <p>DR-012: Only {@link ContentStatus#PUBLISHED} lessons are visible to learners.
 * All lifecycle transitions go through {@link com.alphasoft.studyhub.curriculum.service.ContentLifecycleService}.
 *
 * <p>Lesson types (DR-012):
 * <ul>
 *   <li>NOTES      — markdown content in content_body</li>
 *   <li>VIDEO      — URL/embed ref in content_body</li>
 *   <li>LAB        — structured lab content; companion {@link Lab} row holds steps_json</li>
 *   <li>QUIZ       — embedded quiz; questions drawn from question bank</li>
 *   <li>CHEATSHEET — reference card markdown in content_body</li>
 * </ul>
 */
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false, length = 300)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false, length = 20)
    private LessonType lessonType = LessonType.NOTES;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status", nullable = false, length = 20)
    private ContentStatus contentStatus = ContentStatus.DRAFT;

    // MEDIUMTEXT — up to 16MB; sufficient for 2000-word lesson notes
    @Column(name = "content_body", columnDefinition = "MEDIUMTEXT")
    private String contentBody;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder = 0;

    // ── Content lifecycle audit columns ──────────────────────────────────────

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "deprecated_at")
    private Instant deprecatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Lesson() {}

    public Lesson(Module module, String title, LessonType lessonType, String contentBody) {
        this.module      = module;
        this.title       = title;
        this.lessonType  = lessonType;
        this.contentBody = contentBody;
        this.contentStatus = ContentStatus.DRAFT;
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long          getId()            { return id; }
    public Module        getModule()        { return module; }
    public String        getTitle()         { return title; }
    public LessonType    getLessonType()    { return lessonType; }
    public ContentStatus getContentStatus() { return contentStatus; }
    public String        getContentBody()   { return contentBody; }
    public int           getSequenceOrder() { return sequenceOrder; }
    public Long          getCreatedBy()     { return createdBy; }
    public Long          getReviewedBy()    { return reviewedBy; }
    public Long          getApprovedBy()    { return approvedBy; }
    public Instant       getPublishedAt()   { return publishedAt; }
    public Instant       getDeprecatedAt()  { return deprecatedAt; }
    public Instant       getCreatedAt()     { return createdAt; }
    public Instant       getUpdatedAt()     { return updatedAt; }

    public void setTitle(String title)                       { this.title         = title; }
    public void setContentBody(String contentBody)           { this.contentBody   = contentBody; }
    public void setSequenceOrder(int order)                  { this.sequenceOrder = order; }
    public void setCreatedBy(Long userId)                    { this.createdBy     = userId; }

    // ── lifecycle transitions (use ContentLifecycleService for validation) ──

    public void transitionTo(ContentStatus next, Long actorUserId) {
        if (!this.contentStatus.canTransitionTo(next)) {
            throw new IllegalStateException(
                "Cannot transition lesson %d from %s to %s".formatted(id, contentStatus, next));
        }
        this.contentStatus = next;
        switch (next) {
            case IN_REVIEW  -> this.reviewedBy    = actorUserId;
            case APPROVED   -> this.approvedBy    = actorUserId;
            case PUBLISHED  -> this.publishedAt   = Instant.now();
            case DEPRECATED -> this.deprecatedAt  = Instant.now();
            default -> { /* DRAFT has no audit column */ }
        }
    }
}
