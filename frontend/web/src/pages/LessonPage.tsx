import { useParams } from 'react-router-dom';
import { ErrorMessage, LoadingSpinner } from '../components/LoadingSpinner';
import { MarkdownRenderer } from '../components/MarkdownRenderer';
import { useLesson } from '../hooks/useCurriculum';

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
    </main>
  );
}
