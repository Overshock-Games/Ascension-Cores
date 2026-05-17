package com.ascensioncores;

import com.ascensioncores.event.EntityDeathHandler;
import com.ascensioncores.event.LootHandler;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AscensionCoresMod implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("ascensioncores");

    @Override
    public void onInitializeServer() {
        AscensionCoresConfig.load(LOGGER);
        EntityDeathHandler.register();
        LootHandler.register();
        LOGGER.info("[AscensionCores] Initialised");
    }
}
