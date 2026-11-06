import ReactMarkdown from 'react-markdown';

interface Props {
  content: string;
  className?: string;
}

/**
 * Renders markdown lesson content. DR-012: only PUBLISHED content reaches here.
 */
export function MarkdownRenderer({ content, className }: Props) {
  return (
    <div className={className} style={{ lineHeight: 1.7, maxWidth: '72ch' }}>
      <ReactMarkdown>{content}</ReactMarkdown>
    </div>
  );
}
