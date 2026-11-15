/**
 * LoginPage — email + password login form.
 *
 * Sprint 4, item C.
 *
 * Security:
 *  - Credentials are POSTed to /api/v1/auth/login via authApi.login().
 *  - The access token is stored in-memory only (handled by AuthContext).
 *  - Login is CSRF-exempt server-side; no CSRF token needed here.
 *  - Redirects to /explore on success (or to the originally requested path
 *    if the router provided a redirect location).
 */

import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// ── Shared form styles (inline — no CSS module dep needed for Sprint 4) ───────

const styles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '2rem 1rem',
    background: '#f9fafb',
  } as React.CSSProperties,

  card: {
    background: '#fff',
    border: '1px solid #e5e7eb',
    borderRadius: '0.75rem',
    padding: '2.5rem 2rem',
    width: '100%',
    maxWidth: '400px',
  } as React.CSSProperties,

  heading: {
    fontSize: '1.5rem',
    fontWeight: 700,
    color: '#111827',
    marginBottom: '0.25rem',
    letterSpacing: '-0.02em',
  } as React.CSSProperties,

  subheading: {
    fontSize: '0.875rem',
    color: '#6b7280',
    marginBottom: '2rem',
  } as React.CSSProperties,

  field: {
    marginBottom: '1rem',
  } as React.CSSProperties,

  label: {
    display: 'block',
    fontSize: '0.875rem',
    fontWeight: 500,
    color: '#374151',
    marginBottom: '0.375rem',
  } as React.CSSProperties,

  input: {
    display: 'block',
    width: '100%',
    padding: '0.5rem 0.75rem',
    fontSize: '0.875rem',
    color: '#111827',
    background: '#fff',
    border: '1px solid #d1d5db',
    borderRadius: '0.375rem',
    outline: 'none',
    boxSizing: 'border-box',
  } as React.CSSProperties,

  inputError: {
    borderColor: '#ef4444',
  } as React.CSSProperties,

  fieldError: {
    fontSize: '0.75rem',
    color: '#dc2626',
    marginTop: '0.25rem',
  } as React.CSSProperties,

  errorBanner: {
    background: '#fef2f2',
    border: '1px solid #fecaca',
    borderRadius: '0.5rem',
    padding: '0.75rem 1rem',
    marginBottom: '1.25rem',
    fontSize: '0.875rem',
    color: '#b91c1c',
  } as React.CSSProperties,

  submitButton: {
    display: 'block',
    width: '100%',
    padding: '0.625rem 1rem',
    fontSize: '0.875rem',
    fontWeight: 600,
    color: '#fff',
    background: '#2563eb',
    border: 'none',
    borderRadius: '0.375rem',
    cursor: 'pointer',
    marginTop: '1.5rem',
    letterSpacing: '0.01em',
  } as React.CSSProperties,

  submitButtonDisabled: {
    background: '#93c5fd',
    cursor: 'not-allowed',
  } as React.CSSProperties,

  footer: {
    marginTop: '1.5rem',
    textAlign: 'center' as const,
    fontSize: '0.875rem',
    color: '#6b7280',
  } as React.CSSProperties,
};

// ── Component ─────────────────────────────────────────────────────────────────

export function LoginPage() {
  const { login, status } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Redirect to /explore if already authenticated
  if (status === 'authenticated') {
    const from = (location.state as { from?: string })?.from ?? '/explore';
    return <Navigate to={from} replace />;
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.heading}>Sign in</h1>
        <p style={styles.subheading}>Welcome back to StudyHub</p>
        <LoginForm
          onSuccess={() => {
            const from = (location.state as { from?: string })?.from ?? '/explore';
            navigate(from, { replace: true });
          }}
          login={login}
        />
        <p style={styles.footer}>
          Need access?{' '}
          <Link to="/register" style={{ color: '#2563eb' }}>
            Register with an invitation code
          </Link>
        </p>
      </div>
    </div>
  );
}

// ── LoginForm — isolated for testability ─────────────────────────────────────

interface LoginFormProps {
  onSuccess: () => void;
  login: (email: string, password: string) => Promise<void>;
}

function LoginForm({ onSuccess, login }: LoginFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [serverError, setServerError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  function validate(): boolean {
    let valid = true;
    setEmailError('');
    setPasswordError('');

    if (!email.trim()) {
      setEmailError('Email is required');
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setEmailError('Enter a valid email address');
      valid = false;
    }

    if (!password) {
      setPasswordError('Password is required');
      valid = false;
    }

    return valid;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setServerError('');

    if (!validate()) return;

    setIsLoading(true);
    try {
      await login(email, password);
      onSuccess();
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (status === 401) {
        setServerError('Invalid email or password.');
      } else if (status === 429) {
        setServerError('Too many login attempts. Please wait a moment and try again.');
      } else {
        setServerError('Something went wrong. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  }

  const isDisabled = isLoading;

  return (
    <form onSubmit={handleSubmit} noValidate>
      {serverError && <div style={styles.errorBanner}>{serverError}</div>}

      <div style={styles.field}>
        <label htmlFor="login-email" style={styles.label}>
          Email address
        </label>
        <input
          id="login-email"
          type="email"
          autoComplete="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={{ ...styles.input, ...(emailError ? styles.inputError : {}) }}
          disabled={isDisabled}
          aria-describedby={emailError ? 'login-email-error' : undefined}
          aria-invalid={!!emailError}
        />
        {emailError && (
          <p id="login-email-error" style={styles.fieldError}>
            {emailError}
          </p>
        )}
      </div>

      <div style={styles.field}>
        <label htmlFor="login-password" style={styles.label}>
          Password
        </label>
        <input
          id="login-password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{ ...styles.input, ...(passwordError ? styles.inputError : {}) }}
          disabled={isDisabled}
          aria-describedby={passwordError ? 'login-password-error' : undefined}
          aria-invalid={!!passwordError}
        />
        {passwordError && (
          <p id="login-password-error" style={styles.fieldError}>
            {passwordError}
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={isDisabled}
        style={{
          ...styles.submitButton,
          ...(isDisabled ? styles.submitButtonDisabled : {}),
        }}
      >
        {isLoading ? 'Signing in…' : 'Sign in'}
      </button>
    </form>
  );
}
