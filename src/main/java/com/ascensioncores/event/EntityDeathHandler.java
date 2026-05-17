package com.ascensioncores.event;

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

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(EntityDeathHandler::onDeath);
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof Monster)) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        int armorPieces = countArmorPieces(entity);
        boolean hasWeapon = !entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();

        if (armorPieces == 0 && !hasWeapon) return;

        RandomSource rng = entity.level().getRandom();

        float upgradeChance = armorPieces == 4 ? 0.20f : 0.15f;
        if (rng.nextFloat() < upgradeChance) {
            int count = 1 + rng.nextInt(4);
            entity.spawnAtLocation(serverLevel, new ItemStack(ModItems.UPGRADE_CORE, count));
        }

        if (rng.nextFloat() < 0.01f) {
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
