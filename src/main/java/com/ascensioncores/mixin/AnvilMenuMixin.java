package com.ascensioncores.mixin;

import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    private static final Logger AC_LOG = LoggerFactory.getLogger("ascensioncores/anvil");

    @Shadow private int repairItemCountCost;
    @Shadow @Final private DataSlot cost;

    @Inject(method = "createResult", at = @At("TAIL"))
    private void interceptCreateResult(CallbackInfo ci) {
        ItemCombinerMenuAccessor acc = (ItemCombinerMenuAccessor) (Object) this;
        Container inputSlots = acc.getInputSlots();
        ResultContainer resultSlots = acc.getResultSlots();

        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        Slot s0 = self.slots.get(0);
        Slot s1 = self.slots.get(1);
        AC_LOG.info("  via slots: s0={} (idx {}, container={}), s1={} (idx {}, container={})",
            s0.getItem().getItem(), s0.getContainerSlot(), s0.container.getClass().getSimpleName(),
            s1.getItem().getItem(), s1.getContainerSlot(), s1.container.getClass().getSimpleName());
        AC_LOG.info("  inputSlots size={}, class={}", inputSlots.getContainerSize(), inputSlots.getClass().getSimpleName());

        ItemStack left  = s0.getItem();
        ItemStack right = s1.getItem();

        AC_LOG.info("createResult: left={} right={} isGear={} isUpgradeCore={} isChaosCore={}",
            left.getItem(), right.getItem(),
            !left.isEmpty() && GearHelper.isGear(left),
            !right.isEmpty() && right.is(ModItems.UPGRADE_CORE),
            !right.isEmpty() && right.is(ModItems.CHAOS_CORE));

        if (left.isEmpty() || right.isEmpty()) return;
        if (!GearHelper.isGear(left)) {
            AC_LOG.info("  -> left not gear (weapon={}, armor={}, tool={})",
                GearHelper.isWeapon(left), GearHelper.isArmor(left), GearHelper.isTool(left));
            return;
        }

        int currentLevel = GearHelper.getLevel(left);
        AC_LOG.info("  -> matched gear, currentLevel={}", currentLevel);

        // ── A: Upgrade with cores ──────────────────────────────────────────
        if (right.is(ModItems.UPGRADE_CORE)) {
            if (currentLevel >= 4) {
                AC_LOG.info("  -> max level, no result");
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }
            int coreCost = (int) Math.pow(4, currentLevel);
            if (right.getCount() < coreCost) {
                AC_LOG.info("  -> not enough cores: need {} have {}", coreCost, right.getCount());
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }
            ItemStack result = left.copy();
            GearHelper.levelUp(result);
            resultSlots.setItem(0, result);
            repairItemCountCost = coreCost;
            cost.set(Math.max(1, currentLevel));
            AC_LOG.info("  -> SET RESULT, coreCost={}, xpCost={}", coreCost, Math.max(1, currentLevel));
            return;
        }

        // ── B: Reroll with chaos core ──────────────────────────────────────
        if (right.is(ModItems.CHAOS_CORE)) {
            if (currentLevel == 0) {
                resultSlots.setItem(0, ItemStack.EMPTY);
                return;
            }
            ItemStack result = left.copy();
            GearHelper.reroll(result);
            resultSlots.setItem(0, result);
            repairItemCountCost = 1;
            cost.set(currentLevel);
            return;
        }

        // ── C: Enchanted book — enforce enchantment capacity ──────────────
        ItemStack currentResult = resultSlots.getItem(0);
        if (currentResult.isEmpty()) return;
        if (!GearHelper.hasAscensionData(left)) return;

        int maxEnchants = Math.max(0, currentLevel - 1);
        ItemEnchantments enchants = currentResult.getOrDefault(
            DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchants.size() > maxEnchants) {
            resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }
}
