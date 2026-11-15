import React from 'react';
import { BrowserRouter, Link, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { RequireAuth } from './components/RequireAuth';
import { ExploreDisciplinePage, ExploreListPage } from './pages/ExplorePage';
import { LearnerDashboard } from './pages/LearnerDashboard';
import { LessonPage } from './pages/LessonPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { CourseDetailPage } from './pages/CourseDetailPage';

// ── AppShell ──────────────────────────────────────────────────────────────────

function AppShell({ children }: { children: React.ReactNode }) {
  const { status, user, logout } = useAuth();

  return (
    <div style={{ minHeight: '100vh', background: '#f9fafb', color: '#111827' }}>
      <header
        style={{
          borderBottom: '1px solid #e5e7eb',
          background: '#fff',
          padding: '0 1.5rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          height: '56px',
        }}
      >
        <a
          href="/explore"
          style={{
            fontWeight: 700,
            fontSize: '1.125rem',
            color: '#111827',
            textDecoration: 'none',
            letterSpacing: '-0.01em',
          }}
        >
          StudyHub
        </a>

        <nav style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          {status === 'loading' && (
            <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Loading…</span>
          )}

          {status === 'unauthenticated' && (
            <>
              <Link
                to="/login"
                style={{ fontSize: '0.875rem', color: '#2563eb', textDecoration: 'none' }}
              >
                Sign in
              </Link>
              <Link
                to="/register"
                style={{
                  fontSize: '0.875rem',
                  color: '#fff',
                  background: '#2563eb',
                  padding: '0.375rem 0.75rem',
                  borderRadius: '0.375rem',
                  textDecoration: 'none',
                }}
              >
                Register
              </Link>
            </>
          )}

          {status === 'authenticated' && user && (
            <>
              <Link
                to="/dashboard"
                style={{ fontSize: '0.875rem', color: '#2563eb', textDecoration: 'none' }}
              >
                Dashboard
              </Link>
              <span style={{ fontSize: '0.875rem', color: '#374151' }}>
                {user.displayName}
              </span>
              <button
                onClick={() => logout()}
                style={{
                  fontSize: '0.875rem',
                  color: '#6b7280',
                  background: 'none',
                  border: '1px solid #e5e7eb',
                  padding: '0.25rem 0.625rem',
                  borderRadius: '0.375rem',
                  cursor: 'pointer',
                }}
              >
                Sign out
              </button>
            </>
          )}
        </nav>
      </header>

      <div>{children}</div>
    </div>
  );
}

// ── Root App ──────────────────────────────────────────────────────────────────

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppShell>
          <Routes>
            <Route path="/" element={<Navigate to="/explore" replace />} />
            <Route
              path="/explore"
              element={
                <RequireAuth>
                  <ExploreListPage />
                </RequireAuth>
              }
            />
            <Route
              path="/explore/:slug"
              element={
                <RequireAuth>
                  <ExploreDisciplinePage />
                </RequireAuth>
              }
            />
            <Route
              path="/explore/:slug/courses/:id"
              element={
                <RequireAuth>
                  <CourseDetailPage />
                </RequireAuth>
              }
            />
            <Route
              path="/lessons/:id"
              element={
                <RequireAuth>
                  <LessonPage />
                </RequireAuth>
              }
            />
            <Route
              path="/dashboard"
              element={
                <RequireAuth>
                  <LearnerDashboard />
                </RequireAuth>
              }
            />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </AppShell>
      </BrowserRouter>
    </AuthProvider>
  );
}

function NotFound() {
  return (
    <main style={{ padding: '4rem 2rem', textAlign: 'center' }}>
      <h1 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>Page not found</h1>
      <p style={{ color: '#6b7280' }}>
        <a href="/explore" style={{ color: '#2563eb' }}>Back to Explore</a>
      </p>
    </main>
  );
}
