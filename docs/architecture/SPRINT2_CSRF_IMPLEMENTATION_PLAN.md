# StudyHub — Sprint 2 CSRF Protection Implementation Plan

**Date:** 2026-11-06  
**Status:** Approved for Sprint 2 implementation — do NOT implement until Sprint 2 is authorized  
**Decision record:** DR-009  
**Prepared by:** Claude Sonnet 4.6, session https://claude.ai/code/session_01GrXQBHN1FfE3b1PuBWsGTt

---

## Background

Sprint 1 `SecurityConfig` disables CSRF with the comment:
> "CSRF disabled (stateless REST API; tokens are in-memory / httpOnly cookie)"

This is acceptable in Sprint 1 because there are no authentication endpoints to attack.

Sprint 2 introduces an httpOnly SameSite=Strict refresh-token cookie. That cookie is
automatically sent by the browser on requests to the same origin, which creates a CSRF
attack surface. DR-009 explicitly requires the double-submit cookie pattern as
defence-in-depth even though SameSite=Strict provides the primary mitigation.

---

## Why SameSite=Strict Alone Is Not Enough

SameSite=Strict prevents the refresh cookie from being sent on cross-site requests
in all modern browsers. However:

1. SameSite is a browser policy, not a server-enforced control.
2. Some enterprise proxies, misconfigured environments, or legacy browsers may not
   honor it correctly.
3. DR-009 explicitly requires defence-in-depth; the double-submit cookie is the
   required second layer.

---

## Approved Implementation: Double-Submit Cookie Pattern

The double-submit cookie pattern adds a server-verifiable CSRF token to state-changing
requests without requiring server-side session state (compatible with STATELESS security).

### How it works

1. **On login** (`POST /api/v1/auth/login`): the server generates a random CSRF token
   (UUID v4 or SecureRandom hex, 32+ chars) and sends it in TWO places:
   - As a **readable JavaScript cookie**: `XSRF-TOKEN`, `SameSite=Strict; Path=/`
     (NOT httpOnly — JavaScript must be able to read it)
   - As a **claim in the JWT access token** body (optional second binding)

2. **On state-changing requests** (`/refresh`, `/logout`, any future POST/PUT/DELETE):
   - The browser automatically sends the httpOnly refresh cookie.
   - The client JavaScript must also read `XSRF-TOKEN` and send it as an HTTP header:
     `X-XSRF-TOKEN: <value>`
   - Spring Security's `CsrfTokenRepository` validates that the header value matches
     the cookie value.

3. **Why this works against CSRF**: a cross-site attacker cannot read the `XSRF-TOKEN`
   cookie (same-origin policy) and therefore cannot include the correct header value.

### Spring Security configuration (Sprint 2)

```java
// Replace csrf(csrf -> csrf.disable()) with:
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
    .ignoringRequestMatchers(
        // Public endpoints that require no CSRF protection:
        "/actuator/health",
        // POST /login is exempt — no cookie exists yet at login time.
        // The CSRF cookie is SET during login; it cannot be validated by the same request.
        "/api/v1/auth/login"
    )
)
```

`CookieCsrfTokenRepository.withHttpOnlyFalse()` creates the `XSRF-TOKEN` cookie with
`httpOnly=false` so JavaScript can read it.

### Endpoints that MUST enforce CSRF

| Endpoint | Method | CSRF required? | Reason |
|----------|--------|----------------|--------|
| `/api/v1/auth/login` | POST | ❌ No | CSRF cookie doesn't exist yet |
| `/api/v1/auth/register` | POST | ❌ No | No session/cookie before registration |
| `/api/v1/auth/refresh` | POST | ✅ **Yes** | Sends httpOnly refresh cookie |
| `/api/v1/auth/logout` | POST | ✅ **Yes** | Sends httpOnly refresh cookie |
| `/api/v1/auth/logout-all` | POST | ✅ **Yes** | Sends httpOnly refresh cookie |
| Any future POST/PUT/DELETE | POST/PUT/DELETE | ✅ **Yes** | Default Spring Security behaviour |

### Frontend client requirements (Sprint 2)

The Axios interceptor must:
1. Read `document.cookie` to extract the `XSRF-TOKEN` value.
2. Add it as `X-XSRF-TOKEN` header on all non-GET requests.

```typescript
// In src/lib/api.ts (Sprint 2)
function getCsrfToken(): string | null {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

axiosInstance.interceptors.request.use(config => {
  if (!['GET', 'HEAD', 'OPTIONS', 'TRACE'].includes(config.method?.toUpperCase() ?? '')) {
    const csrf = getCsrfToken();
    if (csrf) {
      config.headers['X-XSRF-TOKEN'] = csrf;
    }
  }
  return config;
});
```

---

## SecurityConfig Changes Required in Sprint 2

### Full Sprint 2 SecurityConfig skeleton

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Stateless — no server-side session
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 2. CSRF — double-submit cookie pattern (DR-009)
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(
                    "/actuator/health",
                    "/api/v1/auth/login",
                    "/api/v1/auth/register"
                )
            )

            // 3. CORS — allowlist of approved origins
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 4. JWT filter — validates Bearer token on every request
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 5. Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configured from studyhub.cors.allowed-origins in application.yml
        // ... Sprint 2 implementation
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

---

## Checklist for Sprint 2 CSRF Implementation

Before the auth endpoints go live, the following must be complete:

- [ ] `CookieCsrfTokenRepository.withHttpOnlyFalse()` configured in SecurityConfig
- [ ] `XSRF-TOKEN` cookie set on login response
- [ ] `/api/v1/auth/refresh` and `/api/v1/auth/logout` reject requests with no/invalid CSRF header
- [ ] Frontend Axios interceptor reads and sends `X-XSRF-TOKEN` header
- [ ] Integration test: `POST /api/v1/auth/refresh` without CSRF header returns 403
- [ ] Integration test: `POST /api/v1/auth/refresh` with correct CSRF header returns 200

---

## References

- DR-009: CSRF protection decision
- Spring Security docs: `CookieCsrfTokenRepository`
- OWASP CSRF Prevention Cheat Sheet — Double Submit Cookie pattern

---

*End of SPRINT2_CSRF_IMPLEMENTATION_PLAN.md*
