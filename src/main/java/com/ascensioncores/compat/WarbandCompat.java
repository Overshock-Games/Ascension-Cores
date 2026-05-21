package com.ascensioncores.compat;

import com.ascensioncores.AscensionCoresConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Method;

/** Soft integration with Warband's per-mob difficulty/role data. */
public final class WarbandCompat {

    private static final String MOD_ID = "warband";

    private static boolean lookedUp;
    private static Method isStamped;
    private static Method getMobData;
    private static Method difficulty;
    private static Method inSquad;
    private static Method role;
    private static Method roleIsLeader;
    private static Method isFarmSuppressed;

    private WarbandCompat() {
    }

    public static Contribution contribution(LivingEntity entity) {
        if (!AscensionCoresConfig.enableWarbandIntegration || !(entity instanceof Mob mob) || !available()) {
            return Contribution.NONE;
        }

        try {
            if (!Boolean.TRUE.equals(isStamped.invoke(null, mob))) return Contribution.NONE;
            if (Boolean.TRUE.equals(isFarmSuppressed.invoke(null, mob))) return Contribution.NONE;

            Object data = getMobData.invoke(null, mob);
            double mobDifficulty = clamp01(((Number) difficulty.invoke(data)).doubleValue());
            if (mobDifficulty < AscensionCoresConfig.warbandMinimumDifficulty) return Contribution.NONE;

            boolean squadded = Boolean.TRUE.equals(inSquad.invoke(data));
            Object roleValue = role.invoke(data);
            boolean leader = Boolean.TRUE.equals(roleIsLeader.invoke(roleValue));
            return new Contribution(true, mobDifficulty, squadded, leader);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return Contribution.NONE;
        }
    }

    private static boolean available() {
        if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) return false;
        lookup();
        return isStamped != null
                && getMobData != null
                && difficulty != null
                && inSquad != null
                && role != null
                && roleIsLeader != null
                && isFarmSuppressed != null;
    }

    private static void lookup() {
        if (lookedUp) return;
        lookedUp = true;

        try {
            Class<?> mobData = Class.forName("com.warband.entity.MobData");
            Class<?> roleClass = Class.forName("com.warband.entity.Role");
            Class<?> antiFarmDirector = Class.forName("com.warband.spawn.AntiFarmDirector");

            isStamped = mobData.getMethod("isStamped", net.minecraft.world.entity.Entity.class);
            getMobData = mobData.getMethod("get", net.minecraft.world.entity.Entity.class);
            difficulty = mobData.getMethod("difficulty");
            inSquad = mobData.getMethod("inSquad");
            role = mobData.getMethod("role");
            roleIsLeader = roleClass.getMethod("isLeader");
            isFarmSuppressed = antiFarmDirector.getMethod("isFarmSuppressed", Mob.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            isStamped = null;
            getMobData = null;
            difficulty = null;
            inSquad = null;
            role = null;
            roleIsLeader = null;
            isFarmSuppressed = null;
        }
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public record Contribution(boolean active, double difficulty, boolean squadded, boolean leader) {
        public static final Contribution NONE = new Contribution(false, 0.0, false, false);
    }
}
