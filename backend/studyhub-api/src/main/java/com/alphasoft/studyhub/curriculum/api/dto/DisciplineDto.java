package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.Discipline;

/**
 * Learner-facing discipline summary (Explore page).
 * DR-015-NATIVE: no web-only contracts — this DTO is usable by web and mobile.
 */
public record DisciplineDto(
        Long   id,
        String name,
        String slug,
        String description,
        String icon,
        int    sequenceOrder
) {
    public static DisciplineDto from(Discipline d) {
        return new DisciplineDto(
                d.getId(),
                d.getName(),
                d.getSlug(),
                d.getDescription(),
                d.getIcon(),
                d.getSequenceOrder()
        );
    }
}
