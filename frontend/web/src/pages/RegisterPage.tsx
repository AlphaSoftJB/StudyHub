/**
 * RegisterPage — invite-only registration form.
 *
 * Sprint 4, item D.
 *
 * Fields: name, email, password, invitationCode (UUID, 36 chars).
 *
 * Security:
 *  - Invite-only: invitationCode is required; backend validates it's a known UUID.
 *  - DO NOT bypass invitation-only registration (security constraint).
 *  - Credentials are POSTed to /api/v1/auth/register via authApi.register().
 *  - The access token is stored in-memory only (handled by AuthContext).
 *  - Registration is CSRF-exempt server-side.
 */

import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// ── Shared form styles (matches LoginPage) ────────────────────────────────────

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
    maxWidth: '420px',
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

  hint: {
    display: 'block',
    fontSize: '0.75rem',
    color: '#6b7280',
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
    fontFamily: 'inherit',
  } as React.CSSProperties,

  inputError: {
    borderColor: '#ef4444',
  } as React.CSSProperties,

  inputMono: {
    fontFamily: 'monospace',
    letterSpacing: '0.03em',
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

// ── UUID pattern (36-char: 8-4-4-4-12) ───────────────────────────────────────
const UUID_RE =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

// ── Component ─────────────────────────────────────────────────────────────────

export function RegisterPage() {
  const { register, status } = useAuth();
  const navigate = useNavigate();

  // Redirect if already authenticated
  if (status === 'authenticated') {
    return <Navigate to="/explore" replace />;
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.heading}>Create account</h1>
        <p style={styles.subheading}>StudyHub is invite-only. You'll need an invitation code.</p>
        <RegisterForm
          onSuccess={() => navigate('/explore', { replace: true })}
          register={register}
        />
        <p style={styles.footer}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#2563eb' }}>
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}

// ── RegisterForm — isolated for testability ───────────────────────────────────

interface RegisterFormProps {
  onSuccess: () => void;
  register: (
    email: string,
    password: string,
    displayName: string,
    invitationCode: string,
  ) => Promise<void>;
}

function RegisterForm({ onSuccess, register }: RegisterFormProps) {
  const [displayName, setDisplayName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [invitationCode, setInvitationCode] = useState('');

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [serverError, setServerError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  function validate(): boolean {
    const next: Record<string, string> = {};

    // Keys must match the HTML id attributes passed to field() below
    if (!displayName.trim()) {
      next['reg-name'] = 'Full name is required';
    } else if (displayName.trim().length < 2) {
      next['reg-name'] = 'Name must be at least 2 characters';
    }

    if (!email.trim()) {
      next['reg-email'] = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      next['reg-email'] = 'Enter a valid email address';
    }

    if (!password) {
      next['reg-password'] = 'Password is required';
    } else if (password.length < 8) {
      next['reg-password'] = 'Password must be at least 8 characters';
    }

    const trimmedCode = invitationCode.trim();
    if (!trimmedCode) {
      next['reg-code'] = 'Invitation code is required';
    } else if (!UUID_RE.test(trimmedCode)) {
      next['reg-code'] = 'Enter a valid invitation code (xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)';
    }

    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setServerError('');

    if (!validate()) return;

    setIsLoading(true);
    try {
      await register(email, password, displayName.trim(), invitationCode.trim());
      onSuccess();
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;

      if (status === 400) {
        setServerError(message ?? 'Please check your details and try again.');
      } else if (status === 409) {
        setErrors((prev) => ({
          ...prev,
          'reg-email': 'An account with this email already exists.',
        }));
      } else if (status === 422) {
        setServerError('Invalid or already-used invitation code.');
      } else {
        setServerError('Something went wrong. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  }

  const isDisabled = isLoading;

  function field(
    id: string,
    label: string,
    type: string,
    value: string,
    onChange: (v: string) => void,
    opts?: {
      autoComplete?: string;
      hint?: string;
      mono?: boolean;
      placeholder?: string;
    },
  ) {
    const err = errors[id];
    return (
      <div style={styles.field}>
        <label htmlFor={id} style={styles.label}>
          {label}
        </label>
        {opts?.hint && <span style={styles.hint}>{opts.hint}</span>}
        <input
          id={id}
          type={type}
          autoComplete={opts?.autoComplete}
          placeholder={opts?.placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          disabled={isDisabled}
          aria-describedby={err ? `${id}-error` : undefined}
          aria-invalid={!!err}
          style={{
            ...styles.input,
            ...(err ? styles.inputError : {}),
            ...(opts?.mono ? styles.inputMono : {}),
          }}
        />
        {err && (
          <p id={`${id}-error`} style={styles.fieldError}>
            {err}
          </p>
        )}
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      {serverError && <div style={styles.errorBanner}>{serverError}</div>}

      {field('reg-name', 'Full name', 'text', displayName, setDisplayName, {
        autoComplete: 'name',
      })}
      {field('reg-email', 'Email address', 'email', email, setEmail, {
        autoComplete: 'email',
      })}
      {field('reg-password', 'Password', 'password', password, setPassword, {
        autoComplete: 'new-password',
        hint: 'At least 8 characters',
      })}
      {field('reg-code', 'Invitation code', 'text', invitationCode, setInvitationCode, {
        autoComplete: 'off',
        hint: 'Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
        mono: true,
        placeholder: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
      })}

      <button
        type="submit"
        disabled={isDisabled}
        style={{
          ...styles.submitButton,
          ...(isDisabled ? styles.submitButtonDisabled : {}),
        }}
      >
        {isLoading ? 'Creating account…' : 'Create account'}
      </button>
    </form>
  );
}
