package com.alphasoft.studyhub.identity.service;

/**
 * Thrown by {@link AuthService} when login credentials are invalid or the
 * account is inactive.
 *
 * Maps to 401 Unauthorized in AuthController.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
