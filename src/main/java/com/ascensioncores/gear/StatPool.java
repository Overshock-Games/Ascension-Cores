package com.ascensioncores.gear;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.puffish.attributesmod.api.PuffishAttributes;

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
        String unit   // "%" → multiply by 100 and append %; otherwise append as-is
    ) {}

    // ── Weapon pool (swords + axes) ─────────────────────────────────────────
    public static final List<StatDef> WEAPON_POOL = List.of(
        new StatDef("life_steal",           PuffishAttributes.LIFE_STEAL,           0.02, 0.06, "Life Steal",           "%"),
        new StatDef("toughness_shred",      PuffishAttributes.TOUGHNESS_SHRED,      0.30, 1.00, "Toughness Shred",      " pts"),
        new StatDef("armor_shred",          PuffishAttributes.ARMOR_SHRED,          0.50, 2.00, "Armor Shred",          " pts"),
        new StatDef("experience",           PuffishAttributes.EXPERIENCE,           0.05, 0.20, "XP Bonus",             "%"),
        new StatDef("sword_damage",         PuffishAttributes.SWORD_DAMAGE,         0.50, 2.00, "Sword Damage",         " DMG"),
        new StatDef("axe_damage",           PuffishAttributes.AXE_DAMAGE,           0.50, 2.00, "Axe Damage",           " DMG"),
        new StatDef("healing",              PuffishAttributes.HEALING,              0.05, 0.15, "Healing Amp.",         "%"),
        new StatDef("protection_shred",     PuffishAttributes.PROTECTION_SHRED,     0.50, 2.00, "Protection Shred",     " pts"),
        new StatDef("melee_resistance_shred",PuffishAttributes.MELEE_RESISTANCE_SHRED,0.30,1.00,"Melee Resist. Shred",  " pts")
    );

    // ── Armor pool ──────────────────────────────────────────────────────────
    public static final List<StatDef> ARMOR_POOL = List.of(
        new StatDef("magic_resistance",     PuffishAttributes.MAGIC_RESISTANCE,     0.50, 2.00, "Magic Resist.",        " DMG"),
        new StatDef("melee_resistance",     PuffishAttributes.MELEE_RESISTANCE,     0.50, 2.00, "Melee Resist.",        " DMG"),
        new StatDef("natural_regeneration", PuffishAttributes.NATURAL_REGENERATION, 0.05, 0.20, "Natural Regen",        "%"),
        new StatDef("stealth",              PuffishAttributes.STEALTH,              0.50, 2.00, "Stealth",              " blk"),
        new StatDef("tamed_resistance",     PuffishAttributes.TAMED_RESISTANCE,     0.50, 2.00, "Tamed Resist.",        " DMG"),
        new StatDef("healing",              PuffishAttributes.HEALING,              0.05, 0.15, "Healing Amp.",         "%"),
        new StatDef("stamina",              PuffishAttributes.STAMINA,              0.50, 2.00, "Stamina",              " pts"),
        new StatDef("experience",           PuffishAttributes.EXPERIENCE,           0.05, 0.20, "XP Bonus",             "%")
    );

    // ── Tool pool (pickaxe, shovel, hoe) ────────────────────────────────────
    public static final List<StatDef> TOOL_POOL = List.of(
        new StatDef("experience",           PuffishAttributes.EXPERIENCE,           0.05, 0.20, "XP Bonus",             "%"),
        new StatDef("jump",                 PuffishAttributes.JUMP,                 0.05, 0.20, "Jump Height",          "%"),
        new StatDef("natural_regeneration", PuffishAttributes.NATURAL_REGENERATION, 0.05, 0.20, "Natural Regen",        "%"),
        new StatDef("stamina",              PuffishAttributes.STAMINA,              0.50, 2.00, "Stamina",              " pts"),
        new StatDef("healing",              PuffishAttributes.HEALING,              0.05, 0.15, "Healing Amp.",         "%"),
        new StatDef("stealth",              PuffishAttributes.STEALTH,              0.50, 2.00, "Stealth",              " blk")
    );

    private static final Random RANDOM = new Random();

    // ── Rolling ─────────────────────────────────────────────────────────────

    /** Rolls a random stat from {@code pool} not already present in {@code existing}. Returns null if pool exhausted. */
    public static RolledStat rollStat(List<RolledStat> existing, List<StatDef> pool) {
        Set<String> existingIds = existing.stream().map(RolledStat::id).collect(Collectors.toSet());
        List<StatDef> available = pool.stream()
            .filter(def -> !existingIds.contains(def.id()))
            .toList();
        if (available.isEmpty()) return null;

        StatDef chosen = available.get(RANDOM.nextInt(available.size()));
        double raw = chosen.minAmount() + RANDOM.nextDouble() * (chosen.maxAmount() - chosen.minAmount());
        double amount = Math.round(raw * 100.0) / 100.0;
        return new RolledStat(chosen.id(), amount);
    }

    // ── Lookup ──────────────────────────────────────────────────────────────

    public static StatDef getById(String id) {
        for (List<StatDef> pool : List.of(WEAPON_POOL, ARMOR_POOL, TOOL_POOL)) {
            for (StatDef def : pool) if (def.id().equals(id)) return def;
        }
        return null;
    }

    // ── Formatting ──────────────────────────────────────────────────────────

    public static String formatValue(StatDef def, double scaledAmount) {
        if ("%".equals(def.unit())) {
            return String.format("+%.0f%%", scaledAmount * 100);
        }
        return scaledAmount < 0
            ? String.format("%.2f%s", scaledAmount, def.unit())
            : String.format("+%.2f%s", scaledAmount, def.unit());
    }
}
