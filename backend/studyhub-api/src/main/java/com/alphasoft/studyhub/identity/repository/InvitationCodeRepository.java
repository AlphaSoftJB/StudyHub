package com.alphasoft.studyhub.identity.repository;

import com.alphasoft.studyhub.identity.domain.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {

    Optional<InvitationCode> findByCode(String code);

    /**
     * Returns the code only if it has not been redeemed, has not expired,
     * and has not been administratively revoked.
     * Used during registration to validate the submitted code in one query.
     *
     * V6: added "AND ic.revoked = false" to enforce admin revocation (DR-024).
     */
    @Query("""
           SELECT ic FROM InvitationCode ic
           WHERE ic.code = :code
             AND ic.usedByUser IS NULL
             AND ic.expiresAt > CURRENT_TIMESTAMP
             AND ic.revoked = false
           """)
    Optional<InvitationCode> findUsableByCode(String code);
}
