package com.ascensioncores.mixin;

import com.ascensioncores.event.TraitFx;
import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.TraitState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerAttackMixin {

    @Unique
    private boolean ascensioncores$criticalAttack;

    @Shadow
    private boolean canCriticalAttack(Entity target) {
        throw new AssertionError();
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void ascensioncores$resetCriticalDamageState(Entity target, CallbackInfo ci) {
        ascensioncores$criticalAttack = false;
    }

    @Redirect(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;canCriticalAttack(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private boolean ascensioncores$captureCriticalAttack(Player player, Entity target) {
        ascensioncores$criticalAttack = canCriticalAttack(target);
        return ascensioncores$criticalAttack;
    }

    @Redirect(
        method = "attack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
        )
    )
    private boolean ascensioncores$applyCriticalDamage(Entity target, DamageSource source, float amount) {
        Player player = (Player) (Object) this;
        ItemStack weapon = player.getWeaponItem();

        if (ascensioncores$criticalAttack) {
            double bonus = GearHelper.getScaledStatAmount(weapon, "critical_damage");
            if (bonus > 0.0) {
                amount *= (float) (1.0 + bonus);
                TraitFx.critical(player.level(), target);
            }
        }

        if (target instanceof LivingEntity livingTarget) {
            double executeBonus = GearHelper.getScaledStatAmount(weapon, "execution_damage");
            if (executeBonus > 0.0 && livingTarget.getHealth() / livingTarget.getMaxHealth() <= 0.35f) {
                amount *= (float) (1.0 + executeBonus);
                TraitFx.execution(player.level(), target);
            }

            double openingBonus = GearHelper.getScaledStatAmount(weapon, "opening_damage");
            if (openingBonus > 0.0 && livingTarget.getHealth() >= livingTarget.getMaxHealth() * 0.99f) {
                amount *= (float) (1.0 + openingBonus);
                TraitFx.opening(player.level(), target);
            }

            int chainHits = TraitState.getChainHits(player.getUUID(), livingTarget.getUUID());
            TraitState.recordHit(player.getUUID(), livingTarget.getUUID());
            double chainBonus = GearHelper.getScaledStatAmount(weapon, "chain_damage");
            if (chainBonus > 0.0 && chainHits > 0) {
                amount *= (float) (1.0 + chainBonus * Math.min(chainHits, 5));
                TraitFx.chainDamage(player.level(), target);
            }

            double frostbite = GearHelper.getScaledStatAmount(weapon, "frostbite")
                + GearHelper.getScaledEquippedArtifactStatAmount(player, "frostbite");
            if (frostbite > 0.0 && Math.random() < frostbite) {
                livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, 60, 1));
                TraitFx.frostbite(player.level(), target);
            }

            double venom = GearHelper.getScaledStatAmount(weapon, "venom")
                + GearHelper.getScaledEquippedArtifactStatAmount(player, "venom");
            if (venom > 0.0 && Math.random() < venom) {
                livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 100, 1));
                TraitFx.venom(player.level(), target);
            }

            double shock = GearHelper.getScaledStatAmount(weapon, "shock")
                + GearHelper.getScaledEquippedArtifactStatAmount(player, "shock");
            if (shock > 0.0 && Math.random() < shock) {
                livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 60, 0));
                TraitFx.shock(player.level(), target);
            }

            double wither = GearHelper.getScaledStatAmount(weapon, "wither")
                + GearHelper.getScaledEquippedArtifactStatAmount(player, "wither");
            if (wither > 0.0 && Math.random() < wither) {
                livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER, 80, 0));
                TraitFx.wither(player.level(), target);
            }

            double healSuppress = GearHelper.getScaledStatAmount(weapon, "heal_suppress")
                + GearHelper.getScaledEquippedArtifactStatAmount(player, "heal_suppress");
            if (healSuppress > 0.0 && Math.random() < healSuppress) {
                TraitState.applyHealSuppress(livingTarget.getUUID(), 4000L);
                TraitFx.healSuppress(player.level(), target);
            }
        }

        double ambushBonus = GearHelper.getScaledStatAmount(weapon, "ambush_damage");
        if (ambushBonus > 0.0 && ascensioncores$isBehindTarget(player, target)) {
            amount *= (float) (1.0 + ambushBonus);
            TraitFx.ambush(player.level(), target);
        }

        return target.hurtOrSimulate(source, amount);
    }

    @Unique
    private static boolean ascensioncores$isBehindTarget(Player player, Entity target) {
        Vec3 toPlayer = player.position().subtract(target.position());
        if (toPlayer.lengthSqr() < 1.0E-5) return false;
        return toPlayer.normalize().dot(target.getLookAngle().normalize()) < -0.55;
    }
}
