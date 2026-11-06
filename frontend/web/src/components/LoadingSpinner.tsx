export function LoadingSpinner({ label = 'Loading…' }: { label?: string }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: '2rem' }}>
      <span>{label}</span>
    </div>
  );
}

export function ErrorMessage({ message }: { message: string }) {
  return (
    <div
      role="alert"
      style={{
        padding: '1rem',
        background: '#fee2e2',
        color: '#991b1b',
        borderRadius: '0.5rem',
        margin: '1rem 0',
      }}
    >
      {message}
    </div>
  );
}
