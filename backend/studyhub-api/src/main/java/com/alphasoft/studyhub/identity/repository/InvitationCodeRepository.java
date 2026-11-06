package com.alphasoft.studyhub.identity.repository;

import com.alphasoft.studyhub.identity.domain.InvitationCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    /**
     * Acquires a pessimistic write lock on the invitation code row.
     * Used during registration to prevent two concurrent requests from
     * redeeming the same code (DR-024 single-use guarantee).
     *
     * Must be called inside a transaction. The lock is held until the
     * transaction commits or rolls back.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ic FROM InvitationCode ic WHERE ic.code = :code")
    Optional<InvitationCode> findByCodeWithLock(String code);
}
