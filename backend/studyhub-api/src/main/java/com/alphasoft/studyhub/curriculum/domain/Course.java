package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A course within a discipline (e.g. "CompTIA Network+", "IBM Python Fundamentals").
 */
@Entity
@Table(name = "courses",
       uniqueConstraints = @UniqueConstraint(name = "uq_courses_discipline_slug",
                                             columnNames = {"discipline_id", "slug"}))
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    private List<Module> modules = new ArrayList<>();

    protected Course() {}

    public Course(Discipline discipline, String title, String slug, String description) {
        this.discipline  = discipline;
        this.title       = title;
        this.slug        = slug;
        this.description = description;
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long        getId()            { return id; }
    public Discipline  getDiscipline()    { return discipline; }
    public String      getTitle()         { return title; }
    public String      getSlug()          { return slug; }
    public String      getDescription()   { return description; }
    public int         getSequenceOrder() { return sequenceOrder; }
    public Instant     getCreatedAt()     { return createdAt; }
    public Instant     getUpdatedAt()     { return updatedAt; }
    public List<Module> getModules()      { return modules; }

    public void setSequenceOrder(int order)        { this.sequenceOrder = order; }
    public void setTitle(String title)             { this.title         = title; }
    public void setDescription(String description) { this.description   = description; }
}
