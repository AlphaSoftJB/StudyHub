package com.alphasoft.studyhub.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Single-use invitation code (DR-024).
 * Created by an admin; redeemed once by a learner during registration.
 * After redemption: usedByUser and usedAt are populated; code remains
 * in the table for audit purposes.
 *
 * Admin revocation (V6): revoked=true marks a code as administratively
 * invalidated before redemption. The row is preserved for audit.
 * Both isRedeemed() and isRevoked() prevent isUsable() from returning true.
 */
@Entity
@Table(name = "invitation_codes")
@Getter
@Setter
@NoArgsConstructor
public class InvitationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID v4 string; the value the learner enters at registration. */
    @Column(nullable = false, unique = true, length = 36)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_user_id")
    private User usedByUser;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** Set to true by an admin to invalidate a code before redemption. Row is kept for audit. */
    @Column(nullable = false)
    private boolean revoked = false;

    /** Populated when revoked is set to true. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public InvitationCode(String code, User createdBy, Instant expiresAt) {
        this.code      = code;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
    }

    public boolean isRedeemed() {
        return usedByUser != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /** Administratively revoke this code. Preserves the row for audit. */
    public void revoke() {
        this.revoked   = true;
        this.revokedAt = Instant.now();
    }

    /**
     * A code is usable if it has not been redeemed, has not expired,
     * and has not been administratively revoked.
     */
    public boolean isUsable() {
        return !isRedeemed() && !isExpired() && !revoked;
    }
}
