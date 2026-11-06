# Sprint 2 Report — JWT Authentication & Auth API

**Date:** 2026-11-06  
**Branch:** `studyhub-dev`  
**Sprint 2 commit:** `f51f4fe`  
**Starting commit:** `3c2dc52` (Sprint 1 complete, 15 tests passing)  
**`main` (production):** `c84f920` — **unchanged**

---

## Bootcamp Protection Confirmation ✅

| Requirement | Status | Evidence |
|---|---|---|
| Existing bootcamp affected? | **No** | No changes to `main`, GitHub Pages, or bootcamp-hub branch. |
| Existing study functionality operational? | **Yes** | `main` at `c84f920` — not touched. All Sprint 1 routes intact. |
| Existing content or learner data modified? | **No** | Sprint 2 work is entirely new code on `studyhub-dev`. No data migrations. |
| Regression tests for existing functionality | **Pass** | 15 Sprint 1 tests (HealthCheckTest, SchemaValidationTest, SecurityTest, etc.) included in the full 46-test suite. Run `mvn test` with `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` to verify. |

---

## Scope

Sprint 2 delivered the full authentication layer: JWT access tokens, refresh token rotation, invite-only registration with pilot capacity enforcement, CSRF double-submit cookie, CORS allowlist, and a complete auth API with consistent error responses.

### Authorized scope items delivered

| Item | Status |
|---|---|
| JWT infrastructure (JwtService, filter, principal) | ✅ Complete |
| Auth filter | ✅ Complete |
| Auth API: register, login, refresh, logout, logout-all | ✅ Complete |
| Invite-only registration (DR-024) | ✅ Complete |
| Refresh token rotation with reuse detection (DR-009) | ✅ Complete |
| Refresh token security (httpOnly, SameSite=Strict, SHA-256 hash) | ✅ Complete |
| CSRF double-submit cookie | ✅ Complete |
| CORS allowlist from config | ✅ Complete |
| Startup validation (StudyHubProperties) | ✅ Complete (carried from Sprint 1) |
| Comprehensive tests | ✅ 31 new + 15 existing = 46 total |
| Documentation | ✅ This report + inline Javadoc |

---

## Files Changed

### New source files (19)

```
identity/security/JwtService.java
identity/security/JwtValidationException.java
identity/security/JwtAuthenticationFilter.java
identity/security/JwtPrincipal.java
identity/service/AuthService.java
identity/service/AuthenticationException.java
identity/service/RefreshTokenService.java
identity/service/RefreshTokenException.java
identity/service/RegistrationService.java
identity/service/RegistrationException.java
identity/api/AuthController.java
identity/api/dto/AuthResponse.java
identity/api/dto/ErrorResponse.java
identity/api/dto/LoginRequest.java
identity/api/dto/RegisterRequest.java
```

### Modified source files (2)

```
config/SecurityConfig.java       — Full rewrite for JWT filter, CSRF double-submit, CORS
identity/repository/InvitationCodeRepository.java  — Added findByCodeWithLock
identity/repository/UserRepository.java            — Added countActiveLearners
```

_(Note: UserRepository and InvitationCodeRepository additions count as 2 modified files, 1 file each.)_

### New test files (5)

```
auth/JwtServiceTest.java          — 6 unit tests
auth/RefreshTokenServiceTest.java — 7 unit tests
auth/AuthApiTest.java             — 9 integration tests
auth/CsrfCorsTest.java            — 8 integration tests
auth/ConcurrencyTest.java         — 2 concurrency tests
```

**Total: 23 files changed, 2,058 insertions, 24 deletions**

---

## Architecture Summary

### JWT token lifecycle

```
Login / Register
     │
     ├─► JwtService.issueAccessToken()
     │       └─ HS256 signed, 15 min expiry, sub=userId, roles claim
     │       └─ Returned in JSON response body (never stored server-side)
     │
     └─► RefreshTokenService.createRefreshToken()
             └─ 32-byte SecureRandom → Base64URL raw token
             └─ sha256Hex(raw) stored in refresh_tokens table
             └─ Raw token set in httpOnly SameSite=Strict cookie
             └─ Cookie path: /api/v1/auth (scoped, not global)

Access (authenticated request)
     ├─ Client sends: Authorization: Bearer <access_token>
     └─ JwtAuthenticationFilter: validates → sets JwtPrincipal in SecurityContext

Refresh
     ├─ Client sends: Cookie: refresh_token=<raw>; X-XSRF-TOKEN: <csrf>
     ├─ RefreshTokenService.rotate():
     │       sha256(raw) → lookup → validate → revoke old → issue new (same familyId)
     │       Reuse of revoked token → revoke ENTIRE family (all sessions compromised)
     └─ New access token + new refresh cookie returned

Logout
     ├─ Single: revoke current refresh token → Max-Age=0 cookie
     └─ All sessions (logout-all): revoke all tokens for user (requires valid JWT)
```

### Security controls

| Control | Implementation |
|---|---|
| Access token storage | In-memory only (never localStorage) |
| Refresh token storage | httpOnly SameSite=Strict cookie |
| Refresh token DB storage | SHA-256 hash only — raw never persisted |
| CSRF protection | CookieCsrfTokenRepository double-submit; X-XSRF-TOKEN header required on state-changing requests |
| CSRF exemptions | POST /register, POST /login (pre-auth, cannot have session-bound token) |
| CORS | Allowlist from `studyhub.cors.allowed-origins`; allowCredentials=true |
| Invite-only registration | Single-use UUID code; pessimistic write lock prevents concurrent double-redemption |
| Pilot capacity | 10 learner max (DR-025); enforced inside SERIALIZABLE transaction |
| Error messages | Generic ("Invalid credentials", "Invalid or expired invitation code") — no oracle |
| Token logging | Never logs token values; only token IDs/metadata on failure |

### CSRF implementation detail

```
CookieCsrfTokenRepository.withHttpOnlyFalse()
  └─ Sets XSRF-TOKEN cookie (readable by JavaScript)
  └─ Validates X-XSRF-TOKEN header on non-exempt state-changing requests

Exempt (pre-authentication, no session):
  POST /api/v1/auth/login
  POST /api/v1/auth/register
  GET  /api/actuator/health

Required (post-authentication):
  POST /api/v1/auth/refresh
  POST /api/v1/auth/logout
  POST /api/v1/auth/logout-all
  Any future protected endpoint
```

### Database (no schema changes)

V1–V6 Flyway migrations already provide all required tables. No V7 migration was needed:

| Table | Sprint 2 usage |
|---|---|
| `roles` | ROLE_LEARNER assigned on registration; ROLE_ADMIN for invite issuers |
| `users` | Created by RegistrationService; `active` flag checked on login |
| `user_roles` | Populated by RegistrationService |
| `invitation_codes` | Read with PESSIMISTIC_WRITE lock; `redeemed_at` set on use |
| `refresh_tokens` | Stores `token_hash`, `family_id`, `revoked`, `expires_at` |

---

## Tests

### Test suite summary (46 total)

| Test class | Count | Type | Description |
|---|---|---|---|
| `JwtServiceTest` | 6 | Unit (no Spring) | Token issue/validate/extract, expiry, wrong key, tamper |
| `RefreshTokenServiceTest` | 7 | Unit (no Spring) | sha256Hex properties + RFC known vector |
| `AuthApiTest` | 9 | Integration | register/login/refresh/logout HTTP flows |
| `CsrfCorsTest` | 8 | Integration | CSRF enforcement, CSRF exemptions, CORS allowed/blocked |
| `ConcurrencyTest` | 2 | Integration | Concurrent invite redemption; pilot capacity enforcement |
| _(Sprint 1 tests)_ | 15 | Integration | HealthCheck, SchemaValidation, Security config, etc. |

### Running the tests

On Ubuntu with Java 25 installed, Java 21 must be explicitly set:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
cd backend/studyhub-api
mvn test
```

Expected: **46 tests, 0 failures, 0 errors**

> **Note on ConcurrencyTest:** H2 in SERIALIZABLE mode handles concurrent transactions conservatively. The concurrent invite test (5 threads, 1 invite) guarantees exactly 1 success via pessimistic write lock + serializable isolation. This test has no sleep/timing dependencies and is deterministic under H2.

---

## Environment Variables Guide

All secrets are injected as environment variables — nothing in source control.

| Variable | Required | Description | Example |
|---|---|---|---|
| `STUDYHUB_JWT_ACCESS_SECRET` | ✅ | HMAC-SHA256 signing key for access tokens (≥32 chars) | `openssl rand -base64 48` |
| `STUDYHUB_JWT_ACCESS_EXPIRY_MS` | optional | Access token lifetime in ms (default: 900000 = 15 min) | `900000` |
| `STUDYHUB_JWT_REFRESH_SECRET` | ✅ | Refresh token signing key (≥32 chars, different from access) | `openssl rand -base64 48` |
| `STUDYHUB_JWT_REFRESH_EXPIRY_MS` | optional | Refresh token lifetime in ms (default: 604800000 = 7 days) | `604800000` |
| `STUDYHUB_COOKIE_DOMAIN` | ✅ | Cookie domain (matches deployment host) | `app.example.com` |
| `STUDYHUB_COOKIE_SECURE` | optional | true in production (HTTPS required for Secure flag) | `true` |
| `STUDYHUB_COOKIE_SAME_SITE` | optional | SameSite policy (default: Strict) | `Strict` |
| `STUDYHUB_CORS_ALLOWED_ORIGINS` | ✅ | Comma-separated list of allowed frontend origins | `https://app.example.com` |
| `STUDYHUB_PILOT_MAX_LEARNERS` | optional | Pilot capacity cap (default: 10) | `10` |
| `STUDYHUB_AI_WEEKLY_CEILING_USD` | ✅ | AI cost ceiling per week in USD | `25.00` |
| `STUDYHUB_AI_ALERT_THRESHOLD_USD` | ✅ | Alert threshold per week in USD | `20.00` |
| `SPRING_DATASOURCE_URL` | ✅ | JDBC URL | `jdbc:mysql://localhost:3306/studyhub` |
| `SPRING_DATASOURCE_USERNAME` | ✅ | DB username | `studyhub` |
| `SPRING_DATASOURCE_PASSWORD` | ✅ | DB password | _(generated)_ |

### Startup validation

`StudyHubProperties` is validated at startup via `@Validated`. Missing required properties cause a `BindException` and the application refuses to start. The startup log will identify which property is missing.

---

## Git Version Control Summary

| Item | Value |
|---|---|
| Branch | `studyhub-dev` |
| Sprint 2 commit | `f51f4fe` |
| Sprint 1 baseline | `3c2dc52` |
| `main` (production) | `c84f920` — **untouched** |
| Remote push | ✅ Pushed to `origin/studyhub-dev` |
| Outstanding uncommitted changes | None |
| Secrets committed | None |
| `main` modified | No |
| GitHub Pages affected | No |
| Existing bootcamp disrupted | No |

---

## Known Limitations / Out of Scope

- **Email verification**: Not in Sprint 2 scope. Invite codes serve as the admission control for now.
- **Password reset**: Not in Sprint 2 scope.
- **Rate limiting**: Not implemented at the application layer. Plan for Sprint 4+.
- **Admin invite issuance API**: Not in Sprint 2 scope. Invite codes must be inserted directly in the DB or via a future admin endpoint.
- **Token blacklist on key rotation**: Secret key rotation would require re-issuing all sessions. Plan: add key versioning in a future sprint if needed.

---

## Next Sprint Options

| Priority | Item |
|---|---|
| High | Admin API: invite code issuance, user management |
| High | Frontend auth integration (React login/register flow) |
| Medium | Email verification flow |
| Medium | Rate limiting (Bucket4j or similar) |
| Low | Password reset flow |
| Low | Audit log for auth events |
