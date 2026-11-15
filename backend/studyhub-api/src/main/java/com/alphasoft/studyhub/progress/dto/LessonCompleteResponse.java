package com.alphasoft.studyhub.progress.dto;

import com.alphasoft.studyhub.progress.domain.LessonProgressStatus;
import com.alphasoft.studyhub.progress.domain.UserLessonProgress;

import java.time.Instant;

/**
 * Response body for POST /api/v1/progress/lessons/{lessonId}/complete.
 *
 * <p>Returns the final state of the progress record after the operation,
 * plus the XP awarded (0 if the lesson was already marked complete).
 */
public record LessonCompleteResponse(
        Long                   progressId,
        Long                   lessonId,
        LessonProgressStatus   status,
        Instant                completedAt,
        int                    xpAwarded,
        int                    totalXp
) {
    public static LessonCompleteResponse from(UserLessonProgress p, int totalXp) {
        return new LessonCompleteResponse(
                p.getId(),
                p.getLesson().getId(),
                p.getStatus(),
                p.getCompletedAt(),
                p.getXpAwarded(),
                totalXp
        );
    }
}
