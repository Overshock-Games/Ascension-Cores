package com.ascensioncores.event;

import com.ascensioncores.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class LootHandler {

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            if (key.equals(BuiltInLootTables.BASTION_TREASURE)
             || key.equals(BuiltInLootTables.END_CITY_TREASURE)) {
                tableBuilder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.UPGRADE_CORE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                    .when(LootItemRandomChanceCondition.randomChance(0.30f)));
            }

            if (key.equals(BuiltInLootTables.ANCIENT_CITY)) {
                // Upgrade cores — higher weight
                tableBuilder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.UPGRADE_CORE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                    .when(LootItemRandomChanceCondition.randomChance(0.40f)));

                // Chaos core — rare
                tableBuilder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.CHAOS_CORE))
                    .when(LootItemRandomChanceCondition.randomChance(0.05f)));
            }
        });
    }
}
