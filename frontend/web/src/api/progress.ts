// Progress API client — mirrors com.alphasoft.studyhub.progress.api.*
// Security: all requests go through apiClient which keeps the access token
// in-memory only (never localStorage) and handles CSRF double-submit.

import { apiClient } from './client';
import type {
  LessonCompleteResponse,
  ProgressPage,
  DashboardResponse,
} from '../types/progress';

export const progressApi = {
  /**
   * POST /api/v1/progress/lessons/{lessonId}/complete
   * Marks the lesson complete for the authenticated learner.
   * Idempotent — repeated calls do not re-award XP.
   */
  completeLesson: (lessonId: number): Promise<LessonCompleteResponse> =>
    apiClient
      .post<LessonCompleteResponse>(
        `/api/v1/progress/lessons/${lessonId}/complete`,
        {}
      )
      .then((r) => r.data),

  /**
   * GET /api/v1/progress/me
   * Returns the authenticated learner's progress, newest first (paginated).
   */
  getMyProgress: (page = 0, size = 20): Promise<ProgressPage> =>
    apiClient
      .get<ProgressPage>(`/api/v1/progress/me`, { params: { page, size } })
      .then((r) => r.data),

  /**
   * GET /api/v1/learner/dashboard
   * Returns overall stats and per-course completion summary.
   */
  getDashboard: (): Promise<DashboardResponse> =>
    apiClient
      .get<DashboardResponse>('/api/v1/learner/dashboard')
      .then((r) => r.data),
};
