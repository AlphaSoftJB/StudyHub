package com.alphasoft.studyhub.curriculum.domain;

/**
 * Lesson content types.
 *
 * <p>Determines how the frontend renders the lesson and what fields are populated:
 * <ul>
 *   <li>NOTES      — markdown text in content_body</li>
 *   <li>VIDEO      — URL/embed ref in content_body</li>
 *   <li>LAB        — structured lab in companion {@code labs} row</li>
 *   <li>QUIZ       — embedded quiz, questions drawn from question bank</li>
 *   <li>CHEATSHEET — reference card markdown in content_body</li>
 * </ul>
 */
public enum LessonType {
    NOTES,
    VIDEO,
    LAB,
    QUIZ,
    CHEATSHEET
}
