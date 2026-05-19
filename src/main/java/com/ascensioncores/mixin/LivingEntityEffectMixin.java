package com.ascensioncores.mixin;

import com.ascensioncores.gear.GearHelper;
import com.ascensioncores.gear.TraitState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEffectMixin {

    @ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), argsOnly = true)
    private MobEffectInstance ascensioncores$reduceNegativeEffects(MobEffectInstance effect) {
        if (effect == null || effect.getEffect() == null) return effect;
        
        LivingEntity entity = (LivingEntity) (Object) this;
        double tenacity = GearHelper.getScaledArmorStatAmount(entity, "tenacity");
        
        if (tenacity > 0.0 && effect.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL) {
            int newDuration = (int) (effect.getDuration() * Math.max(0.1, 1.0 - tenacity));
            return new MobEffectInstance(
                effect.getEffect(), newDuration, effect.getAmplifier(), 
                effect.isAmbient(), effect.isVisible(), effect.showIcon()
            );
        }
        return effect;
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float ascensioncores$applyHealSuppress(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (TraitState.hasHealSuppress(entity.getUUID())) {
            return amount * 0.5f;
        }
        return amount;
    }
}