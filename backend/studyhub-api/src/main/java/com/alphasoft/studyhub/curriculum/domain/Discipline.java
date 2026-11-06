package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Top-level curriculum container (e.g. "Cybersecurity", "IBM Python + ML").
 *
 * <p>Only disciplines with {@code active = true} are returned to learners.
 * Pilot disciplines: {@code cyber} and {@code ibm} (DR-025).
 */
@Entity
@Table(name = "disciplines")
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    private List<Course> courses = new ArrayList<>();

    protected Discipline() {}

    public Discipline(String name, String slug, String description, String icon) {
        this.name        = name;
        this.slug        = slug;
        this.description = description;
        this.icon        = icon;
        this.active      = true;
    }

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long    getId()            { return id; }
    public String  getName()          { return name; }
    public String  getSlug()          { return slug; }
    public String  getDescription()   { return description; }
    public String  getIcon()          { return icon; }
    public boolean isActive()         { return active; }
    public int     getSequenceOrder() { return sequenceOrder; }
    public Instant getCreatedAt()     { return createdAt; }
    public Instant getUpdatedAt()     { return updatedAt; }
    public List<Course> getCourses()  { return courses; }

    public void setActive(boolean active)               { this.active        = active; }
    public void setSequenceOrder(int order)             { this.sequenceOrder = order; }
    public void setName(String name)                    { this.name          = name; }
    public void setDescription(String description)      { this.description   = description; }
    public void setIcon(String icon)                    { this.icon          = icon; }
}
