package com.ascensioncores;

import com.ascensioncores.gear.StatPool;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public final class AscensionCoresConfig {

    public static int maxLevel = 5;
    public static int upgradeCoreCostLevel1 = 1;
    public static int upgradeCoreCostLevel2 = 4;
    public static int upgradeCoreCostLevel3 = 16;
    public static int upgradeCoreCostLevel4 = 32;
    public static int upgradeCoreCostLevel5 = 64;
    public static boolean showInventoryLevelMarkers = true;
    public static boolean playAnvilFeedback = true;
    public static boolean enableEnchantmentSlots = true;
    public static boolean enableSalvage = true;
    public static double salvageRefundPercent = 0.5;
    public static boolean chaosGambleMode = false;
    public static int upgradeXpCostLevel1 = 2;
    public static int upgradeXpCostLevel2 = 4;
    public static int upgradeXpCostLevel3 = 6;
    public static int upgradeXpCostLevel4 = 8;
    public static int upgradeXpCostLevel5 = 10;

    public static double mobAscensionCoreDropChancePerEquipment = 0.015;
    public static double mobChaosCoreDropChance = 0.004;
    public static int mobAscensionCoreMinDrop = 1;
    public static int mobAscensionCoreMaxDrop = 2;

    public static double chestAscensionCoreChance = 0.25;
    public static int chestAscensionCoreMinDrop = 1;
    public static int chestAscensionCoreMaxDrop = 2;
    public static double treasureAscensionCoreChance = 0.50;
    public static int treasureAscensionCoreMinDrop = 1;
    public static int treasureAscensionCoreMaxDrop = 4;
    public static double treasureChaosCoreChance = 0.10;
    public static double randomLootAscensionChance = 0.40;
    public static double treasureRandomLootAscensionChance = 0.6;

    public static Set<String> disabledWeaponTraits  = Set.of();
    public static Set<String> disabledRangedTraits  = Set.of();
    public static Set<String> disabledArmorTraits   = Set.of();
    public static Set<String> disabledToolTraits    = Set.of();

    public static boolean enableBetterVanillaMobsIntegration = true;
    public static double betterVanillaMobsAscensionCoreDropChance = 0.03;
    public static double betterVanillaMobsAscensionCoreDropChancePerStar = 0.03;
    public static double betterVanillaMobsChaosCoreDropChance = 0.01;
    public static double betterVanillaMobsChaosCoreDropChancePerStar = 0.01;

    public static boolean enableHostileMobsImproveIntegration = true;
    public static double hostileMobsImproveAscensionCoreChancePerLevel = 0.002;
    public static double hostileMobsImproveChaosCoreChancePerLevel = 0.0005;

    private static final Path CONFIG_PATH = Path.of("config", "ascensioncores.properties");

    private AscensionCoresConfig() {}

    public static void load(Logger logger) {
        Properties props = new Properties();

        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                props.load(r);
            } catch (IOException e) {
                logger.error("[AscensionCores] Failed to read config, using defaults", e);
            }
        }

        maxLevel = parseInt(props, "maxLevel", maxLevel, 1, 10, logger);
        upgradeCoreCostLevel1 = parseInt(props, "upgradeCoreCostLevel1", upgradeCoreCostLevel1, 1, 64, logger);
        upgradeCoreCostLevel2 = parseInt(props, "upgradeCoreCostLevel2", upgradeCoreCostLevel2, 1, 64, logger);
        upgradeCoreCostLevel3 = parseInt(props, "upgradeCoreCostLevel3", upgradeCoreCostLevel3, 1, 64, logger);
        upgradeCoreCostLevel4 = parseInt(props, "upgradeCoreCostLevel4", upgradeCoreCostLevel4, 1, 64, logger);
        upgradeCoreCostLevel5 = parseInt(props, "upgradeCoreCostLevel5", upgradeCoreCostLevel5, 1, 64, logger);

        showInventoryLevelMarkers = parseBoolean(props, "showInventoryLevelMarkers", showInventoryLevelMarkers, logger);
        playAnvilFeedback = parseBoolean(props, "playAnvilFeedback", playAnvilFeedback, logger);
        enableEnchantmentSlots = parseBoolean(props, "enableEnchantmentSlots", enableEnchantmentSlots, logger);
        enableSalvage = parseBoolean(props, "enableSalvage", enableSalvage, logger);
        salvageRefundPercent = parseDouble(props, "salvageRefundPercent", salvageRefundPercent, 0.0, 1.0, logger);
        chaosGambleMode = parseBoolean(props, "chaosGambleMode", chaosGambleMode, logger);
        upgradeXpCostLevel1 = parseInt(props, "upgradeXpCostLevel1", upgradeXpCostLevel1, 0, 1000, logger);
        upgradeXpCostLevel2 = parseInt(props, "upgradeXpCostLevel2", upgradeXpCostLevel2, 0, 1000, logger);
        upgradeXpCostLevel3 = parseInt(props, "upgradeXpCostLevel3", upgradeXpCostLevel3, 0, 1000, logger);
        upgradeXpCostLevel4 = parseInt(props, "upgradeXpCostLevel4", upgradeXpCostLevel4, 0, 1000, logger);
        upgradeXpCostLevel5 = parseInt(props, "upgradeXpCostLevel5", upgradeXpCostLevel5, 0, 1000, logger);

        mobAscensionCoreDropChancePerEquipment = parseDouble(props, "mobAscensionCoreDropChancePerEquipment", mobAscensionCoreDropChancePerEquipment, 0.0, 1.0, logger);
        mobChaosCoreDropChance = parseDouble(props, "mobChaosCoreDropChance", mobChaosCoreDropChance, 0.0, 1.0, logger);
        mobAscensionCoreMinDrop = parseInt(props, "mobAscensionCoreMinDrop", mobAscensionCoreMinDrop, 1, 64, logger);
        mobAscensionCoreMaxDrop = parseInt(props, "mobAscensionCoreMaxDrop", mobAscensionCoreMaxDrop, mobAscensionCoreMinDrop, 64, logger);

        chestAscensionCoreChance = parseDouble(props, "chestAscensionCoreChance", 
            parseDouble(props, "levelCoreChestChance", chestAscensionCoreChance, 0.0, 1.0, logger), 0.0, 1.0, logger);
        chestAscensionCoreMinDrop = parseInt(props, "chestAscensionCoreMinDrop", 
            parseInt(props, "levelCoreChestMinDrop", chestAscensionCoreMinDrop, 1, 64, logger), 1, 64, logger);
        chestAscensionCoreMaxDrop = parseInt(props, "chestAscensionCoreMaxDrop", 
            parseInt(props, "levelCoreChestMaxDrop", chestAscensionCoreMaxDrop, chestAscensionCoreMinDrop, 64, logger), chestAscensionCoreMinDrop, 64, logger);
        treasureAscensionCoreChance = parseDouble(props, "treasureAscensionCoreChance", treasureAscensionCoreChance, 0.0, 1.0, logger);
        treasureAscensionCoreMinDrop = parseInt(props, "treasureAscensionCoreMinDrop", treasureAscensionCoreMinDrop, 1, 64, logger);
        treasureAscensionCoreMaxDrop = parseInt(props, "treasureAscensionCoreMaxDrop", treasureAscensionCoreMaxDrop, treasureAscensionCoreMinDrop, 64, logger);
        treasureChaosCoreChance = parseDouble(props, "treasureChaosCoreChance", treasureChaosCoreChance, 0.0, 1.0, logger);
        randomLootAscensionChance = parseDouble(props, "randomLootAscensionChance", 
            parseDouble(props, "unenchantedLootAscensionChance", randomLootAscensionChance, 0.0, 1.0, logger), 0.0, 1.0, logger);
        treasureRandomLootAscensionChance = parseDouble(props, "treasureRandomLootAscensionChance", 
            parseDouble(props, "treasureUnenchantedLootAscensionChance", treasureRandomLootAscensionChance, 0.0, 1.0, logger), 0.0, 1.0, logger);

        enableBetterVanillaMobsIntegration = parseBoolean(props, "enableBetterVanillaMobsIntegration", enableBetterVanillaMobsIntegration, logger);
        betterVanillaMobsAscensionCoreDropChance = parseDouble(props, "betterVanillaMobsAscensionCoreDropChance", betterVanillaMobsAscensionCoreDropChance, 0.0, 1.0, logger);
        betterVanillaMobsAscensionCoreDropChancePerStar = parseDouble(props, "betterVanillaMobsAscensionCoreDropChancePerStar",
            betterVanillaMobsAscensionCoreDropChancePerStar, 0.0, 1.0, logger);
        betterVanillaMobsChaosCoreDropChance = parseDouble(props, "betterVanillaMobsChaosCoreDropChance", betterVanillaMobsChaosCoreDropChance, 0.0, 1.0, logger);
        betterVanillaMobsChaosCoreDropChancePerStar = parseDouble(props, "betterVanillaMobsChaosCoreDropChancePerStar",
            betterVanillaMobsChaosCoreDropChancePerStar, 0.0, 1.0, logger);

        enableHostileMobsImproveIntegration = parseBoolean(props, "enableHostileMobsImproveIntegration", enableHostileMobsImproveIntegration, logger);
        hostileMobsImproveAscensionCoreChancePerLevel = parseDouble(props, "hostileMobsImproveAscensionCoreChancePerLevel",
            hostileMobsImproveAscensionCoreChancePerLevel, 0.0, 1.0, logger);
        hostileMobsImproveChaosCoreChancePerLevel = parseDouble(props, "hostileMobsImproveChaosCoreChancePerLevel",
            hostileMobsImproveChaosCoreChancePerLevel, 0.0, 1.0, logger);

        disabledWeaponTraits = parseStringSet(props, "disabledWeaponTraits", logger);
        disabledRangedTraits = parseStringSet(props, "disabledRangedTraits", logger);
        disabledArmorTraits  = parseStringSet(props, "disabledArmorTraits",  logger);
        disabledToolTraits   = parseStringSet(props, "disabledToolTraits",   logger);

        StatPool.refresh();

        save(logger);
        logger.info("[AscensionCores] Config loaded");
    }

    public static void reload(Logger logger) {
        load(logger);
    }

    /** Live config lookup used by AscensionConfigChanceCondition (chest loot rates). */
    public static double getChance(String key) {
        return switch (key) {
            case "chestAscensionCoreChance" -> chestAscensionCoreChance;
            case "treasureAscensionCoreChance" -> treasureAscensionCoreChance;
            case "treasureChaosCoreChance" -> treasureChaosCoreChance;
            case "randomLootAscensionChance" -> randomLootAscensionChance;
            case "treasureRandomLootAscensionChance" -> treasureRandomLootAscensionChance;
            default -> 0.0;
        };
    }

    public static int getUpgradeCoreCost(int currentLevel) {
        return switch (currentLevel) {
            case 0 -> upgradeCoreCostLevel1;
            case 1 -> upgradeCoreCostLevel2;
            case 2 -> upgradeCoreCostLevel3;
            case 3 -> upgradeCoreCostLevel4;
            default -> upgradeCoreCostLevel5;
        };
    }

    public static int getUpgradeXpCost(int currentLevel) {
        return switch (currentLevel) {
            case 0 -> upgradeXpCostLevel1;
            case 1 -> upgradeXpCostLevel2;
            case 2 -> upgradeXpCostLevel3;
            case 3 -> upgradeXpCostLevel4;
            default -> upgradeXpCostLevel5;
        };
    }

    public static void save(Logger logger) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, toPropertiesString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("[AscensionCores] Failed to save config", e);
        }
    }

    private static String toPropertiesString() {
        return """
                # AscensionCores configuration
                # Changes take effect after /ascensioncores reload or server restart.

                # ── Core gameplay ─────────────────────────────────────────────────
                # Max ascension level any gear can reach (1-5). Tooltip caps and the
                # number of rolled stats scale with this.
                maxLevel=%d
                # Ascension Cores required for each upgrade level.
                # Level 1 means L0->L1, Level 2 means L1->L2, etc.
                upgradeCoreCostLevel1=%d
                upgradeCoreCostLevel2=%d
                upgradeCoreCostLevel3=%d
                upgradeCoreCostLevel4=%d
                upgradeCoreCostLevel5=%d
                # If true, leveled gear shows a small color-coded corner marker
                # in the inventory grid.
                showInventoryLevelMarkers=%s
                # If true, anvil upgrade/reroll plays an audio cue on success.
                playAnvilFeedback=%s
                # If true, leveled gear has a per-level enchantment slot cap:
                # gear can hold at most <ascension level> non-curse enchantments,
                # and un-ascended gear cannot be enchanted at all. If false, the
                # whole enchantment-slot system is off (vanilla enchanting, no
                # slot tooltip line).
                enableEnchantmentSlots=%s
                # If true, placing leveled gear alone in an anvil (no second item,
                # no rename) salvages it: the gear is consumed and a portion of the
                # Ascension Cores invested is returned. Costs XP levels equal to
                # the gear's ascension level.
                enableSalvage=%s
                # Fraction of total Ascension Cores spent that salvage returns (0.0-1.0).
                salvageRefundPercent=%.2f
                # If true, Chaos Core rerolls become a gamble: each rerolled trait
                # has a chance to roll above its normal maximum, and an equal
                # chance to bust to its minimum.
                chaosGambleMode=%s
                # XP levels charged by the anvil for each Ascension upgrade.
                # Level 1 means L0->L1, Level 2 means L1->L2, etc.
                upgradeXpCostLevel1=%d
                upgradeXpCostLevel2=%d
                upgradeXpCostLevel3=%d
                upgradeXpCostLevel4=%d
                upgradeXpCostLevel5=%d

                # ── Mob drops ─────────────────────────────────────────────────────
                # Chance per piece of gear (armor or weapon) a hostile mob has equipped to drop Ascension Cores.
                mobAscensionCoreDropChancePerEquipment=%.4f
                # Chance per kill for a non-BVM hostile mob to drop a Chaos Core.
                # BVM-tagged mobs use the BVM chaos rates below instead.
                mobChaosCoreDropChance=%.4f
                # When a mob drops Ascension Cores, how many drop (uniform between min and max).
                mobAscensionCoreMinDrop=%d
                mobAscensionCoreMaxDrop=%d

                # ── Chest loot ────────────────────────────────────────────────────
                # Chance per generated chest (dungeons, mineshafts, strongholds, temples,
                # shipwrecks, trial chambers, etc.) to contain Ascension Cores.
                chestAscensionCoreChance=%.4f
                # When a chest rolls cores, how many drop (uniform between min and max).
                chestAscensionCoreMinDrop=%d
                chestAscensionCoreMaxDrop=%d
                # Chance for Ascension Cores in Bastion Treasure, End City Treasure, and Ancient City chests.
                treasureAscensionCoreChance=%.4f
                # When a treasure chest rolls cores, how many drop (uniform between min and max).
                treasureAscensionCoreMinDrop=%d
                treasureAscensionCoreMaxDrop=%d
                # Chance for a Chaos Core in Bastion Treasure, End City Treasure, and Ancient City chests.
                treasureChaosCoreChance=%.4f
                # Chance for a piece of gear to naturally roll Ascension levels when found in chests.
                # When it rolls, target level is weighted (higher levels are rarer).
                randomLootAscensionChance=%.4f
                treasureRandomLootAscensionChance=%.4f

                # ── Trait pools ───────────────────────────────────────────────
                # Comma-separated list of trait IDs to exclude from rolling, per pool.
                # Leave blank to allow all. Example: disabledWeaponTraits=shock,venom
                #
                # Weapon traits:  life_steal, reach, attack_speed, armor_shred, toughness_shred,
                #   experience_bonus, critical_damage, execution_damage, ambush_damage, frostbite, venom,
                #   shock, sprinting_speed, stealth, jump, repair_discount,
                #   opening_damage, wither, chain_damage, heal_suppress
                # Ranged traits:  life_steal, armor_shred, toughness_shred, experience_bonus,
                #   critical_damage, execution_damage, ambush_damage, venom, shock,
                #   sprinting_speed, stealth, jump, repair_discount,
                #   opening_damage, wither, heal_suppress, pinning, overcharge_damage
                # Armor traits:   evasion, deflection, effect_resist, melee_resistance,
                #   natural_regeneration, low_health_guard, sneak_guard, sprinting_speed,
                #   consuming_speed, repair_discount, stealth, tamed_resistance, stamina, experience_bonus, jump,
                #   emergency_healing, standstill_guard, max_health
                # Tool traits:    experience_bonus, repair_discount, jump,
                #   natural_regeneration, stamina, stealth, sprinting_speed
                disabledWeaponTraits=%s
                disabledRangedTraits=%s
                disabledArmorTraits=%s
                disabledToolTraits=%s

                # ── Compatibility ─────────────────────────────────────────────────
                # If true, BetterVanillaMobs "touched" mobs get the special
                # rarity-scaled drop rates below instead of the generic mob rates.
                enableBetterVanillaMobsIntegration=%s
                # BVM-only override: Ascension Core chance for a 1-star touched mob.
                # 2-5 star mobs add the per-star value below for each extra star.
                # Formula: chance = base + ((stars - 1) * perStar).
                betterVanillaMobsAscensionCoreDropChance=%.4f
                # Added to the Ascension Core chance for each BVM rarity star after the first.
                # Default: 1-star 4%%, 2-star 5.5%%, 3-star 7%%, 4-star 8.5%%, 5-star/Alpha 10%%.
                betterVanillaMobsAscensionCoreDropChancePerStar=%.4f
                # BVM-only override: Chaos Core chance for a 1-star touched mob.
                # Replaces mobChaosCoreDropChance for BVM mobs.
                betterVanillaMobsChaosCoreDropChance=%.4f
                # Added to the Chaos Core chance for each BVM rarity star after the first.
                # Default: 1-star 0.3%%, 2-star 0.6%%, 3-star 0.9%%, 4-star 1.2%%, 5-star/Alpha 1.5%%.
                betterVanillaMobsChaosCoreDropChancePerStar=%.4f
                # If true, integrates with the "Hostile Mobs Improve Over Time" datapack:
                # core drop chance scales with the killer's HostileMobs difficulty score.
                # No effect if the datapack isn't installed.
                enableHostileMobsImproveIntegration=%s
                # Ascension Core chance added per point of the killer's difficulty score.
                hostileMobsImproveAscensionCoreChancePerLevel=%.4f
                # Chaos Core chance added per point of the killer's difficulty score.
                hostileMobsImproveChaosCoreChancePerLevel=%.4f
                """.formatted(
                    maxLevel,
                    upgradeCoreCostLevel1,
                    upgradeCoreCostLevel2,
                    upgradeCoreCostLevel3,
                    upgradeCoreCostLevel4,
                    upgradeCoreCostLevel5,
                    showInventoryLevelMarkers,
                    playAnvilFeedback,
                    enableEnchantmentSlots,
                    enableSalvage,
                    salvageRefundPercent,
                    chaosGambleMode,
                    upgradeXpCostLevel1,
                    upgradeXpCostLevel2,
                    upgradeXpCostLevel3,
                    upgradeXpCostLevel4,
                    upgradeXpCostLevel5,
                    mobAscensionCoreDropChancePerEquipment,
                    mobChaosCoreDropChance,
                    mobAscensionCoreMinDrop,
                    mobAscensionCoreMaxDrop,
                    chestAscensionCoreChance,
                    chestAscensionCoreMinDrop,
                    chestAscensionCoreMaxDrop,
                    treasureAscensionCoreChance,
                    treasureAscensionCoreMinDrop,
                    treasureAscensionCoreMaxDrop,
                    treasureChaosCoreChance,
                    randomLootAscensionChance,
                    treasureRandomLootAscensionChance,
                    setToString(disabledWeaponTraits),
                    setToString(disabledRangedTraits),
                    setToString(disabledArmorTraits),
                    setToString(disabledToolTraits),
                    enableBetterVanillaMobsIntegration,
                    betterVanillaMobsAscensionCoreDropChance,
                    betterVanillaMobsAscensionCoreDropChancePerStar,
                    betterVanillaMobsChaosCoreDropChance,
                    betterVanillaMobsChaosCoreDropChancePerStar,
                    enableHostileMobsImproveIntegration,
                    hostileMobsImproveAscensionCoreChancePerLevel,
                    hostileMobsImproveChaosCoreChancePerLevel
                );
    }

    static boolean parseBoolean(Properties props, String key, boolean def, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null) return def;
        String s = raw.trim().toLowerCase();
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        logger.warn("[AscensionCores] '{}' is not a valid boolean ('{}'), using default {}", key, raw, def);
        return def;
    }

    static int parseInt(Properties props, String key, int def, int min, int max, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null) return def;
        try {
            int val = Integer.parseInt(raw.trim());
            if (val < min || val > max) {
                logger.warn("[AscensionCores] '{}' value {} out of range [{}, {}], clamping", key, val, min, max);
                return Math.max(min, Math.min(max, val));
            }
            return val;
        } catch (NumberFormatException e) {
            logger.warn("[AscensionCores] '{}' is not a valid integer ('{}'), using default {}", key, raw, def);
            return def;
        }
    }

    private static String setToString(Set<String> set) {
        return String.join(",", set);
    }

    static Set<String> parseStringSet(Properties props, String key, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null || raw.isBlank()) return Set.of();
        return Arrays.stream(raw.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toUnmodifiableSet());
    }

    static double parseDouble(Properties props, String key, double def, double min, double max, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null) return def;
        try {
            double val = Double.parseDouble(raw.trim());
            if (val < min || val > max) {
                logger.warn("[AscensionCores] '{}' value {} out of range [{}, {}], clamping", key, val, min, max);
                return Math.max(min, Math.min(max, val));
            }
            return val;
        } catch (NumberFormatException e) {
            logger.warn("[AscensionCores] '{}' is not a valid number ('{}'), using default {}", key, raw, def);
            return def;
        }
    }

}
