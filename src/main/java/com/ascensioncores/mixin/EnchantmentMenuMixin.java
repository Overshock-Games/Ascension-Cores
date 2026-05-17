package com.ascensioncores.mixin;

import com.ascensioncores.gear.GearHelper;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow @Final private Container enchantSlots;
    @Shadow public int[] costs;

    /**
     * If a gear item with ascension level 0 is placed in the enchanting table,
     * zero out all costs so the table is effectively disabled for that item.
     * Items at level >= 1 follow normal enchanting but are capped by the anvil mixin.
     */
    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void onSlotsChanged(Container container, CallbackInfo ci) {
        ItemStack item = enchantSlots.getItem(0);
        if (!item.isEmpty() && GearHelper.isGear(item) && GearHelper.getLevel(item) == 0) {
            costs[0] = 0;
            costs[1] = 0;
            costs[2] = 0;
        }
    }
}
