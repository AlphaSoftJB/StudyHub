package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.curriculum.domain.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // ── Learner-facing queries (PUBLISHED only, DR-012) ─────────────────────

    List<Lesson> findByModuleIdAndContentStatusOrderBySequenceOrderAsc(
            Long moduleId, ContentStatus status);

    Optional<Lesson> findByIdAndContentStatus(Long id, ContentStatus status);

    @Query("""
        SELECT l FROM Lesson l
        JOIN l.module m
        JOIN m.course c
        JOIN c.discipline d
        WHERE d.slug = :disciplineSlug
          AND l.contentStatus = com.alphasoft.studyhub.curriculum.domain.ContentStatus.PUBLISHED
        ORDER BY c.sequenceOrder ASC, m.sequenceOrder ASC, l.sequenceOrder ASC
        """)
    List<Lesson> findPublishedByDisciplineSlug(@Param("disciplineSlug") String disciplineSlug);

    // ── Admin queries (all statuses) ─────────────────────────────────────────

    List<Lesson> findByModuleIdOrderBySequenceOrderAsc(Long moduleId);

    List<Lesson> findByContentStatus(ContentStatus status);

    @Query("""
        SELECT l FROM Lesson l
        WHERE l.lessonType = :type
          AND l.contentStatus = :status
        ORDER BY l.id ASC
        """)
    List<Lesson> findByLessonTypeAndContentStatus(
            @Param("type") LessonType type,
            @Param("status") ContentStatus status);

    // ── Default shortcut for learner fetch ───────────────────────────────────

    default List<Lesson> findPublishedByModuleId(Long moduleId) {
        return findByModuleIdAndContentStatusOrderBySequenceOrderAsc(
                moduleId, ContentStatus.PUBLISHED);
    }

    default Optional<Lesson> findPublishedById(Long id) {
        return findByIdAndContentStatus(id, ContentStatus.PUBLISHED);
    }
}
