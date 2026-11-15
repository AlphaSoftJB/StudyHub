/**
 * CourseDetailPage tests (Sprint 4 final verification — FV-002).
 *
 * Tests:
 *  - Renders course title, module list, lesson badges (happy path)
 *  - Shows loading spinner while fetch is in-flight
 *  - Shows not-found message when API returns 404
 *  - Shows generic error message on API failure
 *  - Only published lessons are rendered (server enforces DR-012; component
 *    trusts response — test verifies lessons in the response are rendered
 *    and no extra server-side lessons are added by the component)
 *  - Navigates to /lessons/:id when a lesson row is clicked
 */

import React from 'react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { CourseDetailPage } from '../pages/CourseDetailPage';
import * as curriculumModule from '../api/curriculum';
import type { CourseDetail } from '../types/curriculum';

// ── Mocks ─────────────────────────────────────────────────────────────────────

vi.mock('../api/curriculum', () => ({
  curriculumApi: {
    getDisciplines: vi.fn(),
    getDiscipline: vi.fn(),
    getCourses: vi.fn(),
    getLesson: vi.fn(),
    getCourse: vi.fn(),
  },
}));

// client.ts uses interceptors; mock minimally to prevent import errors
vi.mock('../api/client', () => ({
  setAccessToken: vi.fn(),
  clearAccessToken: vi.fn(),
  registerRefreshCallback: vi.fn(),
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  },
}));

// ── Helpers ───────────────────────────────────────────────────────────────────

const mockedCurriculumApi = vi.mocked(curriculumModule.curriculumApi);

function makeCourse(overrides?: Partial<CourseDetail>): CourseDetail {
  return {
    id: 42,
    disciplineId: 1,
    title: 'Introduction to Cryptography',
    slug: 'intro-cryptography',
    description: 'Learn the foundations of modern cryptography.',
    sequenceOrder: 1,
    modules: [
      {
        id: 10,
        courseId: 42,
        title: 'Symmetric Encryption',
        sequenceOrder: 1,
        lessons: [
          {
            id: 100,
            moduleId: 10,
            title: 'AES Block Cipher',
            lessonType: 'NOTES',
            sequenceOrder: 1,
          },
          {
            id: 101,
            moduleId: 10,
            title: 'AES Lab Exercise',
            lessonType: 'LAB',
            sequenceOrder: 2,
          },
        ],
      },
      {
        id: 11,
        courseId: 42,
        title: 'Asymmetric Encryption',
        sequenceOrder: 2,
        lessons: [
          {
            id: 200,
            moduleId: 11,
            title: 'RSA Key Pairs',
            lessonType: 'VIDEO',
            sequenceOrder: 1,
          },
        ],
      },
    ],
    ...overrides,
  };
}

/** Renders CourseDetailPage at /explore/cybersecurity/courses/42 */
function renderPage(courseId = '42') {
  return render(
    <MemoryRouter initialEntries={[`/explore/cybersecurity/courses/${courseId}`]}>
      <Routes>
        <Route
          path="/explore/:slug/courses/:id"
          element={<CourseDetailPage />}
        />
        <Route path="/lessons/:id" element={<div data-testid="lesson-page">Lesson Page</div>} />
        <Route path="/explore/cybersecurity" element={<div data-testid="discipline-page">Discipline</div>} />
      </Routes>
    </MemoryRouter>,
  );
}

// ── Tests ─────────────────────────────────────────────────────────────────────

describe('CourseDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ── 1. Happy path — course renders correctly ────────────────────────────────

  it('renders course title, description, modules and lesson titles', async () => {
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(makeCourse());
    renderPage();

    // Course title appears in the h1 (also appears in breadcrumb, so use heading role)
    await waitFor(() =>
      expect(
        screen.getByRole('heading', { name: 'Introduction to Cryptography', level: 1 }),
      ).toBeInTheDocument(),
    );

    // Description
    expect(
      screen.getByText('Learn the foundations of modern cryptography.'),
    ).toBeInTheDocument();

    // Both module headings
    expect(screen.getByText('Symmetric Encryption')).toBeInTheDocument();
    expect(screen.getByText('Asymmetric Encryption')).toBeInTheDocument();

    // Individual lessons
    expect(screen.getByText('AES Block Cipher')).toBeInTheDocument();
    expect(screen.getByText('AES Lab Exercise')).toBeInTheDocument();
    expect(screen.getByText('RSA Key Pairs')).toBeInTheDocument();

    // Lesson type badges
    expect(screen.getByText('Notes')).toBeInTheDocument();
    expect(screen.getByText('Lab')).toBeInTheDocument();
    expect(screen.getByText('Video')).toBeInTheDocument();
  });

  it('calls getCourse with the numeric id from the route parameter', async () => {
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(makeCourse());
    renderPage('42');

    await waitFor(() =>
      expect(mockedCurriculumApi.getCourse).toHaveBeenCalledWith(42),
    );
  });

  it('renders module count and lesson count in the course header', async () => {
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(makeCourse());
    renderPage();

    await waitFor(() =>
      expect(screen.getByText(/2 modules/)).toBeInTheDocument(),
    );
    expect(screen.getByText(/3 lessons/)).toBeInTheDocument();
  });

  // ── 2. Loading state ────────────────────────────────────────────────────────

  it('shows a loading spinner while the fetch is in-flight', () => {
    // Never resolves during this test
    mockedCurriculumApi.getCourse.mockReturnValueOnce(new Promise(() => {}));
    renderPage();

    // The spinner renders immediately (before any await)
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  // ── 3. Not-found (404) ──────────────────────────────────────────────────────

  it('shows a not-found message when the API returns 404', async () => {
    const notFound = { response: { status: 404 } };
    mockedCurriculumApi.getCourse.mockRejectedValueOnce(notFound);
    renderPage();

    await waitFor(() =>
      expect(screen.getByText('Course not found.')).toBeInTheDocument(),
    );
  });

  it('shows the back-link to the discipline page on 404', async () => {
    const notFound = { response: { status: 404 } };
    mockedCurriculumApi.getCourse.mockRejectedValueOnce(notFound);
    renderPage();

    await waitFor(() =>
      expect(screen.getByText(/Back to discipline/)).toBeInTheDocument(),
    );
  });

  // ── 4. Generic API failure ──────────────────────────────────────────────────

  it('shows a generic error message on network / 500 failure', async () => {
    const serverError = { response: { status: 500 } };
    mockedCurriculumApi.getCourse.mockRejectedValueOnce(serverError);
    renderPage();

    await waitFor(() =>
      expect(
        screen.getByText('Failed to load course. Please try again.'),
      ).toBeInTheDocument(),
    );
  });

  it('shows a permission error message on 401/403', async () => {
    const forbidden = { response: { status: 403 } };
    mockedCurriculumApi.getCourse.mockRejectedValueOnce(forbidden);
    renderPage();

    await waitFor(() =>
      expect(
        screen.getByText('You do not have permission to view this course.'),
      ).toBeInTheDocument(),
    );
  });

  // ── 5. Published-content restriction (DR-012) ───────────────────────────────

  it('renders only lessons present in the backend response (DR-012 — server enforces filter)', async () => {
    // Backend returns ONLY published lessons; component renders exactly what it receives.
    // This test verifies the component does NOT add or remove lessons from the response.
    const course = makeCourse({
      modules: [
        {
          id: 10,
          courseId: 42,
          title: 'Module A',
          sequenceOrder: 1,
          lessons: [
            { id: 1, moduleId: 10, title: 'Published Lesson', lessonType: 'NOTES', sequenceOrder: 1 },
          ],
        },
      ],
    });
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(course);
    renderPage();

    await waitFor(() =>
      expect(screen.getByText('Published Lesson')).toBeInTheDocument(),
    );
    // Only one lesson row rendered — no extra lessons added by the component
    expect(screen.getAllByRole('listitem')).toHaveLength(1);
  });

  it('shows "No lessons published yet" when a module has an empty lessons array', async () => {
    const course = makeCourse({
      modules: [
        {
          id: 10,
          courseId: 42,
          title: 'Empty Module',
          sequenceOrder: 1,
          lessons: [],
        },
      ],
    });
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(course);
    renderPage();

    await waitFor(() =>
      expect(screen.getByText('No lessons published yet.')).toBeInTheDocument(),
    );
  });

  it('shows "No modules available yet" when the course has no modules', async () => {
    const course = makeCourse({ modules: [] });
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(course);
    renderPage();

    await waitFor(() =>
      expect(screen.getByText('No modules available yet.')).toBeInTheDocument(),
    );
  });

  // ── 6. Lesson navigation ────────────────────────────────────────────────────

  it('navigates to /lessons/:id when a lesson row link is clicked', async () => {
    const user = userEvent.setup();
    mockedCurriculumApi.getCourse.mockResolvedValueOnce(makeCourse());
    renderPage();

    await waitFor(() =>
      expect(screen.getByText('AES Block Cipher')).toBeInTheDocument(),
    );

    // The lesson row is a link; click it
    await user.click(screen.getByText('AES Block Cipher'));

    // Should navigate to /lessons/100
    await waitFor(() =>
      expect(screen.getByTestId('lesson-page')).toBeInTheDocument(),
    );
  });
});
