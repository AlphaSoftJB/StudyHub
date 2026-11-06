package com.alphasoft.studyhub.curriculum.api.dto;

import com.alphasoft.studyhub.curriculum.domain.ContentStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for PATCH /api/v1/admin/lessons/{id}/status
 */
public record ContentStatusTransitionRequest(
        @NotNull ContentStatus targetStatus
) {}
