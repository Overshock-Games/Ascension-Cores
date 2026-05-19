package com.ascensioncores.component;

import com.ascensioncores.gear.RolledStat;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class ModComponents {

    public static final DataComponentType<Integer> ASCENSION_LEVEL = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath("ascensioncores", "level"),
        DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .build()
    );

    public static final DataComponentType<List<RolledStat>> ROLLED_STATS = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath("ascensioncores", "rolled_stats"),
        DataComponentType.<List<RolledStat>>builder()
            .persistent(RolledStat.CODEC.listOf())
            .build()
    );

    public static final DataComponentType<Long> RANDOM_SEED = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath("ascensioncores", "random_seed"),
        DataComponentType.<Long>builder()
            .persistent(Codec.LONG)
            .build()
    );

    public static void register() {}
}
