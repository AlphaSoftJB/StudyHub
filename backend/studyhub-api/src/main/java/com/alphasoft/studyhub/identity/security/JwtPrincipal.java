package com.alphasoft.studyhub.identity.security;

/**
 * Lightweight principal carried inside a {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
 * after successful JWT validation.
 *
 * Controllers can retrieve the authenticated user's ID and email via:
 * <pre>{@code
 *   JwtPrincipal principal = (JwtPrincipal)
 *       SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 *   Long userId = principal.userId();
 * }</pre>
 *
 * Or through {@code @AuthenticationPrincipal JwtPrincipal principal} with
 * a custom {@code AuthenticationPrincipalArgumentResolver} (future Sprint).
 */
public record JwtPrincipal(Long userId, String email) {}
