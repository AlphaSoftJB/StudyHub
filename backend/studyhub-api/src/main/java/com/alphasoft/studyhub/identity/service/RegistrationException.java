package com.alphasoft.studyhub.identity.service;

/**
 * Thrown by {@link RegistrationService} when registration cannot proceed.
 * Maps to 400 Bad Request or 409 Conflict in AuthController.
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }
}
