// Mirrors DTOs from com.alphasoft.studyhub.progress.dto

export type LessonProgressStatus = 'IN_PROGRESS' | 'COMPLETED';

// ── POST /api/v1/progress/lessons/{id}/complete ───────────────────────────────

/** Mirrors LessonCompleteResponse */
export interface LessonCompleteResponse {
  progressId: number;
  lessonId: number;
  status: LessonProgressStatus;
  completedAt: string; // ISO 8601
  xpAwarded: number;
  totalXp: number;
}

// ── GET /api/v1/progress/me ───────────────────────────────────────────────────

/** Mirrors ProgressSummaryResponse */
export interface ProgressSummary {
  progressId: number;
  lessonId: number;
  lessonTitle: string;
  status: LessonProgressStatus;
  completedAt: string | null;
  xpAwarded: number;
}

/** Spring Page<ProgressSummaryResponse> */
export interface ProgressPage {
  content: ProgressSummary[];
  totalElements: number;
  totalPages: number;
  number: number; // zero-based current page
  size: number;
  last: boolean;
}

// ── GET /api/v1/learner/dashboard ─────────────────────────────────────────────

/** Mirrors DashboardResponse.CourseSummary */
export interface CourseSummary {
  courseId: number;
  courseTitle: string;
  courseSlug: string;
  publishedLessons: number;
  completedLessons: number;
  completionPercent: number; // 0–100
}

/** Mirrors DashboardResponse */
export interface DashboardResponse {
  displayName: string;
  totalXp: number;
  completedLessons: number;
  courses: CourseSummary[];
}
