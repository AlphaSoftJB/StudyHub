/**
 * LoginPage form tests (Sprint 4 — S4-10, S4-11).
 *
 * Tests:
 *  - Login form renders with email/password fields and submit button
 *  - Validation: empty email → error; invalid email → error; empty password → error
 *  - Successful submission calls login() with correct credentials
 *  - Server 401 displays appropriate error message
 *  - Loading state disables the submit button
 *  - Redirects to /explore on success
 */

import React from 'react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { LoginPage } from '../pages/LoginPage';
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
  accessToken: 'token',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  userId: 1,
  email: 'learner@example.com',
  displayName: 'Learner',
  roles: ['ROLE_LEARNER'],
};

function ExplorePage() {
  return <div data-testid="explore-page">Explore</div>;
}

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/explore" element={<ExplorePage />} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  );
}

describe('LoginPage', () => {
  const mockedAuthApi = vi.mocked(authApi.authApi);

  beforeEach(() => {
    vi.clearAllMocks();
    // Session restore: no existing session
    mockedAuthApi.refresh.mockRejectedValue({ response: { status: 401 } });
  });

  it('renders email, password fields and sign in button', async () => {
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  it('shows email validation error for empty email', async () => {
    const user = userEvent.setup();
    renderLogin();

    await waitFor(() =>
      expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument(),
    );

    await user.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/email is required/i)).toBeInTheDocument();
  });

  it('shows email validation error for invalid email format', async () => {
    const user = userEvent.setup();
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'not-an-email');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/valid email/i)).toBeInTheDocument();
  });

  it('shows password validation error for empty password', async () => {
    const user = userEvent.setup();
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'valid@example.com');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/password is required/i)).toBeInTheDocument();
  });

  it('calls login() with email and password on valid submission', async () => {
    const user = userEvent.setup();
    mockedAuthApi.login.mockResolvedValue(MOCK_AUTH_RESPONSE);
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'learner@example.com');
    await user.type(screen.getByLabelText(/password/i), 'password123');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() =>
      expect(mockedAuthApi.login).toHaveBeenCalledWith({
        email: 'learner@example.com',
        password: 'password123',
      }),
    );
  });

  it('redirects to /explore on successful login', async () => {
    const user = userEvent.setup();
    mockedAuthApi.login.mockResolvedValue(MOCK_AUTH_RESPONSE);
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'learner@example.com');
    await user.type(screen.getByLabelText(/password/i), 'password123');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() =>
      expect(screen.getByTestId('explore-page')).toBeInTheDocument(),
    );
  });

  it('shows server error message on 401', async () => {
    const user = userEvent.setup();
    mockedAuthApi.login.mockRejectedValue({
      response: { status: 401 },
    });
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'bad@example.com');
    await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/invalid email or password/i)).toBeInTheDocument();
  });

  it('disables submit button while loading', async () => {
    const user = userEvent.setup();
    // Never resolves — simulates in-flight request
    mockedAuthApi.login.mockReturnValue(new Promise(() => {}));
    renderLogin();

    await waitFor(() =>
      expect(screen.getByLabelText(/email address/i)).toBeInTheDocument(),
    );

    await user.type(screen.getByLabelText(/email address/i), 'learner@example.com');
    await user.type(screen.getByLabelText(/password/i), 'password123');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() =>
      expect(screen.getByRole('button', { name: /signing in/i })).toBeDisabled(),
    );
  });
});
