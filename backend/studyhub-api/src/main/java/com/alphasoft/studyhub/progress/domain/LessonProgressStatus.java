package com.alphasoft.studyhub.progress.domain;

/**
 * Lifecycle states for a learner's progress on a single lesson.
 *
 * <p>DR-022: completion does NOT imply mastery.  Mastery is computed separately
 * from exam results in a later sprint.
 */
public enum LessonProgressStatus {
    IN_PROGRESS,
    COMPLETED
}
