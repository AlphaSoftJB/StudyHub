/**
 * AuthContext — centralized frontend authentication provider.
 *
 * Sprint 4, item A + B.
 *
 * Security constraints (must remain in effect):
 *  - Access tokens stored in-memory ONLY. Never in localStorage, sessionStorage,
 *    IndexedDB, or any browser-readable persistent storage.
 *  - Refresh tokens are httpOnly cookies — never accessible to JavaScript.
 *  - Single-flight refresh: one in-flight Promise<string> prevents concurrent
 *    refresh calls when multiple 401s arrive simultaneously.
 *  - CSRF: login/register are exempt by backend config; refresh/logout and all
 *    protected state-mutating calls require the X-XSRF-TOKEN header (sent
 *    automatically by the Axios request interceptor in client.ts via the
 *    CookieCsrfTokenRepository cookie).
 *  - On mount: attempt POST /api/v1/auth/refresh to restore a valid session
 *    from the existing httpOnly refresh cookie (if any). This is the only
 *    way session state survives a page reload.
 */

import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from 'react';
import { authApi } from '../api/auth';
import type { AuthUser, LoginRequest, RegisterRequest } from '../api/auth';
import { setAccessToken, clearAccessToken, registerRefreshCallback } from '../api/client';

// ── Types ─────────────────────────────────────────────────────────────────────

type AuthStatus = 'loading' | 'authenticated' | 'unauthenticated';

interface AuthState {
  user: AuthUser | null;
  status: AuthStatus;
}

interface AuthContextValue extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  register: (
    email: string,
    password: string,
    displayName: string,
    invitationCode: string,
  ) => Promise<void>;
  logout: () => Promise<void>;
  /**
   * Refresh the access token using the httpOnly refresh cookie.
   * Returns the new access token string on success, or throws on failure.
   * Uses single-flight: concurrent callers all await the same Promise.
   */
  refreshToken: () => Promise<string>;
}

// ── Context ───────────────────────────────────────────────────────────────────

const AuthContext = createContext<AuthContextValue | null>(null);

// ── Provider ──────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    status: 'loading',
  });

  /**
   * Single-flight refresh guard.
   * When a refresh is already in-flight, all callers share the same Promise
   * rather than each issuing a separate POST /api/v1/auth/refresh.
   * This prevents a burst of 401s from triggering multiple concurrent refreshes.
   */
  const refreshPromiseRef = useRef<Promise<string> | null>(null);

  // ── Helpers ──────────────────────────────────────────────────────────────

  const applyAuthResponse = useCallback(
    (res: Awaited<ReturnType<typeof authApi.login>>) => {
      setAccessToken(res.accessToken);
      setState({
        status: 'authenticated',
        user: {
          userId: res.userId,
          email: res.email,
          displayName: res.displayName,
          roles: res.roles,
        },
      });
    },
    [],
  );

  const clearAuth = useCallback(() => {
    clearAccessToken();
    refreshPromiseRef.current = null;
    setState({ user: null, status: 'unauthenticated' });
  }, []);

  // ── Single-flight refresh ─────────────────────────────────────────────────

  const refreshToken = useCallback((): Promise<string> => {
    // Return the existing in-flight promise if one exists
    if (refreshPromiseRef.current !== null) {
      return refreshPromiseRef.current;
    }

    const promise = authApi
      .refresh()
      .then((res) => {
        applyAuthResponse(res);
        return res.accessToken;
      })
      .catch((err) => {
        // Refresh failed — session is gone; clear everything
        clearAuth();
        throw err;
      })
      .finally(() => {
        // Clear the in-flight reference when done (success or failure)
        refreshPromiseRef.current = null;
      });

    refreshPromiseRef.current = promise;
    return promise;
  }, [applyAuthResponse, clearAuth]);

  // ── Session restore on mount ──────────────────────────────────────────────

  useEffect(() => {
    // Attempt to restore a session from the existing httpOnly refresh cookie.
    // This runs once on application initialization. If the cookie is absent or
    // expired, the backend returns 401 and we transition to 'unauthenticated'.
    authApi
      .refresh()
      .then((res) => {
        applyAuthResponse(res);
      })
      .catch(() => {
        // No valid session — that's normal on first visit or after logout
        setState({ user: null, status: 'unauthenticated' });
      });
  }, []); // eslint-disable-line react-hooks/exhaustive-deps
  // applyAuthResponse is stable (useCallback with no deps); omitting to avoid
  // lint warning about missing deps when the intent is "run once on mount"

  // ── Register refresh callback with Axios interceptor ─────────────────────

  useEffect(() => {
    // Wire the single-flight refreshToken into the Axios 401 interceptor.
    // The interceptor in client.ts calls this callback, so it can retry
    // the original request with a fresh token without circular imports.
    registerRefreshCallback(refreshToken);
  }, [refreshToken]);

  // ── Auth methods ──────────────────────────────────────────────────────────

  const login = useCallback(
    async (email: string, password: string) => {
      const req: LoginRequest = { email, password };
      const res = await authApi.login(req);
      applyAuthResponse(res);
    },
    [applyAuthResponse],
  );

  const register = useCallback(
    async (
      email: string,
      password: string,
      displayName: string,
      invitationCode: string,
    ) => {
      const req: RegisterRequest = { email, password, displayName, invitationCode };
      const res = await authApi.register(req);
      applyAuthResponse(res);
    },
    [applyAuthResponse],
  );

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      // Always clear local auth state, even if the backend call fails
      clearAuth();
    }
  }, [clearAuth]);

  // ── Context value ─────────────────────────────────────────────────────────

  const value: AuthContextValue = {
    ...state,
    login,
    register,
    logout,
    refreshToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// ── Hook ──────────────────────────────────────────────────────────────────────

/**
 * useAuth — consume the AuthContext.
 * Must be called inside a component tree wrapped by <AuthProvider>.
 */
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (ctx === null) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}
