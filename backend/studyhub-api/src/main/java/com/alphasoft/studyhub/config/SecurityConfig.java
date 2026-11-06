package com.alphasoft.studyhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration — Sprint 1 foundation.
 *
 * Sprint 1 scope:
 *   - Stateless sessions (no HttpSession)
 *   - Actuator /health permitted without authentication
 *   - All other endpoints require authentication (locked down by default)
 *   - CSRF disabled (stateless REST API; tokens are in-memory / httpOnly cookie)
 *   - No JWT filter yet — added in Sprint 2 when auth endpoints are implemented
 *
 * Sprint 2 will add:
 *   - JwtAuthenticationFilter (before UsernamePasswordAuthenticationFilter)
 *   - Permit /api/v1/auth/** (login, register, refresh)
 *   - Role-based access on /api/v1/admin/**
 *   - CORS configuration bean
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Actuator health check — open for load-balancer / uptime probes
                .requestMatchers("/actuator/health").permitAll()
                // Everything else locked down until auth filter is wired (Sprint 2)
                .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * BCrypt password encoder — work factor 12 (OWASP recommendation for 2024+).
     * Injected into the registration service (Sprint 2).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
