package com.alphasoft.studyhub.identity.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/v1/auth/login.
 */
public record LoginRequest(

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 255)
    String email,

    @NotBlank(message = "Password is required")
    @Size(max = 128)
    String password
) {}
