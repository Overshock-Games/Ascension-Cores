package com.ascensioncores.event;

import com.ascensioncores.AscensionCoresConfig;
import com.ascensioncores.compat.BetterVanillaMobsCompat;
import com.ascensioncores.compat.HostileMobsImproveCompat;
import com.ascensioncores.compat.WarbandCompat;
import com.ascensioncores.item.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;

public final class EntityDeathHandler {

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        ServerLivingEntityEvents.AFTER_DEATH.register(EntityDeathHandler::onDeath);
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof Monster)) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        int armorPieces = countArmorPieces(entity);
        int weapons = (entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() ? 0 : 1) + (entity.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() ? 0 : 1);
        int gearPieces = armorPieces + weapons;
        boolean betterVanillaMob = BetterVanillaMobsCompat.isEnhanced(entity);
        int hmiotLevel = HostileMobsImproveCompat.getDifficultyLevel(serverLevel, entity, source);
        WarbandCompat.Contribution warband = WarbandCompat.contribution(entity);

        if (gearPieces == 0 && !betterVanillaMob && hmiotLevel == 0 && !warband.active()) return;

        RandomSource rng = entity.level().getRandom();

        double upgradeChance = 0.0;
        if (betterVanillaMob) {
            // BVM mobs use their own rate exclusively (regular vs. Alpha resolved inside)
            upgradeChance = BetterVanillaMobsCompat.getAscensionCoreDropChance(entity);
        } else if (gearPieces > 0) {
            upgradeChance = AscensionCoresConfig.mobAscensionCoreDropChancePerEquipment * gearPieces;
        }
        // Additive bonus scaling with the killer's HostileMobs difficulty score
        upgradeChance = Math.min(1.0, upgradeChance
            + hmiotLevel * AscensionCoresConfig.hostileMobsImproveAscensionCoreChancePerLevel);
        if (warband.active()) {
            double difficulty = warband.difficulty();
            upgradeChance = Math.min(1.0, upgradeChance
                + AscensionCoresConfig.warbandAscensionCoreBaseChance
                + difficulty * difficulty * AscensionCoresConfig.warbandAscensionCoreDifficultyChance
                + (warband.squadded() ? AscensionCoresConfig.warbandSquadRoleAscensionCoreBonus : 0.0));
        }
        if (rng.nextDouble() < upgradeChance) {
            int range = AscensionCoresConfig.mobAscensionCoreMaxDrop - AscensionCoresConfig.mobAscensionCoreMinDrop + 1;
            int count = AscensionCoresConfig.mobAscensionCoreMinDrop + rng.nextInt(range);
            entity.spawnAtLocation(serverLevel, new ItemStack(ModItems.ASCENSION_CORE, count));
        }

        double chaosChance = betterVanillaMob
            ? BetterVanillaMobsCompat.getChaosCoreDropChance(entity)
            : AscensionCoresConfig.mobChaosCoreDropChance;
        chaosChance = Math.min(1.0, chaosChance
            + hmiotLevel * AscensionCoresConfig.hostileMobsImproveChaosCoreChancePerLevel);
        if (warband.active() && warband.leader()) {
            double difficulty = warband.difficulty();
            chaosChance = Math.min(1.0, chaosChance
                + difficulty * difficulty * difficulty * AscensionCoresConfig.warbandChaosCoreDifficultyChance);
        }
        if (rng.nextDouble() < chaosChance) {
            entity.spawnAtLocation(serverLevel, new ItemStack(ModItems.CHAOS_CORE, 1));
        }
    }

    private static int countArmorPieces(LivingEntity entity) {
        int count = 0;
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty())  count++;
        if (!entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) count++;
        if (!entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty())  count++;
        if (!entity.getItemBySlot(EquipmentSlot.FEET).isEmpty())  count++;
        return count;
    }
}
