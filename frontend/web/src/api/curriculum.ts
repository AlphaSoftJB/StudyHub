import { apiClient } from './client';
import type { Course, CourseDetail, Discipline, LessonDetail } from '../types/curriculum';

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

  /**
   * GET /api/v1/courses/{id}
   * Returns course detail with modules and published lessons (Sprint 4 — FV-002).
   */
  getCourse: (courseId: number): Promise<CourseDetail> =>
    apiClient
      .get<CourseDetail>(`/api/v1/courses/${courseId}`)
      .then((r) => r.data),
};
