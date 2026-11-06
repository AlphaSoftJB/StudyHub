package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import com.alphasoft.studyhub.curriculum.domain.Lesson;
import com.alphasoft.studyhub.curriculum.domain.LessonType;

import java.time.Instant;

/**
 * Full lesson detail including content body — returned by GET /api/v1/lessons/{id}.
 * Content body is included only when contentStatus = PUBLISHED (enforced at service).
 */
public record LessonDetailDto(
        Long          id,
        Long          moduleId,
        String        title,
        LessonType    lessonType,
        ContentStatus contentStatus,
        String        contentBody,
        int           sequenceOrder,
        Instant       publishedAt
) {
    public static LessonDetailDto from(Lesson l) {
        return new LessonDetailDto(
                l.getId(),
                l.getModule().getId(),
                l.getTitle(),
                l.getLessonType(),
                l.getContentStatus(),
                l.getContentBody(),
                l.getSequenceOrder(),
                l.getPublishedAt()
        );
    }

    /** Admin variant that also surfaces audit fields. */
    public static LessonDetailDto fromAdmin(Lesson l) {
        return from(l); // same shape for now; extend if admin fields needed
    }
}
