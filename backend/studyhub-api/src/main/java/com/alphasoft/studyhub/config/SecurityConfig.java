package com.alphasoft.studyhub.config;

import com.alphasoft.studyhub.identity.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Sprint 2 SecurityConfig — wires JWT authentication filter, CSRF protection,
 * CORS allowlist, and authorization rules.
 *
 * Key design decisions (DR-009):
 *
 * 1. STATELESS — no HttpSession; every request must carry its own credentials.
 *
 * 2. CSRF — double-submit cookie pattern (CookieCsrfTokenRepository).
 *    - XSRF-TOKEN cookie is NOT httpOnly (JS must be able to read it).
 *    - X-XSRF-TOKEN header required on state-changing requests.
 *    - Login and register are EXEMPT (no CSRF cookie exists at that point).
 *    - Refresh, logout, logout-all ENFORCE CSRF.
 *
 * 3. CORS — strict allowlist from studyhub.cors.allowed-origins.
 *    No wildcard + credentials.
 *
 * 4. JWT filter — runs before UsernamePasswordAuthenticationFilter.
 *    Invalid/missing tokens result in anonymous SecurityContext;
 *    protected endpoints return 401 via the AuthenticationEntryPoint.
 *
 * 5. Authorization rules:
 *    - /actuator/health — public
 *    - /api/v1/auth/login, /api/v1/auth/register — public
 *    - /api/v1/admin/** — ROLE_ADMIN only
 *    - everything else — authenticated
 *
 * Context-path is /api (set in application.yml), so full URLs include /api prefix,
 * but Security filter matching uses the request URI (which includes /api).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final StudyHubProperties      props;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          StudyHubProperties props) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.props                   = props;
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) ->
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Stateless — no server-side session
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 2. CSRF — double-submit cookie pattern (DR-009)
            //    ignoringRequestMatchers paths do NOT include the context-path prefix /api
            //    because Spring Security matches against the servlet path (after context-path stripping).
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                    // Health check — no session/cookie context
                    "/actuator/health",
                    // Login and register: CSRF cookie doesn't exist yet at these calls.
                    // Note: Spring Security matches against the servlet path (after the
                    // context-path /api is stripped), so these paths must NOT include /api.
                    "/v1/auth/login",
                    "/v1/auth/register"
                )
            )

            // 3. CORS — allowlist from studyhub.cors.allowed-origins
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 4. JWT filter — populate SecurityContext from Bearer token
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 5. 401 for unauthenticated requests (not Spring's default 403)
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(unauthorizedEntryPoint()))

            // 6. Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public — no token required
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/login", "/v1/auth/register").permitAll()
                // Refresh and logout use the httpOnly refresh cookie (not a Bearer JWT);
                // they must be permit-all so the JWT filter's missing-token anonymous context
                // does not trigger a 401 before the controller reads the cookie.
                // CSRF protection (double-submit cookie) is still enforced on these paths.
                .requestMatchers(HttpMethod.POST, "/v1/auth/refresh", "/v1/auth/logout").permitAll()
                // Admin only
                .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                // Everything else requires authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins from configuration (DR-009: no wildcard with credentials)
        config.setAllowedOrigins(props.cors().allowedOrigins());

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);  // Required for httpOnly cookies
        config.setMaxAge(3600L);           // 1 hour preflight cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
