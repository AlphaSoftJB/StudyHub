/**
 * Authentication API client.
 *
 * Security constraints (DR-016):
 *  - Access tokens are returned in the response body and stored in-memory only.
 *  - Refresh tokens are NEVER returned here — the backend sets them as httpOnly cookies.
 *  - withCredentials: true ensures the httpOnly refresh cookie is sent on every request.
 *  - CSRF: login/register are exempt; refresh/logout require the CSRF cookie (sent via
 *    CookieCsrfTokenRepository — the browser includes the cookie automatically, and
 *    the backend reads the X-XSRF-TOKEN header set by our Axios interceptor).
 */
import { apiClient } from './client';

// ── Types (mirror backend DTOs) ───────────────────────────────────────────────

export interface AuthUser {
  userId: number;
  email: string;
  displayName: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  userId: number;
  email: string;
  displayName: string;
  roles: string[];
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName: string;
  invitationCode: string;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  fieldErrors?: string[];
}

// ── Auth API ──────────────────────────────────────────────────────────────────

export const authApi = {
  /**
   * POST /api/v1/auth/login
   * Returns access token + sets httpOnly refresh cookie.
   */
  login: (req: LoginRequest): Promise<AuthResponse> =>
    apiClient.post<AuthResponse>('/api/v1/auth/login', req).then((r) => r.data),

  /**
   * POST /api/v1/auth/register
   * Invite-only. Returns access token + sets httpOnly refresh cookie on success.
   */
  register: (req: RegisterRequest): Promise<AuthResponse> =>
    apiClient.post<AuthResponse>('/api/v1/auth/register', req).then((r) => r.data),

  /**
   * POST /api/v1/auth/refresh
   * Uses httpOnly refresh cookie (sent automatically by browser via withCredentials).
   * Returns a new access token + rotates the refresh cookie.
   * Does NOT require an Authorization header.
   */
  refresh: (): Promise<AuthResponse> =>
    apiClient.post<AuthResponse>('/api/v1/auth/refresh').then((r) => r.data),

  /**
   * POST /api/v1/auth/logout
   * Revokes current refresh token; clears the cookie server-side.
   */
  logout: (): Promise<void> =>
    apiClient.post('/api/v1/auth/logout').then(() => undefined),
};
