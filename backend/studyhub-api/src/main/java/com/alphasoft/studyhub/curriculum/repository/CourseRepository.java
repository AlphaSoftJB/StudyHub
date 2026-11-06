package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByDisciplineIdOrderBySequenceOrderAsc(Long disciplineId);

    Optional<Course> findByDisciplineIdAndSlug(Long disciplineId, String slug);

    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.discipline d
        WHERE d.slug = :disciplineSlug
        ORDER BY c.sequenceOrder ASC
        """)
    List<Course> findByDisciplineSlugOrdered(@Param("disciplineSlug") String disciplineSlug);
}
