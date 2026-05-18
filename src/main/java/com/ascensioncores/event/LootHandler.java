package com.ascensioncores.event;

import com.ascensioncores.AscensionCoresConfig;
import com.ascensioncores.item.ModItems;
import com.ascensioncores.loot.AscensionAutoLevelFunction;
import com.ascensioncores.loot.AscensionConfigChanceCondition;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public final class LootHandler {

    private static final Set<ResourceKey<LootTable>> NORMAL_CHEST_TABLES = Set.of(
        BuiltInLootTables.SPAWN_BONUS_CHEST,
        BuiltInLootTables.SIMPLE_DUNGEON,
        BuiltInLootTables.ABANDONED_MINESHAFT,
        BuiltInLootTables.STRONGHOLD_LIBRARY,
        BuiltInLootTables.STRONGHOLD_CROSSING,
        BuiltInLootTables.STRONGHOLD_CORRIDOR,
        BuiltInLootTables.DESERT_PYRAMID,
        BuiltInLootTables.JUNGLE_TEMPLE,
        BuiltInLootTables.IGLOO_CHEST,
        BuiltInLootTables.WOODLAND_MANSION,
        BuiltInLootTables.UNDERWATER_RUIN_SMALL,
        BuiltInLootTables.UNDERWATER_RUIN_BIG,
        BuiltInLootTables.BURIED_TREASURE,
        BuiltInLootTables.SHIPWRECK_SUPPLY,
        BuiltInLootTables.SHIPWRECK_TREASURE,
        BuiltInLootTables.PILLAGER_OUTPOST,
        BuiltInLootTables.RUINED_PORTAL,
        BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY,
        BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR,
        BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION,
        BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_RARE,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_UNIQUE,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE,
        BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE,
        BuiltInLootTables.VILLAGE_WEAPONSMITH,
        BuiltInLootTables.VILLAGE_TOOLSMITH,
        BuiltInLootTables.VILLAGE_ARMORER,
        BuiltInLootTables.VILLAGE_FLETCHER,
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
        BuiltInLootTables.VILLAGE_DESERT_HOUSE,
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE
    );

    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {

            if (NORMAL_CHEST_TABLES.contains(key)) {
                addAscensionCorePool(tableBuilder, "chestAscensionCoreChance",
                    AscensionCoresConfig.chestAscensionCoreMinDrop,
                    AscensionCoresConfig.chestAscensionCoreMaxDrop);
            }

            if (key.equals(BuiltInLootTables.BASTION_TREASURE)
             || key.equals(BuiltInLootTables.END_CITY_TREASURE)) {
                tableBuilder.apply(AscensionAutoLevelFunction.treasureLoot());
                addAscensionCorePool(tableBuilder, "treasureAscensionCoreChance",
                    AscensionCoresConfig.mobAscensionCoreMinDrop,
                    AscensionCoresConfig.mobAscensionCoreMaxDrop);
                addChaosCorePool(tableBuilder, "treasureChaosCoreChance");
            }

            if (key.equals(BuiltInLootTables.ANCIENT_CITY)) {
                tableBuilder.apply(AscensionAutoLevelFunction.treasureLoot());
                addAscensionCorePool(tableBuilder, "ancientCityAscensionCoreChance",
                    AscensionCoresConfig.mobAscensionCoreMinDrop,
                    AscensionCoresConfig.mobAscensionCoreMaxDrop);
                addChaosCorePool(tableBuilder, "ancientCityChaosCoreChance");
            }
        });
    }

    private static void addAscensionCorePool(LootTable.Builder tableBuilder, String chanceKey, int minDrop, int maxDrop) {
        tableBuilder.withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(ModItems.ASCENSION_CORE)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop, maxDrop))))
            .when(AscensionConfigChanceCondition.of(chanceKey)));
    }

    private static void addChaosCorePool(LootTable.Builder tableBuilder, String chanceKey) {
        tableBuilder.withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(ModItems.CHAOS_CORE))
            .when(AscensionConfigChanceCondition.of(chanceKey)));
    }
}
