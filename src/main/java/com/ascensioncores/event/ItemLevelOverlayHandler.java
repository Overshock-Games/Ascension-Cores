package com.ascensioncores.event;

import com.ascensioncores.AscensionCoresConfig;
import com.ascensioncores.gear.GearHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

public final class ItemLevelOverlayHandler {

    private static final int ICON_SIZE = 16;
    private static final int CORNER_LENGTH = 4;

    public static void register() {
        ExtractItemDecorationsCallback.EVENT.register(ItemLevelOverlayHandler::appendLevelBadge);
    }

    private static void appendLevelBadge(
            GuiGraphicsExtractor graphics,
            Font font,
            ItemStack stack,
            int x,
            int y) {
        if (stack.isEmpty() || !GearHelper.isGear(stack)) return;
        if (!AscensionCoresConfig.showInventoryLevelMarkers) return;

        int level = GearHelper.getLevel(stack);
        if (level <= 0) return;
        drawCorner(graphics, x, y, Corner.TOP_LEFT, levelColor(level));
    }

    private static int levelColor(int level) {
        return switch (level) {
            case 0 -> 0x88B8B8B8;
            case 1 -> 0xE6FFFFFF;
            case 2 -> 0xE655FFFF;
            case 3 -> 0xE6FF55FF;
            case 4 -> 0xE6FFAA00;
            default -> 0xE6FF5555;
        };
    }

    private static void drawCorner(GuiGraphicsExtractor graphics, int x, int y, Corner corner, int color) {
        int right = x + ICON_SIZE;
        int bottom = y + ICON_SIZE;

        switch (corner) {
            case TOP_LEFT -> {
                graphics.fill(x, y, x + CORNER_LENGTH, y + 1, color);
                graphics.fill(x, y, x + 1, y + CORNER_LENGTH, color);
            }
            case TOP_RIGHT -> {
                graphics.fill(right - CORNER_LENGTH, y, right, y + 1, color);
                graphics.fill(right - 1, y, right, y + CORNER_LENGTH, color);
            }
            case BOTTOM_LEFT -> {
                graphics.fill(x, bottom - 1, x + CORNER_LENGTH, bottom, color);
                graphics.fill(x, bottom - CORNER_LENGTH, x + 1, bottom, color);
            }
            case BOTTOM_RIGHT -> {
                graphics.fill(right - CORNER_LENGTH, bottom - 1, right, bottom, color);
                graphics.fill(right - 1, bottom - CORNER_LENGTH, right, bottom, color);
            }
        }
    }

    private enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}
