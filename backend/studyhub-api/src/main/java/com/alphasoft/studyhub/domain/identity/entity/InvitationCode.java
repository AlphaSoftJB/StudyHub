package com.alphasoft.studyhub.domain.identity.entity;

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

    public boolean isUsable() {
        return !isRedeemed() && !isExpired();
    }
}
