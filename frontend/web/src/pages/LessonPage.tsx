import { useParams } from 'react-router-dom';
import { ErrorMessage, LoadingSpinner } from '../components/LoadingSpinner';
import { MarkdownRenderer } from '../components/MarkdownRenderer';
import { useLesson } from '../hooks/useCurriculum';
import { useLessonCompletion } from '../hooks/useProgress';

const LESSON_TYPE_LABEL: Record<string, string> = {
  NOTES: '📄 Notes',
  VIDEO: '🎥 Video',
  LAB: '🧪 Lab',
  QUIZ: '📝 Quiz',
  CHEATSHEET: '📋 Cheatsheet',
};

export function LessonPage() {
  const { id } = useParams<{ id: string }>();
  const lessonId = id ? parseInt(id, 10) : undefined;
  const { data: lesson, loading, error } = useLesson(lessonId);
  const {
    completing,
    result: completionResult,
    error: completionError,
    completeLesson,
  } = useLessonCompletion(lessonId);

  if (loading) return <LoadingSpinner label="Loading lesson…" />;
  if (error) return <ErrorMessage message={error} />;
  if (!lesson) return <ErrorMessage message="Lesson not found." />;

  const typeLabel = LESSON_TYPE_LABEL[lesson.lessonType] ?? lesson.lessonType;

  return (
    <main style={{ padding: '2rem', maxWidth: '860px', margin: '0 auto' }}>
      {/* Breadcrumb placeholder — real nav wired after modules endpoint exists */}
      <nav style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1.5rem' }}>
        <a href="/explore" style={{ color: '#2563eb' }}>Explore</a> › Lesson
      </nav>

      <header style={{ marginBottom: '2rem' }}>
        <span
          style={{
            display: 'inline-block',
            fontSize: '0.75rem',
            fontWeight: 600,
            letterSpacing: '0.05em',
            textTransform: 'uppercase',
            color: '#6b7280',
            marginBottom: '0.5rem',
          }}
        >
          {typeLabel}
        </span>
        <h1 style={{ margin: 0, fontSize: '1.75rem', lineHeight: 1.3 }}>{lesson.title}</h1>

        {lesson.publishedAt && (
          <p style={{ margin: '0.5rem 0 0', fontSize: '0.8125rem', color: '#9ca3af' }}>
            Published {new Date(lesson.publishedAt).toLocaleDateString()}
          </p>
        )}
      </header>

      {lesson.contentBody ? (
        <MarkdownRenderer content={lesson.contentBody} />
      ) : (
        <p style={{ color: '#6b7280' }}>No content available for this lesson yet.</p>
      )}

      {/* ── Mark Complete ───────────────────────────────────────────────────── */}
      <footer
        style={{
          marginTop: '3rem',
          paddingTop: '1.5rem',
          borderTop: '1px solid #e5e7eb',
          display: 'flex',
          alignItems: 'center',
          gap: '1rem',
          flexWrap: 'wrap',
        }}
      >
        {completionResult ? (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              color: '#16a34a',
              fontWeight: 600,
            }}
          >
            <span style={{ fontSize: '1.25rem' }}>✓</span>
            <span>
              Lesson complete!{' '}
              <span style={{ color: '#6b7280', fontWeight: 400 }}>
                +{completionResult.xpAwarded} XP · {completionResult.totalXp} total
              </span>
            </span>
          </div>
        ) : (
          <button
            onClick={completeLesson}
            disabled={completing}
            style={{
              padding: '0.625rem 1.25rem',
              borderRadius: '0.375rem',
              border: 'none',
              background: completing ? '#d1fae5' : '#16a34a',
              color: '#fff',
              fontWeight: 600,
              fontSize: '0.9375rem',
              cursor: completing ? 'not-allowed' : 'pointer',
              opacity: completing ? 0.7 : 1,
              transition: 'background 0.15s',
            }}
          >
            {completing ? 'Marking…' : '✓ Mark as Complete'}
          </button>
        )}

        {completionError && (
          <span style={{ fontSize: '0.875rem', color: '#dc2626' }}>
            {completionError}
          </span>
        )}
      </footer>
    </main>
  );
}
