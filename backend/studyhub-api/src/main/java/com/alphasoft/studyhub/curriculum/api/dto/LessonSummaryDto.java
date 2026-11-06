package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.curriculum.domain.LessonType;

/**
 * Lightweight lesson summary for module/course listings — does NOT include
 * the content body (fetched separately via {@link LessonDetailDto}).
 */
public record LessonSummaryDto(
        Long       id,
        Long       moduleId,
        String     title,
        LessonType lessonType,
        int        sequenceOrder
) {
    public static LessonSummaryDto from(Lesson l) {
        return new LessonSummaryDto(
                l.getId(),
                l.getModule().getId(),
                l.getTitle(),
                l.getLessonType(),
                l.getSequenceOrder()
        );
    }
}
