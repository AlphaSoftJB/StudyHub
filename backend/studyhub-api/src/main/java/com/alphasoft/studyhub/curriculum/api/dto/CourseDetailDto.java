package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.Course;

import java.util.List;

/**
 * Course detail response — course metadata plus its modules and their published lessons.
 *
 * Used by GET /api/v1/courses/{id} (Sprint 4 — FV-002 fix).
 * Lessons in each module are pre-filtered to PUBLISHED status only (DR-012).
 *
 * DR-015-NATIVE: no web-only response shapes; usable by iOS/Android clients.
 */
public record CourseDetailDto(
        Long                   id,
        Long                   disciplineId,
        String                 title,
        String                 slug,
        String                 description,
        int                    sequenceOrder,
        List<ModuleSummaryDto> modules
) {
    public static CourseDetailDto from(Course c, List<ModuleSummaryDto> modules) {
        return new CourseDetailDto(
                c.getId(),
                c.getDiscipline().getId(),
                c.getTitle(),
                c.getSlug(),
                c.getDescription(),
                c.getSequenceOrder(),
                modules
        );
    }
}
