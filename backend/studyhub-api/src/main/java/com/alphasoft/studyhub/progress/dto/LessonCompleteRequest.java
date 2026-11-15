package com.alphasoft.studyhub.progress.dto;

/**
 * Request body for POST /api/v1/progress/lessons/{lessonId}/complete.
 * Currently no required fields — the endpoint derives everything from
 * the authenticated user and the path parameter.
 *
 * <p>The body is kept as a record (not empty) so that future fields
 * (time-on-lesson, device type, etc.) can be added without a breaking
 * contract change.
 */
public record LessonCompleteRequest() {
}
