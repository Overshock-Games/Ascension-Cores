package com.ascensioncores;

import com.ascensioncores.gear.TraitState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TraitStateTest {

    private UUID player;
    private UUID targetA;
    private UUID targetB;

    @BeforeEach
    void setUp() {
        player  = UUID.randomUUID();
        targetA = UUID.randomUUID();
        targetB = UUID.randomUUID();
    }

    // ── Chain Damage ──────────────────────────────────────────────────────────

    @Test
    void chain_firstHit_returnsZero() {
        assertEquals(0, TraitState.getChainHits(player, targetA));
    }

    @Test
    void chain_secondHitSameTarget_returnsOne() {
        TraitState.recordHit(player, targetA);
        assertEquals(1, TraitState.getChainHits(player, targetA));
    }

    @Test
    void chain_multipleHitsSameTarget_accumulates() {
        TraitState.recordHit(player, targetA);
        TraitState.recordHit(player, targetA);
        TraitState.recordHit(player, targetA);
        assertEquals(3, TraitState.getChainHits(player, targetA));
    }

    @Test
    void chain_switchTarget_resetsCount() {
        TraitState.recordHit(player, targetA);
        TraitState.recordHit(player, targetA);
        TraitState.recordHit(player, targetB); // switch
        assertEquals(0, TraitState.getChainHits(player, targetA));
        assertEquals(1, TraitState.getChainHits(player, targetB));
    }

    @Test
    void chain_independentPerPlayer() {
        UUID player2 = UUID.randomUUID();
        TraitState.recordHit(player, targetA);
        TraitState.recordHit(player, targetA);
        assertEquals(2, TraitState.getChainHits(player, targetA));
        assertEquals(0, TraitState.getChainHits(player2, targetA));
    }

    // ── Heal Suppress ─────────────────────────────────────────────────────────

    @Test
    void healSuppress_none_returnsFalse() {
        assertFalse(TraitState.hasHealSuppress(targetA));
    }

    @Test
    void healSuppress_fresh_returnsTrue() {
        TraitState.applyHealSuppress(targetA, 5000L);
        assertTrue(TraitState.hasHealSuppress(targetA));
    }

    @Test
    void healSuppress_expired_returnsFalse() throws InterruptedException {
        TraitState.applyHealSuppress(targetA, 50L);
        Thread.sleep(60);
        assertFalse(TraitState.hasHealSuppress(targetA));
    }

    @Test
    void healSuppress_renewsOnReapply() throws InterruptedException {
        TraitState.applyHealSuppress(targetA, 80L);
        Thread.sleep(40);
        TraitState.applyHealSuppress(targetA, 5000L); // renew
        Thread.sleep(60);
        assertTrue(TraitState.hasHealSuppress(targetA)); // still active
    }

    @Test
    void healSuppress_independentPerEntity() {
        TraitState.applyHealSuppress(targetA, 5000L);
        assertFalse(TraitState.hasHealSuppress(targetB));
    }

    // ── Second Wind ───────────────────────────────────────────────────────────

    @Test
    void secondWind_firstTrigger_succeeds() {
        assertTrue(TraitState.trySecondWind(player, 30_000L));
    }

    @Test
    void secondWind_immediatlyAfter_blocked() {
        TraitState.trySecondWind(player, 30_000L);
        assertFalse(TraitState.trySecondWind(player, 30_000L));
    }

    @Test
    void secondWind_afterCooldownExpires_succeeds() throws InterruptedException {
        TraitState.trySecondWind(player, 50L);
        Thread.sleep(60);
        assertTrue(TraitState.trySecondWind(player, 50L));
    }

    @Test
    void secondWind_independentPerEntity() {
        UUID player2 = UUID.randomUUID();
        TraitState.trySecondWind(player, 30_000L);
        assertTrue(TraitState.trySecondWind(player2, 30_000L));
    }
}
