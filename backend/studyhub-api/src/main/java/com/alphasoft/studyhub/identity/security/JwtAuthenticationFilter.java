package com.alphasoft.studyhub.identity.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Extracts and validates the JWT Bearer token from every incoming request.
 *
 * Security behaviour (DR-009):
 *  - Runs once per request via OncePerRequestFilter.
 *  - Extracts the token from the Authorization header (Bearer scheme).
 *  - On a valid token: populates the SecurityContext so downstream code
 *    can call SecurityContextHolder.getContext().getAuthentication().
 *  - On a missing token: does NOT set authentication — the request
 *    proceeds as anonymous; Spring Security's authorisation rules apply.
 *  - On an invalid/expired token: does NOT set authentication and does
 *    NOT short-circuit with 401 here — the AuthenticationEntryPoint
 *    configured in SecurityConfig returns 401 when a protected resource
 *    rejects the anonymous request.
 *
 * Not logging tokens — only the extracted userId on success.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX        = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.validateAndParse(token);

                Long         userId = jwtService.extractUserId(claims);
                String       email  = jwtService.extractEmail(claims);
                List<String> roles  = jwtService.extractRoles(claims);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // Principal is a simple object carrying userId + email
                JwtPrincipal principal = new JwtPrincipal(userId, email);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("JWT authentication successful for userId={}", userId);

            } catch (JwtValidationException ex) {
                // Invalid token — do not populate the SecurityContext.
                // The AuthenticationEntryPoint returns 401 when downstream
                // authorisation rules reject the now-anonymous request.
                log.debug("JWT validation failed: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    /**
     * Extracts the raw JWT from the Authorization header.
     * Returns null if the header is absent or not a Bearer token.
     * Never throws — absent/malformed headers are simply treated as anonymous.
     */
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
