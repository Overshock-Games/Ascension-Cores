package com.ascensioncores.gear;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.puffish.attributesmod.api.PuffishAttributes;

import java.text.DecimalFormat;
import com.ascensioncores.AscensionCoresConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class StatPool {

    /**
     * @param minAmount  lower bound of the per-multiplier random roll
     * @param maxAmount  upper bound of the per-multiplier random roll
     */
    public record StatDef(
        String id,
        Holder<Attribute> attribute,
        double minAmount,
        double maxAmount,
        String displayName,
        String unit,   // "%" → multiply by 100 and append %; otherwise append as-is
        AttributeModifier.Operation operation,
        int minLevel    // earliest item ascension level at which this stat may roll
    ) {
        public StatDef(String id, Holder<Attribute> attribute, double minAmount, double maxAmount, String displayName, String unit) {
            this(id, attribute, minAmount, maxAmount, displayName, unit, AttributeModifier.Operation.ADD_VALUE, 1);
        }
        public StatDef(String id, Holder<Attribute> attribute, double minAmount, double maxAmount, String displayName, String unit, AttributeModifier.Operation operation) {
            this(id, attribute, minAmount, maxAmount, displayName, unit, operation, 1);
        }
        public StatDef(String id, Holder<Attribute> attribute, double minAmount, double maxAmount, String displayName, String unit, int minLevel) {
            this(id, attribute, minAmount, maxAmount, displayName, unit, AttributeModifier.Operation.ADD_VALUE, minLevel);
        }
    }

    // ── Weapon pool (swords + axes) ─────────────────────────────────────────
    public static List<StatDef> WEAPON_POOL = createWeaponPool();

    // ── Ranged weapon pool (bow, crossbow, trident) ────────────────────────
    public static List<StatDef> RANGED_WEAPON_POOL = createRangedWeaponPool();

    // ── Armor pool ──────────────────────────────────────────────────────────
    public static List<StatDef> ARMOR_POOL = createArmorPool();

    // ── Tool pool (pickaxe, shovel, hoe) ────────────────────────────────────
    public static List<StatDef> TOOL_POOL = createToolPool();

    private static final Random RANDOM = new Random();
    private static final DecimalFormat COMPACT_DECIMAL = new DecimalFormat("0.##");

    public static void refresh() {
        WEAPON_POOL       = filter(createWeaponPool(),       AscensionCoresConfig.disabledWeaponTraits);
        RANGED_WEAPON_POOL= filter(createRangedWeaponPool(), AscensionCoresConfig.disabledRangedTraits);
        ARMOR_POOL        = filter(createArmorPool(),        AscensionCoresConfig.disabledArmorTraits);
        TOOL_POOL         = filter(createToolPool(),         AscensionCoresConfig.disabledToolTraits);
    }

    private static List<StatDef> filter(List<StatDef> pool, Set<String> disabled) {
        if (disabled.isEmpty()) return pool;
        return pool.stream().filter(d -> !disabled.contains(d.id())).toList();
    }

    private static List<StatDef> createWeaponPool() {
        List<StatDef> pool = new ArrayList<>(List.of(
            new StatDef("life_steal",           PuffishAttributes.LIFE_STEAL,           0.02, 0.06, "Life Steal",           "%"),
            new StatDef("reach",                Attributes.ENTITY_INTERACTION_RANGE,    0.10, 0.30, "Reach",                " blk"),
            new StatDef("attack_speed",         Attributes.ATTACK_SPEED,                0.05, 0.15, "Attack Speed",         " pts"),
            new StatDef("armor_shred",          PuffishAttributes.ARMOR_SHRED,          0.20, 0.70, "Armor Shred",          " pts"),
            new StatDef("toughness_shred",      PuffishAttributes.TOUGHNESS_SHRED,      0.10, 0.35, "Toughness Shred",      " pts"),
            new StatDef("experience_bonus",     PuffishAttributes.EXPERIENCE,           0.05, 0.20, "Experience Bonus",     "%"),
            new StatDef("critical_damage",      null,                                   0.10, 0.25, "Critical Damage",      "%"),
            new StatDef("execution_damage",     null,                                   0.08, 0.18, "Execution Damage",     "%"),
            new StatDef("ambush_damage",        null,                                   0.08, 0.18, "Ambush Damage",        "%"),
            new StatDef("frostbite",            null,                                   0.03, 0.10, "Frostbite",            "%"),
            new StatDef("venom",                null,                                   0.03, 0.10, "Venom",                "%"),
            new StatDef("shock",                null,                                   0.03, 0.10, "Shock",                "%"),
            new StatDef("sprinting_speed",      PuffishAttributes.SPRINTING_SPEED,      0.03, 0.10, "Sprint Speed",         "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("stealth",              PuffishAttributes.STEALTH,              0.10, 0.30, "Stealth",              " blk"),
            new StatDef("jump",                 PuffishAttributes.JUMP,                 0.05, 0.20, "Jump Height",          "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("repair_discount",      PuffishAttributes.REPAIR_COST,         -0.15,-0.05, "Repair Discount",      "%"),
            new StatDef("opening_damage",       null,                                   0.05, 0.10, "Opening Damage",       "%"),
            new StatDef("wither",               null,                                   0.03, 0.08, "Wither",               "%"),
            new StatDef("chain_damage",         null,                                   0.01, 0.03, "Chain Damage",         "%"),
            new StatDef("heal_suppress",        null,                                   0.03, 0.08, "Heal Suppress",        "%")
        ));
        return List.copyOf(pool);
    }

    private static List<StatDef> createRangedWeaponPool() {
        // Same as weapon pool but skips melee-only stats (reach, attack_speed).
        // ProjectileDamageMixin handles crit/execute/ambush/frost/venom/shock for arrows.
        List<StatDef> pool = new ArrayList<>(List.of(
            new StatDef("life_steal",           PuffishAttributes.LIFE_STEAL,           0.02, 0.06, "Life Steal",           "%"),
            new StatDef("armor_shred",          PuffishAttributes.ARMOR_SHRED,          0.20, 0.70, "Armor Shred",          " pts"),
            new StatDef("toughness_shred",      PuffishAttributes.TOUGHNESS_SHRED,      0.10, 0.35, "Toughness Shred",      " pts"),
            new StatDef("experience_bonus",     PuffishAttributes.EXPERIENCE,           0.05, 0.20, "Experience Bonus",     "%"),
            new StatDef("critical_damage",      null,                                   0.10, 0.25, "Critical Damage",      "%"),
            new StatDef("execution_damage",     null,                                   0.08, 0.18, "Execution Damage",     "%"),
            new StatDef("ambush_damage",        null,                                   0.08, 0.18, "Ambush Damage",        "%"),
            new StatDef("frostbite",            null,                                   0.03, 0.10, "Frostbite",            "%"),
            new StatDef("venom",                null,                                   0.03, 0.10, "Venom",                "%"),
            new StatDef("shock",                null,                                   0.03, 0.10, "Shock",                "%"),
            new StatDef("sprinting_speed",      PuffishAttributes.SPRINTING_SPEED,      0.03, 0.10, "Sprint Speed",         "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("stealth",              PuffishAttributes.STEALTH,              0.10, 0.30, "Stealth",              " blk"),
            new StatDef("jump",                 PuffishAttributes.JUMP,                 0.05, 0.20, "Jump Height",          "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("repair_discount",      PuffishAttributes.REPAIR_COST,         -0.15,-0.05, "Repair Discount",      "%"),
            new StatDef("opening_damage",       null,                                   0.05, 0.10, "Opening Damage",       "%"),
            new StatDef("wither",               null,                                   0.03, 0.08, "Wither",               "%"),
            new StatDef("heal_suppress",        null,                                   0.03, 0.08, "Heal Suppress",        "%"),
            new StatDef("pinning",              null,                                   0.03, 0.08, "Pinning",              "%"),
            new StatDef("overcharge_damage",    null,                                   0.08, 0.20, "Overcharge Damage",    "%")
        ));
        return List.copyOf(pool);
    }

    private static List<StatDef> createArmorPool() {
        List<StatDef> pool = new ArrayList<>(List.of(
            new StatDef("evasion",              null,                                   0.005,0.02, "Evasion",              "%"),
            new StatDef("deflection",           null,                                   0.01, 0.04, "Deflection",           "%"),
            new StatDef("tenacity",             null,                                   0.04, 0.10, "Tenacity",             "%"),
            new StatDef("melee_resistance",     null,                                   0.02, 0.06, "Melee Resistance",     "%"),
            new StatDef("natural_regeneration", PuffishAttributes.NATURAL_REGENERATION, 0.05, 0.20, "Natural Regeneration", "%"),
            new StatDef("low_health_guard",     null,                                   0.05, 0.12, "Low Health Guard",     "%"),
            new StatDef("steady_guard",         null,                                   0.04, 0.10, "Steady Guard",         "%"),
            new StatDef("sprinting_speed",      PuffishAttributes.SPRINTING_SPEED,      0.03, 0.10, "Sprint Speed",         "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("consuming_speed",      PuffishAttributes.CONSUMING_SPEED,      0.03, 0.10, "Consuming Speed",      "%"),
            new StatDef("repair_discount",      PuffishAttributes.REPAIR_COST,         -0.15,-0.05, "Repair Discount",      "%"),
            new StatDef("stealth",              PuffishAttributes.STEALTH,              0.10, 0.30, "Stealth",              " blk"),
            new StatDef("tamed_resistance",     PuffishAttributes.TAMED_RESISTANCE,     0.10, 0.30, "Tamed Resistance",     " DMG"),
            new StatDef("stamina",              PuffishAttributes.STAMINA,              0.20, 0.60, "Stamina",              " pts"),
            new StatDef("experience_bonus",     PuffishAttributes.EXPERIENCE,           0.05, 0.20, "Experience Bonus",     "%"),
            new StatDef("jump",                 PuffishAttributes.JUMP,                 0.05, 0.20, "Jump Height",          "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("emergency_healing",    null,                                   0.02, 0.05, "Emergency Healing",    "%"),
            new StatDef("bulwark",              null,                                   0.05, 0.15, "Bulwark",              "%"),
            new StatDef("vigor",                Attributes.MAX_HEALTH,                  0.20, 0.60, "Vigor",                " HP")
        ));
        return List.copyOf(pool);
    }

    private static List<StatDef> createToolPool() {
        List<StatDef> pool = new ArrayList<>(List.of(
            new StatDef("experience_bonus",     PuffishAttributes.EXPERIENCE,           0.05, 0.20, "Experience Bonus",     "%"),
            new StatDef("repair_discount",      PuffishAttributes.REPAIR_COST,         -0.15,-0.05, "Repair Discount",      "%"),
            new StatDef("consuming_speed",      PuffishAttributes.CONSUMING_SPEED,      0.03, 0.10, "Consuming Speed",      "%"),
            new StatDef("jump",                 PuffishAttributes.JUMP,                 0.05, 0.20, "Jump Height",          "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            new StatDef("natural_regeneration", PuffishAttributes.NATURAL_REGENERATION, 0.05, 0.20, "Natural Regeneration", "%"),
            new StatDef("stamina",              PuffishAttributes.STAMINA,              0.20, 0.60, "Stamina",              " pts"),
            new StatDef("stealth",              PuffishAttributes.STEALTH,              0.10, 0.30, "Stealth",              " blk"),
            new StatDef("sprinting_speed",      PuffishAttributes.SPRINTING_SPEED,      0.03, 0.10, "Sprint Speed",         "%", AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        ));
        return List.copyOf(pool);
    }

    // ── Rolling ─────────────────────────────────────────────────────────────

    /** Rolls a random stat from {@code pool} not already present in {@code existing}. Returns null if pool exhausted. */
    public static RolledStat rollStat(List<RolledStat> existing, List<StatDef> pool) {
        return rollStat(existing, pool, Integer.MAX_VALUE, RANDOM);
    }

    public static RolledStat rollStat(List<RolledStat> existing, List<StatDef> pool, Random random) {
        return rollStat(existing, pool, Integer.MAX_VALUE, random);
    }

    public static RolledStat rollStat(List<RolledStat> existing, List<StatDef> pool, int currentLevel) {
        return rollStat(existing, pool, currentLevel, RANDOM);
    }

    /** Rolls only from stats whose {@code minLevel <= currentLevel}. */
    public static RolledStat rollStat(List<RolledStat> existing, List<StatDef> pool, int currentLevel, Random random) {
        Set<String> existingIds = existing.stream().map(RolledStat::id).collect(Collectors.toSet());
        List<StatDef> available = pool.stream()
            .filter(def -> !existingIds.contains(def.id()))
            .filter(def -> def.minLevel() <= currentLevel)
            .toList();
        if (available.isEmpty()) return null;

        StatDef chosen = available.get(random.nextInt(available.size()));
        double raw = chosen.minAmount() + random.nextDouble() * (chosen.maxAmount() - chosen.minAmount());
        double amount = Math.round(raw * 100.0) / 100.0;
        return new RolledStat(chosen.id(), amount);
    }

    // ── Lookup ──────────────────────────────────────────────────────────────

    public static StatDef getById(String id) {
        for (List<StatDef> pool : List.of(WEAPON_POOL, RANGED_WEAPON_POOL, ARMOR_POOL, TOOL_POOL)) {
            for (StatDef def : pool) if (def.id().equals(id)) return def;
        }
        return null;
    }

    // ── Formatting ──────────────────────────────────────────────────────────

    public static String formatValue(StatDef def, double scaledAmount) {
        if ("%".equals(def.unit())) {
            return formatSigned(scaledAmount * 100) + "%";
        }
        if (" ticks".equals(def.unit())) {
            return formatSigned(Math.round(scaledAmount)) + def.unit();
        }
        return formatSigned(scaledAmount) + def.unit();
    }

    private static String formatSigned(double value) {
        return value < 0
            ? COMPACT_DECIMAL.format(value)
            : "+" + COMPACT_DECIMAL.format(value);
    }
}
