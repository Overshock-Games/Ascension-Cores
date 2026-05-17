package com.ascensioncores;

import com.ascensioncores.component.ModComponents;
import com.ascensioncores.item.ModItems;
import net.fabricmc.api.ModInitializer;

public final class AscensionCommonMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ModComponents.register();
        ModItems.register();
    }
}
