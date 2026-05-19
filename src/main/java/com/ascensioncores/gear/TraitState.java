package com.ascensioncores.gear;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TraitState {

    // Chain Damage: last target + consecutive hit count, per player
    private static final Map<UUID, UUID> chainTarget = new HashMap<>();
    private static final Map<UUID, Integer> chainHits  = new HashMap<>();

    // Heal Suppress: entity UUID → expiry timestamp (ms)
    private static final Map<UUID, Long> healSuppress = new HashMap<>();

    // Second Wind cooldown: entity UUID → next allowed trigger (ms)
    private static final Map<UUID, Long> secondWindCooldown = new HashMap<>();

    private TraitState() {}

    // ── Chain Damage ──────────────────────────────────────────────────────────

    /** Returns consecutive hit count on {@code targetUUID} before this hit (0 on first hit). */
    public static int getChainHits(UUID playerUUID, UUID targetUUID) {
        if (targetUUID.equals(chainTarget.get(playerUUID))) {
            return chainHits.getOrDefault(playerUUID, 0);
        }
        return 0;
    }

    public static void recordHit(UUID playerUUID, UUID targetUUID) {
        if (targetUUID.equals(chainTarget.get(playerUUID))) {
            chainHits.merge(playerUUID, 1, Integer::sum);
        } else {
            chainTarget.put(playerUUID, targetUUID);
            chainHits.put(playerUUID, 1);
        }
    }

    // ── Heal Suppress ─────────────────────────────────────────────────────────

    public static boolean hasHealSuppress(UUID entityUUID) {
        Long expiry = healSuppress.get(entityUUID);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public static void applyHealSuppress(UUID entityUUID, long durationMs) {
        healSuppress.put(entityUUID, System.currentTimeMillis() + durationMs);
    }

    // ── Second Wind ───────────────────────────────────────────────────────────

    /** Returns true and sets cooldown if Second Wind is off cooldown. */
    public static boolean trySecondWind(UUID entityUUID, long cooldownMs) {
        Long next = secondWindCooldown.get(entityUUID);
        long now = System.currentTimeMillis();
        if (next != null && now < next) return false;
        secondWindCooldown.put(entityUUID, now + cooldownMs);
        return true;
    }
}
