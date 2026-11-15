package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.Module;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module summary for the course-detail endpoint.
 * Includes published lesson summaries; unpublished lessons are excluded (DR-012).
 *
 * DR-015-NATIVE: no web-only response shapes; usable by iOS/Android clients.
 */
public record ModuleSummaryDto(
        Long              id,
        Long              courseId,
        String            title,
        int               sequenceOrder,
        List<LessonSummaryDto> lessons
) {
    public static ModuleSummaryDto from(Module m, List<LessonSummaryDto> publishedLessons) {
        return new ModuleSummaryDto(
                m.getId(),
                m.getCourse().getId(),
                m.getTitle(),
                m.getSequenceOrder(),
                publishedLessons
        );
    }
}
