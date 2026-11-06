package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByCourseIdAndActiveTrueOrderByIdAsc(Long courseId);

    List<Exam> findByCourseId(Long courseId);
}
