/**
 * Axios client — JWT stored in-memory only (never localStorage).
 *
 * DR-016: Access tokens in-memory; refresh tokens in httpOnly cookie.
 *
 * Sprint 4 additions:
 *  - CSRF: reads XSRF-TOKEN cookie and sends as X-XSRF-TOKEN header on
 *    state-mutating requests (POST, PUT, PATCH, DELETE). The backend uses
 *    CookieCsrfTokenRepository — the cookie is NOT httpOnly so JS can read it.
 *    Login/register are exempted server-side; refresh/logout are NOT.
 *  - 401 retry: if a request gets a 401, attempt one token refresh via the
 *    registered callback (set by AuthContext), then retry the original request.
 *    Single-flight is enforced inside the callback (see AuthContext.tsx).
 */
import axios from 'axios';
import type { AxiosRequestConfig } from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // sends httpOnly refresh token cookie
  headers: { 'Content-Type': 'application/json' },
});

// ── In-memory JWT store ───────────────────────────────────────────────────────
// Intentionally NOT localStorage — security constraint (DR-016)

let _accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  _accessToken = token;
}

export function clearAccessToken() {
  _accessToken = null;
}

// ── Refresh callback ──────────────────────────────────────────────────────────
// AuthContext registers its refreshToken() method here so the interceptor
// below can trigger a refresh without a circular import.

type RefreshCallback = () => Promise<string>;
let _refreshCallback: RefreshCallback | null = null;

export function registerRefreshCallback(cb: RefreshCallback) {
  _refreshCallback = cb;
}

// ── CSRF helper ───────────────────────────────────────────────────────────────

function getCsrfToken(): string | null {
  const match = document.cookie
    .split(';')
    .map((c) => c.trim())
    .find((c) => c.startsWith('XSRF-TOKEN='));
  return match ? decodeURIComponent(match.split('=')[1]) : null;
}

const STATE_MUTATING_METHODS = new Set(['post', 'put', 'patch', 'delete']);

// ── Request interceptor ───────────────────────────────────────────────────────

apiClient.interceptors.request.use((config) => {
  // 1. Attach JWT Bearer token when present
  if (_accessToken) {
    config.headers.Authorization = `Bearer ${_accessToken}`;
  }

  // 2. Attach CSRF token for state-mutating methods
  //    Backend exempts login/register; all other POST/PUT/PATCH/DELETE need it.
  const method = (config.method ?? '').toLowerCase();
  if (STATE_MUTATING_METHODS.has(method)) {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
  }

  return config;
});

// ── Response interceptor — 401 retry with single-flight refresh ───────────────

// Track which requests have already been retried to prevent infinite loops
const RETRY_FLAG = '__authRetry';

apiClient.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalConfig = err.config as AxiosRequestConfig & { [RETRY_FLAG]?: boolean };

    // Only attempt refresh on 401 responses that haven't been retried yet,
    // and only when we have a registered refresh callback.
    if (
      err.response?.status === 401 &&
      !originalConfig[RETRY_FLAG] &&
      _refreshCallback !== null
    ) {
      originalConfig[RETRY_FLAG] = true;

      try {
        // Single-flight refresh: if multiple 401s fire concurrently,
        // AuthContext.refreshToken() returns the same in-flight Promise
        const newToken = await _refreshCallback();

        // Retry the original request with the new token
        if (originalConfig.headers) {
          originalConfig.headers.Authorization = `Bearer ${newToken}`;
        }
        return apiClient(originalConfig);
      } catch {
        // Refresh failed — AuthContext has already cleared the session.
        // Let the original 401 error propagate.
        clearAccessToken();
        return Promise.reject(err);
      }
    }

    // Non-401, or already retried — propagate
    return Promise.reject(err);
  },
);
