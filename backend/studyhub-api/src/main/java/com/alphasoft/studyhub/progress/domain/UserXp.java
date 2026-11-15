package com.alphasoft.studyhub.progress.domain;

import com.alphasoft.studyhub.identity.domain.User;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Cumulative XP ledger for one learner.
 *
 * <p>One row per user; created lazily on first lesson completion.
 * XP is a display/gamification value only — it does not determine mastery (DR-022).
 */
@Entity
@Table(name = "user_xp")
public class UserXp {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_xp", nullable = false)
    private int totalXp = 0;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    // ── factory ──────────────────────────────────────────────────────────────

    public static UserXp createFor(User user) {
        var xp = new UserXp();
        xp.user = user;
        xp.userId = user.getId();
        return xp;
    }

    // ── domain operations ────────────────────────────────────────────────────

    public void addXp(int amount) {
        if (amount > 0) {
            totalXp += amount;
            updatedAt = Instant.now();
        }
    }

    // ── accessors ────────────────────────────────────────────────────────────

    public Long getUserId() { return userId; }
    public User getUser() { return user; }
    public int getTotalXp() { return totalXp; }
    public Instant getUpdatedAt() { return updatedAt; }
}
