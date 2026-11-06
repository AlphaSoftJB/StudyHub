package com.alphasoft.studyhub.identity.api;

import com.alphasoft.studyhub.identity.api.dto.AuthResponse;
import com.alphasoft.studyhub.identity.api.dto.ErrorResponse;
import com.alphasoft.studyhub.identity.api.dto.LoginRequest;
import com.alphasoft.studyhub.identity.api.dto.RegisterRequest;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.security.JwtPrincipal;
import com.alphasoft.studyhub.identity.service.AuthService;
import com.alphasoft.studyhub.identity.service.AuthenticationException;
import com.alphasoft.studyhub.identity.service.RefreshTokenException;
import com.alphasoft.studyhub.identity.service.RefreshTokenService;
import com.alphasoft.studyhub.identity.service.RefreshTokenService.RotationResult;
import com.alphasoft.studyhub.identity.service.RegistrationException;
import com.alphasoft.studyhub.identity.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authentication API endpoints.
 *
 * All endpoints are under /api/v1/auth (context-path /api is set in application.yml,
 * so the servlet path is /v1/auth — but Spring Security matches against full URI).
 *
 * Public endpoints (no JWT required):
 *   POST /api/v1/auth/register   — invite-only registration
 *   POST /api/v1/auth/login      — returns access token + sets refresh cookie
 *
 * Token endpoints (refresh cookie required):
 *   POST /api/v1/auth/refresh    — rotate refresh token, return new access token
 *   POST /api/v1/auth/logout     — revoke refresh token (single device)
 *   POST /api/v1/auth/logout-all — revoke all refresh tokens for user (all devices)
 *
 * Security notes:
 *  - No PII leaks in error messages (login/register failures are generic).
 *  - Refresh cookie set/cleared exclusively by RefreshTokenService.
 *  - logout-all requires a valid JWT (authenticated endpoint).
 *  - CSRF: login and register are exempt; refresh, logout, logout-all enforce it
 *    via Spring Security's CookieCsrfTokenRepository (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RegistrationService  registrationService;
    private final AuthService          authService;
    private final RefreshTokenService  refreshTokenService;

    public AuthController(
            RegistrationService  registrationService,
            AuthService          authService,
            RefreshTokenService  refreshTokenService) {
        this.registrationService = registrationService;
        this.authService         = authService;
        this.refreshTokenService = refreshTokenService;
    }

    // ── POST /v1/auth/register ───────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req,
                                      HttpServletResponse response) {
        try {
            User user = registrationService.register(
                req.email(), req.password(), req.displayName(), req.invitationCode());

            // Issue an access token and refresh cookie so the user is immediately logged in
            String rawRefreshToken = refreshTokenService.createRefreshToken(user);
            refreshTokenService.setRefreshCookie(response, rawRefreshToken);

            AuthResponse body = authService.buildAuthResponse(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(body);

        } catch (RegistrationException ex) {
            // Don't distinguish "code invalid" from "email exists" to prevent oracle attacks
            HttpStatus status = ex.getMessage().contains("capacity")
                    ? HttpStatus.SERVICE_UNAVAILABLE
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), ex.getMessage()));
        }
    }

    // ── POST /v1/auth/login ──────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req,
                                   HttpServletResponse response) {
        try {
            User user = authService.authenticate(req.email(), req.password());

            String rawRefreshToken = refreshTokenService.createRefreshToken(user);
            refreshTokenService.setRefreshCookie(response, rawRefreshToken);

            return ResponseEntity.ok(authService.buildAuthResponse(user));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(401, "Unauthorized", "Invalid credentials"));
        }
    }

    // ── POST /v1/auth/refresh ────────────────────────────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = refreshTokenService.extractRefreshToken(request)
                .orElse(null);

        if (rawToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(401, "Unauthorized", "Refresh token cookie missing"));
        }

        try {
            RotationResult result = refreshTokenService.rotate(rawToken);

            // Set the new rotated cookie
            refreshTokenService.setRefreshCookie(response, result.newRawToken());

            return ResponseEntity.ok(authService.buildAuthResponse(result.user()));

        } catch (RefreshTokenException ex) {
            // On reuse detection the family has already been fully revoked
            refreshTokenService.clearRefreshCookie(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(401, "Unauthorized", "Session expired; please log in again"));
        }
    }

    // ── POST /v1/auth/logout ─────────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.extractRefreshToken(request)
                .ifPresent(refreshTokenService::revoke);
        refreshTokenService.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    // ── POST /v1/auth/logout-all ─────────────────────────────────────────────

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal JwtPrincipal principal,
            HttpServletResponse response) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        refreshTokenService.revokeAllForUser(principal.userId());
        refreshTokenService.clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    // ── Exception handlers ───────────────────────────────────────────────────

    /**
     * Handles Bean Validation failures (@Valid) for all endpoints in this controller.
     * Returns field-level error messages; no stack traces leaked.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest().body(ErrorResponse.validation(fieldErrors));
    }
}
