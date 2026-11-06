package com.alphasoft.studyhub.curriculum.repository;

import com.alphasoft.studyhub.curriculum.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseIdOrderBySequenceOrderAsc(Long courseId);
}
