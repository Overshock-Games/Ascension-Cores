package com.ascensioncores.compat;

import com.ascensioncores.AscensionCoresConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

/**
 * Soft integration with the "Hostile Mobs Improve Over Time" datapack
 * (KNIZE1007). No mod/API to depend on — everything here is read from vanilla
 * scoreboard + entity-tag state, so it's a no-op if the datapack isn't installed.
 *
 * <p>How the datapack works: each player has a per-player difficulty score in a
 * scoreboard objective named {@code HostileMobs}. Mobs within 32 blocks of a
 * player get buffed by that nearest player's score, applied once as attribute
 * modifiers; a buffed mob is tagged {@code knize.hmiot}. The datapack stores no
 * numeric level on the mob, so we gate on that tag and scale by the killer's
 * score — the killer is, in practice, the nearest player who buffed the mob.
 */
public final class HostileMobsImproveCompat {

    private static final String OBJECTIVE_NAME = "HostileMobs";
    private static final String IMPROVED_TAG = "knize.hmiot";

    private HostileMobsImproveCompat() {
    }

    /** True if this mob was actually buffed by the datapack. */
    public static boolean isImproved(LivingEntity entity) {
        return AscensionCoresConfig.enableHostileMobsImproveIntegration
            && entity.entityTags().contains(IMPROVED_TAG);
    }

    /**
     * Difficulty score the datapack used to buff this mob, read from the killer
     * player's {@code HostileMobs} score. Returns 0 if the mob wasn't
     * datapack-improved, the integration is off, the datapack isn't present, or
     * no player is credited with the kill.
     */
    public static int getDifficultyLevel(ServerLevel level, LivingEntity entity, DamageSource source) {
        if (!isImproved(entity)) return 0;
        if (source == null) return 0;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player player)) return 0;

        Scoreboard scoreboard = level.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) return 0;

        ReadOnlyScoreInfo info = scoreboard.getPlayerScoreInfo(player, objective);
        if (info == null) return 0;
        return Math.max(0, info.value());
    }
}
