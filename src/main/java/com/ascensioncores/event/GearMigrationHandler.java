package com.ascensioncores.event;

import com.ascensioncores.gear.GearHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Keeps existing ascended gear aligned with current trait attribute semantics. */
public final class GearMigrationHandler {

    private static int tickCounter;

    private GearMigrationHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter < 20 * 10) return;
            tickCounter = 0;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                    GearHelper.rebuildAttributesIfOutdated(player.getInventory().getItem(slot));
                }
            }
        });
    }
}
