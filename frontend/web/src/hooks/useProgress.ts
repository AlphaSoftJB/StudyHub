import { useCallback, useEffect, useState } from 'react';
import { progressApi } from '../api/progress';
import type {
  DashboardResponse,
  LessonCompleteResponse,
  ProgressPage,
} from '../types/progress';

type FetchState<T> = { data: T | null; loading: boolean; error: string | null };

function initialState<T>(): FetchState<T> {
  return { data: null, loading: true, error: null };
}

// ── Dashboard ─────────────────────────────────────────────────────────────────

export function useDashboard() {
  const [state, setState] = useState<FetchState<DashboardResponse>>(initialState);

  useEffect(() => {
    setState(initialState());
    progressApi
      .getDashboard()
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((e) =>
        setState({ data: null, loading: false, error: e.message ?? 'Failed to load dashboard' }),
      );
  }, []);

  return state;
}

// ── My Progress list ──────────────────────────────────────────────────────────

export function useMyProgress(page = 0) {
  const [state, setState] = useState<FetchState<ProgressPage>>(initialState);

  useEffect(() => {
    setState(initialState());
    progressApi
      .getMyProgress(page)
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((e) =>
        setState({ data: null, loading: false, error: e.message ?? 'Failed to load progress' }),
      );
  }, [page]);

  return state;
}

// ── Complete-lesson action ────────────────────────────────────────────────────

type CompletionState = {
  completing: boolean;
  result: LessonCompleteResponse | null;
  error: string | null;
};

/**
 * Returns a stable `completeLesson` callback and the current completion state.
 * DR-022: the response does NOT carry a mastery field — do not infer mastery
 * from the returned data.
 */
export function useLessonCompletion(lessonId: number | undefined) {
  const [state, setState] = useState<CompletionState>({
    completing: false,
    result: null,
    error: null,
  });

  const completeLesson = useCallback(() => {
    if (!lessonId) return;
    setState({ completing: true, result: null, error: null });
    progressApi
      .completeLesson(lessonId)
      .then((result) => setState({ completing: false, result, error: null }))
      .catch((e) =>
        setState({ completing: false, result: null, error: e.message ?? 'Failed to mark complete' }),
      );
  }, [lessonId]);

  return { ...state, completeLesson };
}
