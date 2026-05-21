package com.ascensioncores;

import com.ascensioncores.command.AscensionCoresCommand;
import com.ascensioncores.component.ModComponents;
import com.ascensioncores.event.EntityDeathHandler;
import com.ascensioncores.event.GearMigrationHandler;
import com.ascensioncores.event.LootHandler;
import com.ascensioncores.item.ModItems;
import com.ascensioncores.loot.ModLootConditions;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AscensionCommonMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("ascensioncores");

    @Override
    public void onInitialize() {
        AscensionCoresConfig.load(LOGGER);
        ModComponents.register();
        ModItems.register();
        ModLootConditions.register();
        GearMigrationHandler.register();
        EntityDeathHandler.register();
        LootHandler.register();
        AscensionCoresCommand.register();
    }
}
