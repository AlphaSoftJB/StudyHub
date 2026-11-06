package com.alphasoft.studyhub.identity.security;

/**
 * Thrown by {@link JwtService} when a JWT token fails validation.
 *
 * This exception is a stable API boundary — it shields callers from JJWT
 * internal exceptions and allows the authentication filter to handle all
 * JWT failure modes uniformly by catching this single type.
 */
public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
