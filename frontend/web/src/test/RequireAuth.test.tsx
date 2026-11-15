/**
 * RequireAuth (protected route) tests (Sprint 4 — S4-08, S4-09).
 *
 * Tests:
 *  - Unauthenticated user is redirected to /login with `from` state
 *  - Authenticated user can access the protected route
 *  - Loading state renders loading indicator (no flash-redirect)
 */

import React from 'react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { RequireAuth } from '../components/RequireAuth';
import * as authApi from '../api/auth';

vi.mock('../api/auth', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
    refresh: vi.fn(),
    logout: vi.fn(),
  },
}));

vi.mock('../api/client', () => ({
  setAccessToken: vi.fn(),
  clearAccessToken: vi.fn(),
  registerRefreshCallback: vi.fn(),
  apiClient: {
    post: vi.fn(),
    get: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  },
}));

const MOCK_AUTH_RESPONSE = {
  accessToken: 'mock-token',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  userId: 1,
  email: 'user@example.com',
  displayName: 'User',
  roles: ['ROLE_LEARNER'],
};

function ProtectedContent() {
  return <div data-testid="protected">Protected Content</div>;
}

function LoginPage() {
  return <div data-testid="login-page">Login Page</div>;
}

function renderRoutes(initialPath = '/protected') {
  return render(
    <MemoryRouter initialEntries={[initialPath]}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/protected"
            element={
              <RequireAuth>
                <ProtectedContent />
              </RequireAuth>
            }
          />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  );
}

describe('RequireAuth', () => {
  const mockedAuthApi = vi.mocked(authApi.authApi);

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('redirects unauthenticated user to /login', async () => {
    mockedAuthApi.refresh.mockRejectedValue({ response: { status: 401 } });
    renderRoutes();

    // After session restore settles to unauthenticated → redirect to /login
    await waitFor(() =>
      expect(screen.getByTestId('login-page')).toBeInTheDocument(),
    );
    expect(screen.queryByTestId('protected')).not.toBeInTheDocument();
  });

  it('renders protected content for authenticated user', async () => {
    mockedAuthApi.refresh.mockResolvedValue(MOCK_AUTH_RESPONSE);
    renderRoutes();

    await waitFor(() =>
      expect(screen.getByTestId('protected')).toBeInTheDocument(),
    );
    expect(screen.queryByTestId('login-page')).not.toBeInTheDocument();
  });

  it('shows loading indicator during session restore (no flash-redirect)', () => {
    // Never resolves — keep status at 'loading'
    mockedAuthApi.refresh.mockReturnValue(new Promise(() => {}));
    renderRoutes();

    // Should show loading, not redirect
    expect(screen.getByRole('status')).toBeInTheDocument();
    expect(screen.queryByTestId('login-page')).not.toBeInTheDocument();
    expect(screen.queryByTestId('protected')).not.toBeInTheDocument();
  });
});
