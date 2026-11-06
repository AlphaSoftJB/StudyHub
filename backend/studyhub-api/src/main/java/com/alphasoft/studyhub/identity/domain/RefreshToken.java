package com.alphasoft.studyhub.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Server-side record of an issued refresh token.
 *
 * Security notes:
 *  - tokenHash stores hex-encoded SHA-256 of the actual token; raw value never persisted.
 *  - familyId groups tokens from the same login; reuse of an already-rotated token
 *    triggers revocation of the whole family (token rotation, Sprint 2).
 *  - revoked rows are retained for audit; a cleanup job (Phase 2) can purge expired rows.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Hex-encoded SHA-256(rawToken). 64 hex chars. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** UUID v4 assigned at login; shared by all tokens in one refresh chain. */
    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        if (issuedAt == null) issuedAt = Instant.now();
    }

    public RefreshToken(User user, String tokenHash, String familyId, Instant expiresAt) {
        this.user      = user;
        this.tokenHash = tokenHash;
        this.familyId  = familyId;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked   = true;
        this.revokedAt = Instant.now();
    }
}
