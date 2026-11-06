package com.alphasoft.studyhub.domain.identity.repository;

import com.alphasoft.studyhub.domain.identity.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByFamilyId(String familyId);

    /**
     * Revoke all tokens in a refresh family. Used when reuse is detected
     * (token rotation security — Sprint 2) or on explicit logout.
     */
    @Modifying
    @Query("""
           UPDATE RefreshToken rt
           SET rt.revoked = true, rt.revokedAt = :now
           WHERE rt.familyId = :familyId AND rt.revoked = false
           """)
    int revokeAllByFamilyId(String familyId, Instant now);

    /**
     * Revoke all active tokens for a user. Used on logout-all or account suspension.
     */
    @Modifying
    @Query("""
           UPDATE RefreshToken rt
           SET rt.revoked = true, rt.revokedAt = :now
           WHERE rt.user.id = :userId AND rt.revoked = false
           """)
    int revokeAllByUserId(Long userId, Instant now);
}
