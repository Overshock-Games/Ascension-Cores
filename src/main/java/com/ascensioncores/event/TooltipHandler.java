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
            lines.add(Component.literal("Levels gear in an anvil.").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Cost scales with the item's current level.").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        if (stack.is(ModItems.CHAOS_CORE)) {
            lines.add(Component.literal("Rerolls an upgraded item's stats in an anvil.").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("The preview is fixed until the reroll is consumed.").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        if (!GearHelper.isGear(stack)) return;

        int level = GearHelper.getLevel(stack);
        int capacity = Math.min(GearHelper.getMaterialCapacity(stack), GearHelper.getMaxLevel());
        List<RolledStat> stats = GearHelper.getRolledStats(stack);
        boolean showNextLevelPreview = !isAnvilResultTooltip();

        lines.add(Component.literal("Level: " + level + " - " + levelTitle(level) + "  (" + stats.size() + "/" + capacity + " stats)")
            .withStyle(style -> style.withColor(levelColor(level)).withBold(true)));

        for (int i = 0; i < stats.size(); i++) {
            RolledStat rolled = stats.get(i);
            StatPool.StatDef def = StatPool.getById(rolled.id());
            if (def == null) continue;

            int multiplier = level - i;
            double current = rolled.amount() * multiplier;
            String display = "  ✦ " + def.displayName() + ": " + formatStatValue(def, rolled.id(), current);
            if (showNextLevelPreview && level < GearHelper.getMaxLevel()) {
                double next = rolled.amount() * (multiplier + 1);
                display += " ➔ " + formatStatValue(def, rolled.id(), next);
            }
            lines.add(Component.literal(display).withStyle(ChatFormatting.DARK_AQUA));
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
        if (id.equals("frostbite") || id.equals("venom") || id.equals("shock")) {
            return String.format("+%.1f%% Chance", value * 100.0);
        } else if (id.equals("life_steal")) {
            return String.format("+%.1f%%", value * 100.0);
        } else if (id.equals("reach") || id.equals("stealth")) {
            return String.format("+%.2f Blocks", value);
        }
        return StatPool.formatValue(def, value);
    }

    private static boolean isAnvilResultTooltip() {
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof AbstractContainerScreen<?> screen)) return false;
        if (!(screen.getMenu() instanceof AnvilMenu anvilMenu)) return false;

        Slot hoveredSlot = ((AbstractContainerScreenAccessor) screen).ascensioncores$getHoveredSlot();
        return hoveredSlot != null && hoveredSlot.index == anvilMenu.getResultSlot();
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
