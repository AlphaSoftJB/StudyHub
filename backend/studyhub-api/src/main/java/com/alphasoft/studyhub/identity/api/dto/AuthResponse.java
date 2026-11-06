package com.alphasoft.studyhub.identity.api.dto;

import java.util.List;

/**
 * Response body for successful login and token refresh.
 *
 * Security note: the refresh token is NOT included here — it is sent
 * exclusively as an httpOnly SameSite=Strict cookie by RefreshTokenService.
 * Only the access token (short-lived, in-memory) is returned in the body.
 */
public record AuthResponse(
    String       accessToken,
    String       tokenType,
    long         expiresInSeconds,
    Long         userId,
    String       email,
    String       displayName,
    List<String> roles
) {
    /** Convenience factory — tokenType is always "Bearer". */
    public static AuthResponse of(
            String       accessToken,
            long         expiresInMs,
            Long         userId,
            String       email,
            String       displayName,
            List<String> roles) {
        return new AuthResponse(
            accessToken,
            "Bearer",
            expiresInMs / 1000,
            userId,
            email,
            displayName,
            roles
        );
    }
}
