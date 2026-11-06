// Mirrors DTOs from com.alphasoft.studyhub.curriculum.api.dto

export type LessonType = 'NOTES' | 'VIDEO' | 'LAB' | 'QUIZ' | 'CHEATSHEET';

export type ContentStatus =
  | 'DRAFT'
  | 'IN_REVIEW'
  | 'APPROVED'
  | 'PUBLISHED'
  | 'DEPRECATED';

export interface Discipline {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  icon: string | null;
  sequenceOrder: number;
}

export interface Course {
  id: number;
  disciplineId: number;
  title: string;
  slug: string;
  description: string | null;
  sequenceOrder: number;
}

export interface LessonSummary {
  id: number;
  moduleId: number;
  title: string;
  lessonType: LessonType;
  sequenceOrder: number;
}

export interface LessonDetail {
  id: number;
  moduleId: number;
  title: string;
  lessonType: LessonType;
  contentStatus: ContentStatus;
  contentBody: string | null;
  sequenceOrder: number;
  publishedAt: string | null; // ISO 8601
}
