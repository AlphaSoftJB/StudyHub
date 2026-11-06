package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.Course;

public record CourseDto(
        Long   id,
        Long   disciplineId,
        String title,
        String slug,
        String description,
        int    sequenceOrder
) {
    public static CourseDto from(Course c) {
        return new CourseDto(
                c.getId(),
                c.getDiscipline().getId(),
                c.getTitle(),
                c.getSlug(),
                c.getDescription(),
                c.getSequenceOrder()
        );
    }
}
