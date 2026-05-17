package com.ascensioncores;

import com.ascensioncores.event.TooltipHandler;
import net.fabricmc.api.ClientModInitializer;

public final class AscensionClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TooltipHandler.register();
    }
}
