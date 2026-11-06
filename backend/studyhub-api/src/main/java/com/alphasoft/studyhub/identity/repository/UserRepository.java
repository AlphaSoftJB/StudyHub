package com.alphasoft.studyhub.identity.repository;

import com.alphasoft.studyhub.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Counts active learners for pilot capacity enforcement (DR-025).
     * Uses a JOIN against the roles table to count ROLE_LEARNER users specifically
     * rather than all users (admins do not count toward the learner cap).
     */
    @Query("""
           SELECT COUNT(u) FROM User u
           JOIN u.roles r
           WHERE r.name = 'ROLE_LEARNER'
             AND u.active = true
           """)
    long countActiveLearners();
}
