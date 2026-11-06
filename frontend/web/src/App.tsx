import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ExploreDisciplinePage, ExploreListPage } from './pages/ExplorePage';
import { LessonPage } from './pages/LessonPage';

function AppShell({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ minHeight: '100vh', background: '#f9fafb', color: '#111827' }}>
      <header
        style={{
          borderBottom: '1px solid #e5e7eb',
          background: '#fff',
          padding: '0 1.5rem',
          display: 'flex',
          alignItems: 'center',
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
      </header>
      <div>{children}</div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppShell>
        <Routes>
          <Route path="/" element={<Navigate to="/explore" replace />} />
          <Route path="/explore" element={<ExploreListPage />} />
          <Route path="/explore/:slug" element={<ExploreDisciplinePage />} />
          <Route path="/lessons/:id" element={<LessonPage />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </AppShell>
    </BrowserRouter>
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
