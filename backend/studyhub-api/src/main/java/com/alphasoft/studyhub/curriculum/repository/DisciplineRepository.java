package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {

    /** Active disciplines ordered by sequence — used by the Explore page. */
    List<Discipline> findByActiveTrueOrderBySequenceOrderAsc();

    Optional<Discipline> findBySlug(String slug);

    /** Admin: all disciplines regardless of active state. */
    List<Discipline> findAllByOrderBySequenceOrderAsc();

    @Query("SELECT d FROM Discipline d WHERE d.active = true AND d.slug = :slug")
    Optional<Discipline> findActiveBySlug(String slug);
}
