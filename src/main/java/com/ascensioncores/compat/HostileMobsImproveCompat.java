package com.ascensioncores.compat;

import com.ascensioncores.AscensionCoresConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

/**
 * Soft integration with the "Hostile Mobs Improve Over Time" datapack.
 *
 * <p>That datapack tracks a per-player difficulty score in a scoreboard objective
 * named {@code HostileMobs} and buffs mobs near a player by that player's score.
 * There is no mod/API to depend on — we just read the objective. If it is absent
 * (datapack not installed), every method here is a no-op returning 0.
 */
public final class HostileMobsImproveCompat {

    private static final String OBJECTIVE_NAME = "HostileMobs";

    private HostileMobsImproveCompat() {
    }

    /**
     * Difficulty level of the player credited with this kill. Returns 0 if the
     * integration is disabled, the datapack isn't present, or no player is responsible.
     */
    public static int getDifficultyLevel(ServerLevel level, DamageSource source) {
        if (!AscensionCoresConfig.enableHostileMobsImproveIntegration) return 0;
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
