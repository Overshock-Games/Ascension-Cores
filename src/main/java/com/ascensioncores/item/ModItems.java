package com.ascensioncores.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("ascensioncores", path));
    }

    public static final Item UPGRADE_CORE = Registry.register(
        BuiltInRegistries.ITEM,
        key("upgrade_core"),
        new Item(new Item.Properties().stacksTo(64).setId(key("upgrade_core")))
    );

    public static final Item CHAOS_CORE = Registry.register(
        BuiltInRegistries.ITEM,
        key("chaos_core"),
        new Item(new Item.Properties().stacksTo(64).setId(key("chaos_core")))
    );

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(UPGRADE_CORE);
            entries.accept(CHAOS_CORE);
        });
    }
}
