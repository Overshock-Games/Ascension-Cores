package com.ascensioncores.gear;

import com.ascensioncores.component.ModComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;

import java.util.ArrayList;
import java.util.List;

public final class GearHelper {

    // ── Classification ──────────────────────────────────────────────────────

    public static boolean isWeapon(ItemStack stack) {
        return stack.has(DataComponents.WEAPON);
    }

    public static boolean isArmor(ItemStack stack) {
        Equippable eq = stack.get(DataComponents.EQUIPPABLE);
        if (eq == null) return false;
        EquipmentSlot slot = eq.slot();
        return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST
            || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    /** Tools = has TOOL component but not WEAPON (pickaxes, shovels, hoes). Axes are weapons. */
    public static boolean isTool(ItemStack stack) {
        return stack.has(DataComponents.TOOL) && !stack.has(DataComponents.WEAPON);
    }

    public static boolean isGear(ItemStack stack) {
        return isWeapon(stack) || isArmor(stack) || isTool(stack);
    }

    // ── Component helpers ───────────────────────────────────────────────────

    public static boolean hasAscensionData(ItemStack stack) {
        return stack.has(ModComponents.ASCENSION_LEVEL);
    }

    public static int getLevel(ItemStack stack) {
        return stack.getOrDefault(ModComponents.ASCENSION_LEVEL, 0);
    }

    public static List<RolledStat> getRolledStats(ItemStack stack) {
        List<RolledStat> stats = stack.get(ModComponents.ROLLED_STATS);
        return stats != null ? stats : List.of();
    }

    // ── Upgrade operations ──────────────────────────────────────────────────

    public static void levelUp(ItemStack stack) {
        int newLevel = getLevel(stack) + 1;
        stack.set(ModComponents.ASCENSION_LEVEL, newLevel);

        List<RolledStat> stats = new ArrayList<>(getRolledStats(stack));
        if (newLevel <= getMaterialCapacity(stack)) {
            RolledStat rolled = StatPool.rollStat(stats, getPool(stack));
            if (rolled != null) stats.add(rolled);
        }
        stack.set(ModComponents.ROLLED_STATS, stats);
        rebuildAttributes(stack, newLevel, stats);
    }

    public static void reroll(ItemStack stack) {
        int level = getLevel(stack);
        int statCount = Math.min(level, getMaterialCapacity(stack));
        List<StatPool.StatDef> pool = getPool(stack);

        List<RolledStat> stats = new ArrayList<>();
        for (int i = 0; i < statCount; i++) {
            RolledStat s = StatPool.rollStat(stats, pool);
            if (s != null) stats.add(s);
        }
        stack.set(ModComponents.ROLLED_STATS, stats);
        rebuildAttributes(stack, level, stats);
    }

    // ── Attribute building ──────────────────────────────────────────────────

    public static void rebuildAttributes(ItemStack stack, int level, List<RolledStat> stats) {
        EquipmentSlotGroup slotGroup = isArmor(stack) ? getArmorSlotGroup(stack) : EquipmentSlotGroup.MAINHAND;

        ItemAttributeModifiers existing = stack.getOrDefault(
            DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
            if (!"ascensioncores".equals(entry.modifier().id().getNamespace())) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
        }

        for (int i = 0; i < stats.size(); i++) {
            RolledStat rolled = stats.get(i);
            StatPool.StatDef def = StatPool.getById(rolled.id());
            if (def == null) continue;
            double amount = rolled.amount() * (level - i);
            AttributeModifier mod = new AttributeModifier(
                Identifier.fromNamespaceAndPath("ascensioncores", "stat_" + def.id() + "_" + i),
                amount,
                AttributeModifier.Operation.ADD_VALUE
            );
            builder.add(def.attribute(), mod, slotGroup);
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    // ── Material capacity ───────────────────────────────────────────────────

    public static int getMaterialCapacity(ItemStack stack) {
        Item item = stack.getItem();
        if (isTier0(item)) return 0;
        if (isTier1(item)) return 1;
        if (isTier2(item)) return 2;
        if (isTier3(item)) return 3;
        if (isTier4(item)) return 4;
        return 0;
    }

    private static boolean isTier0(Item item) {
        return item == Items.WOODEN_SWORD   || item == Items.WOODEN_AXE
            || item == Items.WOODEN_PICKAXE || item == Items.WOODEN_SHOVEL || item == Items.WOODEN_HOE
            || item == Items.GOLDEN_SWORD   || item == Items.GOLDEN_AXE
            || item == Items.GOLDEN_PICKAXE || item == Items.GOLDEN_SHOVEL || item == Items.GOLDEN_HOE
            || item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE
            || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }

    private static boolean isTier1(Item item) {
        return item == Items.STONE_SWORD    || item == Items.STONE_AXE
            || item == Items.STONE_PICKAXE  || item == Items.STONE_SHOVEL || item == Items.STONE_HOE
            || item == Items.COPPER_SWORD   || item == Items.COPPER_AXE
            || item == Items.COPPER_PICKAXE || item == Items.COPPER_SHOVEL || item == Items.COPPER_HOE
            || item == Items.COPPER_HELMET  || item == Items.COPPER_CHESTPLATE
            || item == Items.COPPER_LEGGINGS || item == Items.COPPER_BOOTS
            || item == Items.CHAINMAIL_HELMET   || item == Items.CHAINMAIL_CHESTPLATE
            || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_BOOTS;
    }

    private static boolean isTier2(Item item) {
        return item == Items.IRON_SWORD    || item == Items.IRON_AXE
            || item == Items.IRON_PICKAXE  || item == Items.IRON_SHOVEL || item == Items.IRON_HOE
            || item == Items.IRON_HELMET   || item == Items.IRON_CHESTPLATE
            || item == Items.IRON_LEGGINGS || item == Items.IRON_BOOTS;
    }

    private static boolean isTier3(Item item) {
        return item == Items.DIAMOND_SWORD    || item == Items.DIAMOND_AXE
            || item == Items.DIAMOND_PICKAXE  || item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_HOE
            || item == Items.DIAMOND_HELMET   || item == Items.DIAMOND_CHESTPLATE
            || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_BOOTS
            || item == Items.TURTLE_HELMET;
    }

    private static boolean isTier4(Item item) {
        return item == Items.NETHERITE_SWORD    || item == Items.NETHERITE_AXE
            || item == Items.NETHERITE_PICKAXE  || item == Items.NETHERITE_SHOVEL || item == Items.NETHERITE_HOE
            || item == Items.NETHERITE_HELMET   || item == Items.NETHERITE_CHESTPLATE
            || item == Items.NETHERITE_LEGGINGS || item == Items.NETHERITE_BOOTS;
    }

    // ── Internal helpers ────────────────────────────────────────────────────

    public static List<StatPool.StatDef> getPool(ItemStack stack) {
        if (isWeapon(stack)) return StatPool.WEAPON_POOL;
        if (isArmor(stack))  return StatPool.ARMOR_POOL;
        return StatPool.TOOL_POOL;
    }

    private static EquipmentSlotGroup getArmorSlotGroup(ItemStack stack) {
        Equippable eq = stack.get(DataComponents.EQUIPPABLE);
        if (eq == null) return EquipmentSlotGroup.ANY;
        return switch (eq.slot()) {
            case HEAD  -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS  -> EquipmentSlotGroup.LEGS;
            case FEET  -> EquipmentSlotGroup.FEET;
            default    -> EquipmentSlotGroup.ANY;
        };
    }
}
