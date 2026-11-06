package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import com.alphasoft.studyhub.curriculum.domain.Question;
import com.alphasoft.studyhub.curriculum.domain.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // ── Lesson-scoped ────────────────────────────────────────────────────────

    List<Question> findByLessonIdAndContentStatus(Long lessonId, ContentStatus status);

    default List<Question> findPublishedByLessonId(Long lessonId) {
        return findByLessonIdAndContentStatus(lessonId, ContentStatus.PUBLISHED);
    }

    // ── Pool / mastery queries (DR-013) ───────────────────────────────────────

    @Query("""
        SELECT q FROM Question q
        WHERE q.domain = :domain
          AND q.contentStatus = com.alphasoft.studyhub.curriculum.domain.ContentStatus.PUBLISHED
        ORDER BY q.difficulty ASC
        """)
    List<Question> findPublishedByDomain(@Param("domain") String domain);

    @Query("""
        SELECT q FROM Question q
        WHERE q.topicTag = :topicTag
          AND q.contentStatus = com.alphasoft.studyhub.curriculum.domain.ContentStatus.PUBLISHED
        """)
    List<Question> findPublishedByTopicTag(@Param("topicTag") String topicTag);

    // ── Admin ─────────────────────────────────────────────────────────────────

    List<Question> findByContentStatus(ContentStatus status);

    @Query("""
        SELECT q FROM Question q
        WHERE q.lesson IS NULL
          AND q.contentStatus = com.alphasoft.studyhub.curriculum.domain.ContentStatus.PUBLISHED
        """)
    List<Question> findPublishedStandaloneQuestions();

    long countByContentStatus(ContentStatus status);

    long countByQuestionTypeAndContentStatus(QuestionType type, ContentStatus status);
}
