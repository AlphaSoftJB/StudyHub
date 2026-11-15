/**
 * RequireAuth — route guard component.
 *
 * Sprint 4, item E.
 *
 * Redirects unauthenticated users to /login.
 * Passes the originally requested path as location.state.from so the
 * LoginPage can redirect back after a successful sign-in.
 *
 * While the auth status is still loading (session restore in progress),
 * renders a loading indicator to prevent a flash-of-redirect.
 */

import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface RequireAuthProps {
  children: React.ReactNode;
}

export function RequireAuth({ children }: RequireAuthProps) {
  const { status } = useAuth();
  const location = useLocation();

  if (status === 'loading') {
    // Session restore is in-flight — don't redirect yet
    return (
      <div
        role="status"
        aria-label="Loading"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '50vh',
          color: '#6b7280',
          fontSize: '0.875rem',
        }}
      >
        Loading…
      </div>
    );
  }

  if (status === 'unauthenticated') {
    // Pass the current location so LoginPage can redirect back after sign-in
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  return <>{children}</>;
}
