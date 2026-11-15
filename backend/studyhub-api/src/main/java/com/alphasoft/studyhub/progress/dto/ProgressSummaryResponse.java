package com.alphasoft.studyhub.progress.dto;

import com.alphasoft.studyhub.progress.domain.LessonProgressStatus;
import com.alphasoft.studyhub.progress.domain.UserLessonProgress;

import java.time.Instant;

/**
 * One entry in the learner's progress list — returned by GET /api/v1/progress/me.
 *
 * <p>DR-015-NATIVE: the endpoint wraps these in a {@link org.springframework.data.domain.Page}.
 */
public record ProgressSummaryResponse(
        Long                 progressId,
        Long                 lessonId,
        String               lessonTitle,
        LessonProgressStatus status,
        Instant              completedAt,
        int                  xpAwarded
) {
    public static ProgressSummaryResponse from(UserLessonProgress p) {
        return new ProgressSummaryResponse(
                p.getId(),
                p.getLesson().getId(),
                p.getLesson().getTitle(),
                p.getStatus(),
                p.getCompletedAt(),
                p.getXpAwarded()
        );
    }
}
