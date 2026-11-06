package com.alphasoft.studyhub.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/v1/auth/register.
 *
 * All fields are validated before the service layer is called.
 * Error messages do not indicate whether a field is structurally wrong
 * vs. already in use, to avoid leaking PII about existing accounts.
 */
public record RegisterRequest(

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email must be 255 characters or fewer")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    String password,

    @NotBlank(message = "Display name is required")
    @Size(min = 1, max = 100, message = "Display name must be 100 characters or fewer")
    String displayName,

    @NotBlank(message = "Invitation code is required")
    @Size(min = 36, max = 36, message = "Invitation code must be a 36-character UUID")
    String invitationCode
) {}
