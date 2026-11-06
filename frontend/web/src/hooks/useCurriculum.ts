import { useEffect, useState } from 'react';
import { curriculumApi } from '../api/curriculum';
import type { Course, Discipline, LessonDetail } from '../types/curriculum';

type FetchState<T> = { data: T | null; loading: boolean; error: string | null };

function initialState<T>(): FetchState<T> {
  return { data: null, loading: true, error: null };
}

export function useDisciplines() {
  const [state, setState] = useState<FetchState<Discipline[]>>(initialState);

  useEffect(() => {
    setState(initialState());
    curriculumApi
      .getDisciplines()
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((e) =>
        setState({ data: null, loading: false, error: e.message ?? 'Failed to load disciplines' }),
      );
  }, []);

  return state;
}

export function useCourses(disciplineSlug: string | undefined) {
  const [state, setState] = useState<FetchState<Course[]>>(initialState);

  useEffect(() => {
    if (!disciplineSlug) return;
    setState(initialState());
    curriculumApi
      .getCourses(disciplineSlug)
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((e) =>
        setState({ data: null, loading: false, error: e.message ?? 'Failed to load courses' }),
      );
  }, [disciplineSlug]);

  return state;
}

export function useLesson(lessonId: number | undefined) {
  const [state, setState] = useState<FetchState<LessonDetail>>(initialState);

  useEffect(() => {
    if (!lessonId) return;
    setState(initialState());
    curriculumApi
      .getLesson(lessonId)
      .then((data) => setState({ data, loading: false, error: null }))
      .catch((e) =>
        setState({ data: null, loading: false, error: e.message ?? 'Lesson not found' }),
      );
  }, [lessonId]);

  return state;
}
