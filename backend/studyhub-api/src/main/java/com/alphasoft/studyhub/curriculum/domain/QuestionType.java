package com.alphasoft.studyhub.curriculum.domain;

/**
 * Question types in the unified question bank.
 *
 * <ul>
 *   <li>MCQ   — single correct answer; correct_answer holds 0-based index</li>
 *   <li>MULTI — multiple correct answers; correct_answers_json holds index array</li>
 *   <li>PBQ   — performance-based question; tasks_json holds sub-tasks</li>
 * </ul>
 */
public enum QuestionType {
    MCQ,
    MULTI,
    PBQ
}
