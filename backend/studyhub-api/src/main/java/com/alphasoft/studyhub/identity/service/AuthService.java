package com.alphasoft.studyhub.identity.service;

import com.alphasoft.studyhub.config.StudyHubProperties;
import com.alphasoft.studyhub.identity.api.dto.AuthResponse;
import com.alphasoft.studyhub.identity.domain.User;
import com.alphasoft.studyhub.identity.repository.UserRepository;
import com.alphasoft.studyhub.identity.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orchestrates authentication operations (login, token refresh, logout).
 *
 * This service does not deal with cookies — that is the responsibility of
 * AuthController, which has access to HttpServletResponse. This keeps the
 * service layer free from HTTP concerns and testable in isolation.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository     userRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtService         jwtService;
    private final StudyHubProperties props;

    public AuthService(
            UserRepository     userRepository,
            PasswordEncoder    passwordEncoder,
            JwtService         jwtService,
            StudyHubProperties props) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService      = jwtService;
        this.props           = props;
    }

    /**
     * Authenticates a user by email/password.
     *
     * <p>Returns an AuthResponse on success. The caller (AuthController) is
     * responsible for creating the refresh token and setting the cookie.
     *
     * <p>Throws {@link AuthenticationException} on failure. The message is
     * deliberately generic to avoid leaking whether the email exists.
     */
    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!user.isActive()) {
            log.warn("Login attempt for inactive account: email={}", email);
            throw new AuthenticationException("Invalid credentials");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.debug("Login failed: bad password for email={}", email);
            throw new AuthenticationException("Invalid credentials");
        }

        log.debug("Login successful: userId={}", user.getId());
        return user;
    }

    /**
     * Builds an AuthResponse for the given user (used after login and token refresh).
     */
    public AuthResponse buildAuthResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .toList();

        String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail(), roles);

        return AuthResponse.of(
            accessToken,
            props.jwt().accessExpiryMs(),
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            roles
        );
    }
}
