package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    List<ExamAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ExamAttempt> findByUserIdAndExamIdOrderByCreatedAtDesc(Long userId, Long examId);

    // ── Counts ───────────────────────────────────────────────────────────────

    long countByUserIdAndExamId(Long userId, Long examId);

    @Query("""
        SELECT COUNT(a) FROM ExamAttempt a
        WHERE a.user.id = :userId
          AND a.exam.id = :examId
          AND a.passed = true
        """)
    long countPassedByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);

    // ── Best score (for mastery) ──────────────────────────────────────────────

    @Query("""
        SELECT MAX(a.score) FROM ExamAttempt a
        WHERE a.user.id = :userId AND a.exam.id = :examId
        """)
    Integer findBestScoreByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);
}
