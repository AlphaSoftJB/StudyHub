/**
 * RegisterPage form tests (Sprint 4 — S4-12, S4-13).
 *
 * Tests:
 *  - Renders all four fields: name, email, password, invitation code
 *  - Validates: empty name, invalid email, short password, missing/invalid code
 *  - UUID format validation for invitation code
 *  - Successful registration calls register() with correct arguments
 *  - 409 conflict shows email-already-exists error
 *  - 422 shows invalid-invitation-code error
 *  - Redirects to /explore on success
 */

import React from 'react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';
import { RegisterPage } from '../pages/RegisterPage';
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

const VALID_INVITE = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee';

const MOCK_AUTH_RESPONSE = {
  accessToken: 'token',
  tokenType: 'Bearer',
  expiresInSeconds: 900,
  userId: 1,
  email: 'new@example.com',
  displayName: 'New User',
  roles: ['ROLE_LEARNER'],
};

function ExplorePage() {
  return <div data-testid="explore-page">Explore</div>;
}

function renderRegister() {
  return render(
    <MemoryRouter initialEntries={['/register']}>
      <AuthProvider>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/explore" element={<ExplorePage />} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  );
}

async function fillValidForm(user: ReturnType<typeof userEvent.setup>) {
  await user.type(screen.getByLabelText(/full name/i), 'New User');
  await user.type(screen.getByLabelText(/email address/i), 'new@example.com');
  await user.type(screen.getByLabelText(/password/i), 'SecurePass1');
  await user.type(screen.getByLabelText(/invitation code/i), VALID_INVITE);
}

describe('RegisterPage', () => {
  const mockedAuthApi = vi.mocked(authApi.authApi);

  beforeEach(() => {
    vi.clearAllMocks();
    mockedAuthApi.refresh.mockRejectedValue({ response: { status: 401 } });
  });

  it('renders all four fields', async () => {
    renderRegister();
    await waitFor(() =>
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument(),
    );
    expect(screen.getByLabelText(/email address/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/invitation code/i)).toBeInTheDocument();
  });

  it('validates empty name', async () => {
    const user = userEvent.setup();
    renderRegister();
    // The form renders during loading; we just need to find and submit it.
    // findByRole waits up to 1s for the element to appear.
    const btn = await screen.findByRole('button', { name: /create account/i });
    await user.click(btn);
    // Validation runs synchronously in handleSubmit, so error appears immediately
    await waitFor(() =>
      expect(screen.getByText('Full name is required')).toBeInTheDocument(),
    );
  });

  it('validates empty invitation code', async () => {
    const user = userEvent.setup();
    renderRegister();
    const btn = await screen.findByRole('button', { name: /create account/i });
    await user.click(btn);
    await waitFor(() =>
      expect(screen.getByText('Invitation code is required')).toBeInTheDocument(),
    );
  });

  it('validates malformed invitation code (not UUID)', async () => {
    const user = userEvent.setup();
    renderRegister();
    const codeInput = await screen.findByLabelText(/invitation code/i);
    await user.type(codeInput, 'not-a-uuid');
    await user.click(screen.getByRole('button', { name: /create account/i }));
    await waitFor(() =>
      expect(
        screen.getByText(/valid invitation code/i),
      ).toBeInTheDocument(),
    );
  });

  it('validates short password (< 8 chars)', async () => {
    const user = userEvent.setup();
    renderRegister();
    // Fill name + email to avoid those errors; test only the password error
    const nameInput = await screen.findByLabelText(/full name/i);
    await user.type(nameInput, 'Test User');
    await user.type(screen.getByLabelText(/email address/i), 'test@test.com');
    await user.type(screen.getByLabelText(/password/i), 'short');
    await user.click(screen.getByRole('button', { name: /create account/i }));
    await waitFor(() =>
      expect(screen.getByText('Password must be at least 8 characters')).toBeInTheDocument(),
    );
  });

  it('calls register() with correct arguments on valid submission', async () => {
    const user = userEvent.setup();
    mockedAuthApi.register.mockResolvedValue(MOCK_AUTH_RESPONSE);
    renderRegister();

    await waitFor(() =>
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument(),
    );

    await fillValidForm(user);
    await user.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() =>
      expect(mockedAuthApi.register).toHaveBeenCalledWith({
        email: 'new@example.com',
        password: 'SecurePass1',
        displayName: 'New User',
        invitationCode: VALID_INVITE,
      }),
    );
  });

  it('redirects to /explore on successful registration', async () => {
    const user = userEvent.setup();
    mockedAuthApi.register.mockResolvedValue(MOCK_AUTH_RESPONSE);
    renderRegister();

    await waitFor(() =>
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument(),
    );

    await fillValidForm(user);
    await user.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() =>
      expect(screen.getByTestId('explore-page')).toBeInTheDocument(),
    );
  });

  it('shows email-already-exists error on 409', async () => {
    const user = userEvent.setup();
    mockedAuthApi.register.mockRejectedValue({ response: { status: 409 } });
    renderRegister();

    await waitFor(() =>
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument(),
    );

    await fillValidForm(user);
    await user.click(screen.getByRole('button', { name: /create account/i }));

    expect(
      await screen.findByText(/account with this email already exists/i),
    ).toBeInTheDocument();
  });

  it('shows invalid invitation code error on 422', async () => {
    const user = userEvent.setup();
    mockedAuthApi.register.mockRejectedValue({ response: { status: 422 } });
    renderRegister();

    await waitFor(() =>
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument(),
    );

    await fillValidForm(user);
    await user.click(screen.getByRole('button', { name: /create account/i }));

    expect(
      await screen.findByText(/invalid or already-used invitation code/i),
    ).toBeInTheDocument();
  });
});
