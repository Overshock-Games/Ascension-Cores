package com.ascensioncores.mixin;

import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.TraitState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float ascensioncores$applyCustomArmorStats(float amount, ServerLevel level, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (amount <= 0.0f || entity.getMaxHealth() <= 0.0f) return amount;

        // Evasion (Complete Dodge)
        double evasion = GearHelper.getScaledArmorStatAmount(entity, "evasion");
        if (evasion > 0.0 && Math.random() < evasion) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(), 10, 0.3, 0.3, 0.3, 0.02);
            level.playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_SWEEP, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.5f);
            return 0.0f;
        }

        // Deflection (Projectile Reflection)
        if (source.getDirectEntity() instanceof Projectile projectile) {
            double deflection = GearHelper.getScaledArmorStatAmount(entity, "deflection");
            if (deflection > 0.0 && Math.random() < deflection) {
                projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-0.5));
                level.playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.SHIELD_BLOCK.value(), net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.2f);
                return 0.0f;
            }
        }

        double lastStand = GearHelper.getScaledArmorStatAmount(entity, "low_health_guard");
        if (lastStand > 0.0 && entity.getHealth() / entity.getMaxHealth() <= 0.35f) {
            amount *= (float) (1.0 - Math.min(lastStand, 0.60));
        }

        double steadyGuard = GearHelper.getScaledArmorStatAmount(entity, "steady_guard");
        if (steadyGuard > 0.0 && entity.isShiftKeyDown()) {
            amount *= (float) (1.0 - Math.min(steadyGuard, 0.50));
        }

        double bulwark = GearHelper.getScaledArmorStatAmount(entity, "bulwark");
        if (bulwark > 0.0 && entity.getDeltaMovement().horizontalDistanceSqr() < 0.001) {
            amount *= (float) (1.0 - Math.min(bulwark, 0.50));
        }

        boolean isMelee = source.getDirectEntity() instanceof LivingEntity
            && !(source.getDirectEntity() instanceof Projectile);
        if (isMelee) {
            double meleeReduction = GearHelper.getScaledArmorStatAmount(entity, "melee_resistance");
            if (meleeReduction > 0.0) {
                amount *= (float) (1.0 - Math.min(meleeReduction, 0.50));
            }
        }

        return amount;
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void ascensioncores$checkSecondWind(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getHealth() <= 0) return;
        double secondWind = GearHelper.getScaledArmorStatAmount(entity, "emergency_healing");
        if (secondWind <= 0.0) return;
        if (entity.getHealth() / entity.getMaxHealth() <= 0.30f) {
            if (TraitState.trySecondWind(entity.getUUID(), 30_000L)) {
                entity.heal((float) (secondWind * entity.getMaxHealth()));
                level.sendParticles(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.0, entity.getZ(), 5, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }
}
