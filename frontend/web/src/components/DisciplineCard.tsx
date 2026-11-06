import { Link } from 'react-router-dom';
import type { Discipline } from '../types/curriculum';

interface Props {
  discipline: Discipline;
}

const ICON_FALLBACKS: Record<string, string> = {
  cyber: '🔒',
  security: '🔒',
  network: '🌐',
  python: '🐍',
  ml: '🤖',
  ai: '🤖',
  linux: '🐧',
};

function getEmoji(icon: string | null, slug: string): string {
  if (!icon) {
    for (const [key, emoji] of Object.entries(ICON_FALLBACKS)) {
      if (slug.toLowerCase().includes(key)) return emoji;
    }
    return '📚';
  }
  return icon;
}

export function DisciplineCard({ discipline }: Props) {
  const emoji = getEmoji(discipline.icon, discipline.slug);

  return (
    <Link
      to={`/explore/${discipline.slug}`}
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '0.5rem',
        padding: '1.25rem',
        borderRadius: '0.75rem',
        border: '1px solid #e5e7eb',
        background: '#ffffff',
        textDecoration: 'none',
        color: 'inherit',
        boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
        transition: 'box-shadow 0.15s',
      }}
      onMouseEnter={(e) => ((e.currentTarget as HTMLElement).style.boxShadow = '0 4px 12px rgba(0,0,0,0.12)')}
      onMouseLeave={(e) => ((e.currentTarget as HTMLElement).style.boxShadow = '0 1px 3px rgba(0,0,0,0.08)')}
    >
      <span style={{ fontSize: '2rem' }}>{emoji}</span>
      <strong style={{ fontSize: '1.0625rem' }}>{discipline.name}</strong>
      {discipline.description && (
        <p style={{ margin: 0, fontSize: '0.875rem', color: '#6b7280', lineHeight: 1.5 }}>
          {discipline.description}
        </p>
      )}
    </Link>
  );
}
