package com.alphasoft.studyhub.progress.repository;

import com.alphasoft.studyhub.progress.domain.UserXp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserXpRepository extends JpaRepository<UserXp, Long> {

    Optional<UserXp> findByUserId(Long userId);
}
