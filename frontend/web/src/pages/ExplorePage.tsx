import { useParams } from 'react-router-dom';
import { DisciplineCard } from '../components/DisciplineCard';
import { ErrorMessage, LoadingSpinner } from '../components/LoadingSpinner';
import { useCourses, useDisciplines } from '../hooks/useCurriculum';
import type { Course } from '../types/curriculum';

/** /explore — List all active disciplines */
export function ExploreListPage() {
  const { data: disciplines, loading, error } = useDisciplines();

  if (loading) return <LoadingSpinner label="Loading disciplines…" />;
  if (error) return <ErrorMessage message={error} />;
  if (!disciplines?.length) return <p style={{ padding: '2rem' }}>No disciplines available yet.</p>;

  return (
    <main style={{ padding: '2rem', maxWidth: '1100px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '0.5rem' }}>Explore</h1>
      <p style={{ color: '#6b7280', marginBottom: '2rem' }}>
        Choose a discipline to start learning.
      </p>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
          gap: '1rem',
        }}
      >
        {disciplines.map((d) => (
          <DisciplineCard key={d.id} discipline={d} />
        ))}
      </div>
    </main>
  );
}

/** /explore/:slug — Discipline detail + course listing */
export function ExploreDisciplinePage() {
  const { slug } = useParams<{ slug: string }>();
  const { data: courses, loading, error } = useCourses(slug);

  if (loading) return <LoadingSpinner label="Loading courses…" />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <main style={{ padding: '2rem', maxWidth: '900px', margin: '0 auto' }}>
      <nav style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1.5rem' }}>
        <a href="/explore" style={{ color: '#2563eb' }}>Explore</a> › {slug}
      </nav>

      <h1 style={{ marginBottom: '1.5rem' }}>Courses</h1>

      {!courses?.length ? (
        <p>No courses available in this discipline yet.</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {courses.map((course) => (
            <CourseRow key={course.id} course={course} disciplineSlug={slug!} />
          ))}
        </div>
      )}
    </main>
  );
}

function CourseRow({ course, disciplineSlug }: { course: Course; disciplineSlug: string }) {
  return (
    <div
      style={{
        padding: '1.25rem 1.5rem',
        border: '1px solid #e5e7eb',
        borderRadius: '0.75rem',
        background: '#fff',
      }}
    >
      <h2 style={{ margin: '0 0 0.375rem', fontSize: '1.0625rem' }}>{course.title}</h2>
      {course.description && (
        <p style={{ margin: '0 0 0.75rem', fontSize: '0.875rem', color: '#6b7280' }}>
          {course.description}
        </p>
      )}
      <a
        href={`/explore/${disciplineSlug}/courses/${course.id}`}
        style={{ fontSize: '0.875rem', color: '#2563eb' }}
      >
        View course →
      </a>
    </div>
  );
}
