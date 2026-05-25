package com.ascensioncores.event;

import com.ascensioncores.gear.GearHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Keeps existing ascended gear aligned with current trait attribute semantics. */
public final class GearMigrationHandler {

    private GearMigrationHandler() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            server.execute(() -> {
                for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                    ItemStack stack = player.getInventory().getItem(slot);
                    GearHelper.repairTraitGaps(stack);
                    GearHelper.rebuildAttributesIfOutdated(stack);
                }
            });
        });
    }
}
