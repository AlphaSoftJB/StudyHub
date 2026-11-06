import { apiClient } from './client';
import type { Course, Discipline, LessonDetail } from '../types/curriculum';

export const curriculumApi = {
  getDisciplines: (): Promise<Discipline[]> =>
    apiClient.get<Discipline[]>('/api/v1/disciplines').then((r) => r.data),

  getDiscipline: (slug: string): Promise<Discipline> =>
    apiClient.get<Discipline>(`/api/v1/disciplines/${slug}`).then((r) => r.data),

  getCourses: (disciplineSlug: string): Promise<Course[]> =>
    apiClient
      .get<Course[]>(`/api/v1/disciplines/${disciplineSlug}/courses`)
      .then((r) => r.data),

  getLesson: (lessonId: number): Promise<LessonDetail> =>
    apiClient
      .get<LessonDetail>(`/api/v1/lessons/${lessonId}`)
      .then((r) => r.data),
};
