/**
 * CourseDetailPage — displays a course's modules and published lessons.
 *
 * Sprint 4, item F (frontend half — FV-002 fix).
 *
 * Route: /explore/:slug/courses/:id
 * Fetches: GET /api/v1/courses/:id
 *
 * Only PUBLISHED lessons are shown (enforced server-side by DR-012;
 * this component trusts the backend response and renders whatever
 * lessons are returned).
 */

import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { curriculumApi } from '../api/curriculum';
import type { CourseDetail, LessonSummary } from '../types/curriculum';
import { ErrorMessage, LoadingSpinner } from '../components/LoadingSpinner';

// ── Lesson type badge labels ──────────────────────────────────────────────────

const LESSON_TYPE_LABELS: Record<string, string> = {
  NOTES: 'Notes',
  VIDEO: 'Video',
  LAB: 'Lab',
  QUIZ: 'Quiz',
  CHEATSHEET: 'Cheat Sheet',
};

const LESSON_TYPE_COLORS: Record<string, { bg: string; color: string }> = {
  NOTES: { bg: '#eff6ff', color: '#1d4ed8' },
  VIDEO: { bg: '#fdf4ff', color: '#7e22ce' },
  LAB: { bg: '#f0fdf4', color: '#15803d' },
  QUIZ: { bg: '#fffbeb', color: '#b45309' },
  CHEATSHEET: { bg: '#fff1f2', color: '#be185d' },
};

// ── Component ─────────────────────────────────────────────────────────────────

export function CourseDetailPage() {
  const { slug, id } = useParams<{ slug: string; id: string }>();

  const [course, setCourse] = useState<CourseDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const courseId = id ? parseInt(id, 10) : NaN;

  useEffect(() => {
    if (isNaN(courseId)) {
      setError('Invalid course ID.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    curriculumApi
      .getCourse(courseId)
      .then((data) => {
        setCourse(data);
      })
      .catch((err) => {
        const status = (err as { response?: { status?: number } })?.response?.status;
        if (status === 404) {
          setError('Course not found.');
        } else if (status === 401 || status === 403) {
          setError('You do not have permission to view this course.');
        } else {
          setError('Failed to load course. Please try again.');
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, [courseId]);

  if (loading) {
    return (
      <main style={{ padding: '4rem 2rem' }}>
        <LoadingSpinner />
      </main>
    );
  }

  if (error || !course) {
    return (
      <main style={{ padding: '4rem 2rem', maxWidth: '800px', margin: '0 auto' }}>
        <ErrorMessage message={error ?? 'Course not found.'} />
        <p style={{ marginTop: '1rem' }}>
          <Link to={`/explore/${slug}`} style={{ color: '#2563eb' }}>
            ← Back to discipline
          </Link>
        </p>
      </main>
    );
  }

  const totalLessons = course.modules.reduce((sum, m) => sum + m.lessons.length, 0);

  return (
    <main
      style={{
        maxWidth: '860px',
        margin: '0 auto',
        padding: '2rem 1.5rem',
      }}
    >
      {/* Breadcrumb */}
      <nav
        style={{
          fontSize: '0.8125rem',
          color: '#6b7280',
          marginBottom: '1.5rem',
          display: 'flex',
          gap: '0.375rem',
          alignItems: 'center',
        }}
        aria-label="Breadcrumb"
      >
        <Link to="/explore" style={{ color: '#2563eb', textDecoration: 'none' }}>
          Explore
        </Link>
        <span>›</span>
        <Link to={`/explore/${slug}`} style={{ color: '#2563eb', textDecoration: 'none' }}>
          {slug}
        </Link>
        <span>›</span>
        <span style={{ color: '#374151' }}>{course.title}</span>
      </nav>

      {/* Course header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1
          style={{
            fontSize: '1.75rem',
            fontWeight: 700,
            color: '#111827',
            marginBottom: '0.5rem',
            letterSpacing: '-0.02em',
            lineHeight: 1.2,
          }}
        >
          {course.title}
        </h1>

        {course.description && (
          <p
            style={{
              fontSize: '1rem',
              color: '#4b5563',
              lineHeight: 1.6,
              marginBottom: '0.75rem',
            }}
          >
            {course.description}
          </p>
        )}

        <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
          {course.modules.length} module{course.modules.length !== 1 ? 's' : ''} ·{' '}
          {totalLessons} lesson{totalLessons !== 1 ? 's' : ''}
        </p>
      </div>

      {/* Module list */}
      {course.modules.length === 0 ? (
        <div
          style={{
            padding: '3rem 2rem',
            textAlign: 'center',
            color: '#6b7280',
            border: '1px dashed #e5e7eb',
            borderRadius: '0.75rem',
          }}
        >
          <p>No modules available yet.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {course.modules
            .slice()
            .sort((a, b) => a.sequenceOrder - b.sequenceOrder)
            .map((module, idx) => (
              <section
                key={module.id}
                style={{
                  border: '1px solid #e5e7eb',
                  borderRadius: '0.75rem',
                  overflow: 'hidden',
                  background: '#fff',
                }}
              >
                {/* Module header */}
                <div
                  style={{
                    padding: '1rem 1.25rem',
                    background: '#f8fafc',
                    borderBottom: module.lessons.length > 0 ? '1px solid #e5e7eb' : undefined,
                    display: 'flex',
                    alignItems: 'baseline',
                    gap: '0.75rem',
                  }}
                >
                  <span
                    style={{
                      fontSize: '0.75rem',
                      fontWeight: 600,
                      color: '#9ca3af',
                      textTransform: 'uppercase',
                      letterSpacing: '0.08em',
                      flexShrink: 0,
                    }}
                  >
                    Module {idx + 1}
                  </span>
                  <h2
                    style={{
                      fontSize: '1rem',
                      fontWeight: 600,
                      color: '#111827',
                      margin: 0,
                    }}
                  >
                    {module.title}
                  </h2>
                  <span
                    style={{
                      marginLeft: 'auto',
                      fontSize: '0.8125rem',
                      color: '#6b7280',
                      flexShrink: 0,
                    }}
                  >
                    {module.lessons.length} lesson{module.lessons.length !== 1 ? 's' : ''}
                  </span>
                </div>

                {/* Lesson list */}
                {module.lessons.length === 0 ? (
                  <p
                    style={{
                      padding: '1rem 1.25rem',
                      fontSize: '0.875rem',
                      color: '#9ca3af',
                      fontStyle: 'italic',
                      margin: 0,
                    }}
                  >
                    No lessons published yet.
                  </p>
                ) : (
                  <ul style={{ listStyle: 'none', margin: 0, padding: 0 }}>
                    {module.lessons
                      .slice()
                      .sort((a, b) => a.sequenceOrder - b.sequenceOrder)
                      .map((lesson, lIdx) => (
                        <LessonRow
                          key={lesson.id}
                          lesson={lesson}
                          isLast={lIdx === module.lessons.length - 1}
                        />
                      ))}
                  </ul>
                )}
              </section>
            ))}
        </div>
      )}
    </main>
  );
}

// ── LessonRow ─────────────────────────────────────────────────────────────────

function LessonRow({
  lesson,
  isLast,
}: {
  lesson: LessonSummary;
  isLast: boolean;
}) {
  const badge = LESSON_TYPE_COLORS[lesson.lessonType] ?? { bg: '#f3f4f6', color: '#374151' };
  const label = LESSON_TYPE_LABELS[lesson.lessonType] ?? lesson.lessonType;

  return (
    <li
      style={{
        borderBottom: isLast ? undefined : '1px solid #f3f4f6',
      }}
    >
      <Link
        to={`/lessons/${lesson.id}`}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.75rem',
          padding: '0.875rem 1.25rem',
          textDecoration: 'none',
          color: 'inherit',
          transition: 'background 150ms',
        }}
        onMouseEnter={(e) =>
          ((e.currentTarget as HTMLAnchorElement).style.background = '#f9fafb')
        }
        onMouseLeave={(e) =>
          ((e.currentTarget as HTMLAnchorElement).style.background = 'transparent')
        }
      >
        {/* Lesson type badge */}
        <span
          style={{
            fontSize: '0.6875rem',
            fontWeight: 600,
            padding: '0.125rem 0.5rem',
            borderRadius: '9999px',
            background: badge.bg,
            color: badge.color,
            flexShrink: 0,
            textTransform: 'uppercase',
            letterSpacing: '0.05em',
          }}
        >
          {label}
        </span>

        {/* Title */}
        <span
          style={{
            fontSize: '0.9375rem',
            color: '#111827',
            flexGrow: 1,
          }}
        >
          {lesson.title}
        </span>

        {/* Arrow */}
        <span style={{ color: '#9ca3af', fontSize: '0.875rem', flexShrink: 0 }}>
          →
        </span>
      </Link>
    </li>
  );
}
