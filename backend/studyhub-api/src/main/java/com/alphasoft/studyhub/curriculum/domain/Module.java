package com.alphasoft.studyhub.curriculum.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A module (week/unit) within a course (e.g. "Week 1: OSI Model").
 */
@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    private List<Lesson> lessons = new ArrayList<>();

    protected Module() {}

    public Module(Course course, String title) {
        this.course = course;
        this.title  = title;
    }

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public Long         getId()            { return id; }
    public Course       getCourse()        { return course; }
    public String       getTitle()         { return title; }
    public int          getSequenceOrder() { return sequenceOrder; }
    public Instant      getCreatedAt()     { return createdAt; }
    public Instant      getUpdatedAt()     { return updatedAt; }
    public List<Lesson> getLessons()       { return lessons; }

    public void setSequenceOrder(int order) { this.sequenceOrder = order; }
    public void setTitle(String title)      { this.title         = title; }
}
