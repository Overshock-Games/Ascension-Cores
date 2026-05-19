package com.ascensioncores.event;

import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.RolledStat;
import com.ascensioncores.gear.StatPool;
import com.ascensioncores.item.ModItems;
import com.ascensioncores.mixin.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class TooltipHandler {

    public static void register() {
        ItemTooltipCallback.EVENT.register(TooltipHandler::appendTooltip);
    }

    private static void appendTooltip(
            ItemStack stack,
            net.minecraft.world.item.Item.TooltipContext context,
            net.minecraft.world.item.TooltipFlag flag,
            List<Component> lines) {

        if (stack.is(ModItems.ASCENSION_CORE)) {
            lines.add(Component.literal("Use in an anvil to level up/ascend gear.").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Each level adds a new trait, up to the item's capacity and enhanced existing traits.").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        if (stack.is(ModItems.CHAOS_CORE)) {
            lines.add(Component.literal("Use in an anvil to reroll traits on ascended gear.").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Rerolls lowest-rank traits first (bottom of list).").withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Stack more cores to reroll more traits at once.").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        if (!GearHelper.isGear(stack)) return;

        int level = GearHelper.getLevel(stack);
        int capacity = Math.min(GearHelper.getMaterialCapacity(stack), GearHelper.getMaxLevel());
        List<RolledStat> stats = GearHelper.getRolledStats(stack);
        boolean showNextLevelPreview = !suppressNextLevelPreview();

        if (level > 0 && !lines.isEmpty()) {
            Component original = lines.get(0);
            lines.set(0, Component.literal(levelTitle(level) + " ")
                .withStyle(style -> style.withColor(levelColor(level)))
                .append(original));
        }

        int maxLevel = GearHelper.getMaxLevel();
        StringBuilder pips = new StringBuilder("  Tier: ");
        for (int p = 1; p <= maxLevel; p++) pips.append(p <= level ? "◆" : "◇");
        lines.add(Component.literal(pips.toString())
            .withStyle(style -> style.withColor(levelColor(level)).withBold(true)));

        lines.add(Component.literal("  (" + stats.size() + "/" + capacity + " traits)")
            .withStyle(style -> style.withColor(levelColor(level))));

        for (int i = 0; i < stats.size(); i++) {
            RolledStat rolled = stats.get(i);
            StatPool.StatDef def = StatPool.getById(rolled.id());
            if (def == null) continue;

            int multiplier = level - i;
            if (multiplier <= 0) {
                int awakensAt = i + 1;
                lines.add(Component.literal("  ✦ " + def.displayName() + ": Awakens at L" + awakensAt)
                    .withStyle(style -> style.withColor(0x888888).withItalic(true)));
                continue;
            }

            double current = rolled.amount() * multiplier;
            String rankPips = "  " + "✦".repeat(multiplier) + " ";
            String display = rankPips + def.displayName() + ": " + formatStatValue(def, rolled.id(), current);
            if (showNextLevelPreview && level < GearHelper.getMaxLevel()) {
                double next = rolled.amount() * (multiplier + 1);
                display += " ➔ " + formatStatValue(def, rolled.id(), next);
            }
            lines.add(Component.literal(display).withStyle(style -> style.withColor(statColor(rolled.id()))));
        }

        int enchantSlots = level;
        lines.add(Component.literal("  Enchantment Slots: " + enchantSlots)
            .withStyle(ChatFormatting.DARK_PURPLE));

        if (showNextLevelPreview && level < GearHelper.getMaxLevel()) {
            int coreCost = GearHelper.getAscensionCoreCost(level);
            lines.add(Component.literal("  Cost to Level: " + coreCost + " Ascension Cores")
                .withStyle(ChatFormatting.GRAY));
        }
    }

    private static String formatStatValue(StatPool.StatDef def, String id, double value) {
        if (id.equals("frostbite") || id.equals("venom") || id.equals("shock")
                || id.equals("wither") || id.equals("heal_suppress") || id.equals("pinning")) {
            return String.format("+%.1f%% Chance", value * 100.0);
        } else if (id.equals("life_steal")) {
            return String.format("+%.1f%%", value * 100.0);
        } else if (id.equals("repair_discount")) {
            return String.format("%.1f%% Discount", Math.abs(value) * 100.0);
        } else if (id.equals("low_health_guard") || id.equals("steady_guard") || id.equals("bulwark") || id.equals("melee_resistance")) {
            return String.format("+%.1f%% Damage Reduction", value * 100.0);
        } else if (id.equals("emergency_healing")) {
            return String.format("+%.1f%% Max Health", value * 100.0);
        } else if (id.equals("reach") || id.equals("stealth")) {
            return String.format("+%.2f Blocks", value);
        }
        return StatPool.formatValue(def, value);
    }

    private static boolean suppressNextLevelPreview() {
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof AbstractContainerScreen<?> screen)) return false;
        if (!(screen.getMenu() instanceof AnvilMenu anvilMenu)) return false;

        Slot hoveredSlot = ((AbstractContainerScreenAccessor) screen).ascensioncores$getHoveredSlot();
        if (hoveredSlot == null) return false;

        // suppress on result slot always
        if (hoveredSlot.index == anvilMenu.getResultSlot()) return true;

        // suppress on left input when a chaos core is in right input (reroll, not upgrade)
        if (hoveredSlot.index == 0) {
            ItemStack right = anvilMenu.getSlot(1).getItem();
            return right.is(ModItems.CHAOS_CORE);
        }
        return false;
    }

    private static String levelTitle(int level) {
        return switch (level) {
            case 0 -> "Unawakened";
            case 1 -> "Honed";
            case 2 -> "Empowered";
            case 3 -> "Ascendant";
            case 4 -> "Mythic";
            default -> "Divine";
        };
    }

    private static int statColor(String id) {
        return switch (id) {
            case "life_steal"           -> 0xFF5555;
            case "reach"                -> 0x55AAFF;
            case "attack_speed"         -> 0xFFAA00;
            case "armor_shred"          -> 0xFF7755;
            case "toughness_shred"      -> 0xDD8866;
            case "experience_bonus"     -> 0x55FF55;
            case "critical_damage"      -> 0xFF55FF;
            case "execution_damage"     -> 0xFF3333;
            case "ambush_damage"        -> 0x7777FF;
            case "frostbite"            -> 0x55FFFF;
            case "venom"                -> 0x55FF55;
            case "shock"                -> 0xFFFF55;
            case "sprinting_speed"      -> 0x55FFAA;
            case "stealth"              -> 0x8888AA;
            case "jump"                 -> 0xAAFF55;
            case "repair_discount"      -> 0x66CCAA;
            case "opening_damage"       -> 0xFFD700;
            case "wither"               -> 0x7F00FF;
            case "chain_damage"         -> 0xFF8C00;
            case "heal_suppress"        -> 0xCC2200;
            case "pinning"              -> 0x4488FF;
            case "overcharge_damage"    -> 0xFFEE00;
            case "evasion"              -> 0x88FFDD;
            case "deflection"           -> 0xAAAAFF;
            case "tenacity"             -> 0xFFAA55;
            case "melee_resistance"     -> 0xBBBBBB;
            case "natural_regeneration" -> 0x66FF88;
            case "low_health_guard"     -> 0xFF8844;
            case "steady_guard"         -> 0xC0C0C0;
            case "consuming_speed"      -> 0xFFDD66;
            case "tamed_resistance"     -> 0xDDFFAA;
            case "stamina"              -> 0x66FFCC;
            case "emergency_healing"    -> 0xFF88BB;
            case "bulwark"              -> 0xAAAAAA;
            case "vigor"                -> 0xFF2244;
            default                     -> 0x2BBBCC;
        };
    }

    private static int levelColor(int level) {
        return switch (level) {
            case 0 -> 0xB8B8B8;
            case 1 -> 0xFFFFFF;
            case 2 -> 0x55FFFF;
            case 3 -> 0xFF55FF;
            case 4 -> 0xFFAA00;
            default -> 0xFF5555;
        };
    }
}
