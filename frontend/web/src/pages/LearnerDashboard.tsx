import { Link } from 'react-router-dom';
import { ErrorMessage, LoadingSpinner } from '../components/LoadingSpinner';
import { useDashboard } from '../hooks/useProgress';
import type { CourseSummary } from '../types/progress';

// ── Stat tile ─────────────────────────────────────────────────────────────────

function StatTile({ label, value }: { label: string; value: number | string }) {
  return (
    <div
      style={{
        background: '#f9fafb',
        border: '1px solid #e5e7eb',
        borderRadius: '0.5rem',
        padding: '1.25rem 1.5rem',
        minWidth: '8rem',
        flex: '1 1 8rem',
      }}
    >
      <div
        style={{
          fontSize: '1.75rem',
          fontWeight: 700,
          color: '#111827',
          lineHeight: 1,
          marginBottom: '0.25rem',
        }}
      >
        {value}
      </div>
      <div style={{ fontSize: '0.8125rem', color: '#6b7280', fontWeight: 500 }}>
        {label}
      </div>
    </div>
  );
}

// ── Progress bar ──────────────────────────────────────────────────────────────

function ProgressBar({ pct }: { pct: number }) {
  return (
    <div
      style={{
        height: '0.5rem',
        borderRadius: '9999px',
        background: '#e5e7eb',
        overflow: 'hidden',
      }}
    >
      <div
        style={{
          height: '100%',
          width: `${pct}%`,
          background: pct === 100 ? '#16a34a' : '#2563eb',
          borderRadius: '9999px',
          transition: 'width 0.4s ease',
        }}
      />
    </div>
  );
}

// ── Course card ───────────────────────────────────────────────────────────────

function CourseCard({ course }: { course: CourseSummary }) {
  return (
    <Link
      to={`/explore/courses/${course.courseId}`}
      style={{ textDecoration: 'none', color: 'inherit' }}
    >
      <div
        style={{
          border: '1px solid #e5e7eb',
          borderRadius: '0.5rem',
          padding: '1rem 1.25rem',
          transition: 'box-shadow 0.15s',
          cursor: 'pointer',
        }}
        onMouseEnter={(e) =>
          ((e.currentTarget as HTMLDivElement).style.boxShadow =
            '0 1px 6px rgba(0,0,0,0.1)')
        }
        onMouseLeave={(e) =>
          ((e.currentTarget as HTMLDivElement).style.boxShadow = 'none')
        }
      >
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            gap: '1rem',
            marginBottom: '0.625rem',
          }}
        >
          <span style={{ fontWeight: 600, fontSize: '0.9375rem' }}>
            {course.courseTitle}
          </span>
          <span
            style={{
              fontSize: '0.8125rem',
              color: course.completionPercent === 100 ? '#16a34a' : '#6b7280',
              fontWeight: course.completionPercent === 100 ? 600 : 400,
              whiteSpace: 'nowrap',
            }}
          >
            {course.completedLessons}/{course.publishedLessons} lessons ·{' '}
            {course.completionPercent}%
          </span>
        </div>
        <ProgressBar pct={course.completionPercent} />
      </div>
    </Link>
  );
}

// ── Page ──────────────────────────────────────────────────────────────────────

export function LearnerDashboard() {
  const { data: dashboard, loading, error } = useDashboard();

  if (loading) return <LoadingSpinner label="Loading dashboard…" />;
  if (error) return <ErrorMessage message={error} />;
  if (!dashboard) return <ErrorMessage message="Dashboard unavailable." />;

  return (
    <main style={{ padding: '2rem', maxWidth: '860px', margin: '0 auto' }}>
      {/* Header */}
      <header style={{ marginBottom: '2rem' }}>
        <h1 style={{ margin: 0, fontSize: '1.75rem', lineHeight: 1.3 }}>
          Welcome back, {dashboard.displayName}
        </h1>
      </header>

      {/* Stats row */}
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: '1rem',
          marginBottom: '2.5rem',
        }}
      >
        <StatTile label="Lessons completed" value={dashboard.completedLessons} />
        <StatTile label="Total XP" value={dashboard.totalXp} />
        <StatTile label="Courses in progress" value={dashboard.courses.length} />
      </div>

      {/* Course progress */}
      <section>
        <h2
          style={{
            margin: '0 0 1rem',
            fontSize: '1.125rem',
            fontWeight: 600,
            color: '#374151',
          }}
        >
          Course Progress
        </h2>

        {dashboard.courses.length === 0 ? (
          <div
            style={{
              textAlign: 'center',
              padding: '3rem 1rem',
              color: '#9ca3af',
              border: '1px dashed #d1d5db',
              borderRadius: '0.5rem',
            }}
          >
            <p style={{ margin: '0 0 1rem', fontSize: '1rem' }}>
              No progress yet. Start a lesson to track your progress here.
            </p>
            <Link
              to="/explore"
              style={{
                color: '#2563eb',
                fontWeight: 600,
                textDecoration: 'none',
              }}
            >
              Browse courses →
            </Link>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {dashboard.courses.map((course) => (
              <CourseCard key={course.courseId} course={course} />
            ))}
          </div>
        )}
      </section>
    </main>
  );
}
