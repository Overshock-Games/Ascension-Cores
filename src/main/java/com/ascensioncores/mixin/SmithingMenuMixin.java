package com.ascensioncores.mixin;

import com.ascensioncores.component.ModComponents;
import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.RolledStat;
import com.ascensioncores.gear.StatPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void previewSmithingResult(CallbackInfo ci) {
        ItemCombinerMenuAccessor acc = (ItemCombinerMenuAccessor)(Object)this;
        Container inputSlots = acc.getInputSlots();
        ResultContainer resultSlots = acc.getResultSlots();

        ItemStack result = resultSlots.getItem(0);
        if (result.isEmpty() || !GearHelper.isGear(result)) return;

        ItemStack base = inputSlots.getItem(1); // BASE_SLOT
        if (!GearHelper.isGear(base)) return;

        smithingcores$applyExtraTraits(result, base);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onSmithingTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.level().isClientSide()) return;
        if (!GearHelper.isGear(stack)) return;

        ItemCombinerMenuAccessor acc = (ItemCombinerMenuAccessor)(Object)this;
        Container inputSlots = acc.getInputSlots();
        ItemStack base = inputSlots.getItem(1); // BASE_SLOT

        if (!GearHelper.isGear(base)) return;

        smithingcores$applyExtraTraits(stack, base);
    }

    @org.spongepowered.asm.mixin.Unique
    private static void smithingcores$applyExtraTraits(ItemStack stack, ItemStack base) {
        int baseCap   = GearHelper.getMaterialCapacity(base);
        int resultCap = GearHelper.getMaterialCapacity(stack);
        if (resultCap <= baseCap) return;

        int level = GearHelper.getLevel(stack);
        if (level == 0) return;

        List<RolledStat> stats = new ArrayList<>(GearHelper.getRolledStats(stack));
        int targetCount = Math.min(level, resultCap);
        if (stats.size() >= targetCount) return;

        Random rng = new Random(GearHelper.getUpgradeSeed(stack));
        List<StatPool.StatDef> pool = GearHelper.getPool(stack);

        while (stats.size() < targetCount) {
            RolledStat rolled = StatPool.rollStat(stats, pool, level, rng);
            if (rolled == null) break;
            stats.add(rolled);
        }

        stack.set(ModComponents.ROLLED_STATS, stats);
        GearHelper.rebuildAttributes(stack, level, stats);
    }
}
