package com.alphasoft.studyhub.progress.repository;

import com.alphasoft.studyhub.progress.domain.LessonProgressStatus;
import com.alphasoft.studyhub.progress.domain.UserLessonProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * DR-015-NATIVE: all list queries accept a {@link Pageable} parameter.
 */
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    /** All progress rows for a learner, newest first. */
    Page<UserLessonProgress> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

    /** Count completed lessons for a learner. */
    long countByUserIdAndStatus(Long userId, LessonProgressStatus status);

    /**
     * Completed lessons for a learner in a specific course — used to compute
     * per-course completion percentage on the dashboard.
     */
    @Query("""
        SELECT ulp FROM UserLessonProgress ulp
        JOIN ulp.lesson l
        JOIN l.module m
        WHERE ulp.user.id = :userId
          AND m.course.id = :courseId
          AND ulp.status = 'COMPLETED'
        """)
    Page<UserLessonProgress> findCompletedByCourse(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            Pageable pageable);

    /**
     * Count of published lessons per course (used server-side for completion %).
     * Returned as Object[] {courseId, count} — avoids a full entity load.
     */
    @Query("""
        SELECT m.course.id, COUNT(l.id)
        FROM com.alphasoft.studyhub.curriculum.domain.Lesson l
        JOIN l.module m
        WHERE l.contentStatus = com.alphasoft.studyhub.curriculum.domain.ContentStatus.PUBLISHED
          AND m.course.id IN :courseIds
        GROUP BY m.course.id
        """)
    java.util.List<Object[]> countPublishedLessonsPerCourse(@Param("courseIds") java.util.List<Long> courseIds);

    /**
     * Count of completed lessons per course for a learner — used in dashboard.
     * Returned as Object[] {courseId, count} — avoids entity loading.
     */
    @Query("""
        SELECT m.course.id, COUNT(ulp.id)
        FROM UserLessonProgress ulp
        JOIN ulp.lesson l
        JOIN l.module m
        WHERE ulp.user.id = :userId
          AND m.course.id IN :courseIds
          AND ulp.status = 'COMPLETED'
        GROUP BY m.course.id
        """)
    java.util.List<Object[]> countCompletedPerCourse(
            @Param("userId") Long userId,
            @Param("courseIds") java.util.List<Long> courseIds);

    /**
     * Returns the distinct course IDs for which a learner has any progress record,
     * ordered by the most-recently-updated progress row in that course (DESC).
     */
    @Query("""
        SELECT m.course.id
        FROM UserLessonProgress ulp
        JOIN ulp.lesson l
        JOIN l.module m
        WHERE ulp.user.id = :userId
        GROUP BY m.course.id
        ORDER BY MAX(ulp.updatedAt) DESC
        """)
    java.util.List<Long> findDistinctCourseIdsByUserId(@Param("userId") Long userId);
}
