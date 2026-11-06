package com.alphasoft.studyhub.domain.identity.repository;

import com.alphasoft.studyhub.domain.identity.entity.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {

    Optional<InvitationCode> findByCode(String code);

    /**
     * Returns the code only if it has not been redeemed and has not expired.
     * Used during registration to validate the submitted code in one query.
     */
    @Query("""
           SELECT ic FROM InvitationCode ic
           WHERE ic.code = :code
             AND ic.usedByUser IS NULL
             AND ic.expiresAt > CURRENT_TIMESTAMP
           """)
    Optional<InvitationCode> findUsableByCode(String code);
}
