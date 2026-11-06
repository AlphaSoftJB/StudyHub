package com.alphasoft.studyhub.curriculum.domain;

/**
 * DR-012 — Content lifecycle states.
 *
 * <p>All content (lessons, questions) must follow this lifecycle.
 * Only {@code PUBLISHED} content is visible to learners.
 * Transitions: DRAFT → IN_REVIEW → APPROVED → PUBLISHED → DEPRECATED
 *
 * <p>Direct SQL writes to content_status are prohibited outside emergency recovery
 * (DR-012 v1.1). All transitions go through {@link com.alphasoft.studyhub.curriculum.service.ContentLifecycleService}.
 */
public enum ContentStatus {

    /** Initial state. Content is being authored. Not visible to anyone. */
    DRAFT,

    /** Content has been submitted for editorial/factual review. */
    IN_REVIEW,

    /**
     * Content has been reviewed and approved.
     * USMLE/AI-generated content requires a qualified reviewer (DR-007).
     * Not yet live to learners.
     */
    APPROVED,

    /** Content is live and visible to learners. */
    PUBLISHED,

    /** Content has been superseded or removed from the active curriculum. */
    DEPRECATED;

    /** Returns true when this status allows learner visibility. */
    public boolean isLearnerVisible() {
        return this == PUBLISHED;
    }

    /** Returns true when this status allows editorial transitions. */
    public boolean canTransitionTo(ContentStatus next) {
        return switch (this) {
            case DRAFT       -> next == IN_REVIEW  || next == APPROVED;
            case IN_REVIEW   -> next == APPROVED   || next == DRAFT;
            case APPROVED    -> next == PUBLISHED  || next == IN_REVIEW;
            case PUBLISHED   -> next == DEPRECATED;
            case DEPRECATED  -> false;
        };
    }
}
