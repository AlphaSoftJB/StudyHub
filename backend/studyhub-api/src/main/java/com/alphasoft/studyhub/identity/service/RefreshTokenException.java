package com.alphasoft.studyhub.identity.service;

/**
 * Thrown by {@link RefreshTokenService} when a refresh token is invalid,
 * expired, or a reuse attack is detected.
 *
 * Maps to 401 Unauthorized in AuthController.
 */
public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String message) {
        super(message);
    }
}
