package com.ascensioncores.event;

import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.RolledStat;
import com.ascensioncores.gear.StatPool;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

        if (!GearHelper.hasAscensionData(stack)) return;

        int level = GearHelper.getLevel(stack);
        List<RolledStat> stats = GearHelper.getRolledStats(stack);

        lines.add(Component.literal("⚔ Ascension Tier: " + level)
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        for (int i = 0; i < stats.size(); i++) {
            RolledStat rolled = stats.get(i);
            StatPool.StatDef def = StatPool.getById(rolled.id());
            if (def == null) continue;

            int multiplier = level - i;
            double current = rolled.amount() * multiplier;
            String display = "  ✦ " + def.displayName() + ": " + StatPool.formatValue(def, current);
            if (level < 4) {
                double next = rolled.amount() * (multiplier + 1);
                display += " ➔ " + StatPool.formatValue(def, next);
            }
            lines.add(Component.literal(display).withStyle(ChatFormatting.AQUA));
        }

        int enchantSlots = Math.max(0, level - 1);
        lines.add(Component.literal("  Enchantment Slots: " + enchantSlots)
            .withStyle(ChatFormatting.DARK_PURPLE));

        if (level < 4) {
            int coreCost = (int) Math.pow(4, level);
            lines.add(Component.literal("  Cost to Upgrade: " + coreCost + " Upgrade Cores")
                .withStyle(ChatFormatting.GRAY));
        }
    }
}
