package com.ascensioncores.event;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class TraitFx {

    private TraitFx() {}

    public static void critical(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.CRIT, 12, 0.4, 0.4, 0.4, 0.4);
        play(level, target, SoundEvents.PLAYER_ATTACK_CRIT, 0.6f, 1.4f);
    }

    public static void execution(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        DustParticleOptions red = new DustParticleOptions(0xFF1A1A, 1.5f);
        spawn(sl, target, red, 18, 0.5, 0.5, 0.5, 0.05);
        play(level, target, SoundEvents.WITHER_HURT, 0.4f, 1.6f);
    }

    public static void opening(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.END_ROD, 10, 0.4, 0.4, 0.4, 0.05);
        play(level, target, SoundEvents.PLAYER_LEVELUP, 0.3f, 1.8f);
    }

    public static void chainDamage(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        DustParticleOptions orange = new DustParticleOptions(0xFF8C00, 1.0f);
        spawn(sl, target, orange, 8, 0.3, 0.4, 0.3, 0.02);
        play(level, target, SoundEvents.FIRE_AMBIENT, 0.3f, 1.6f);
    }

    public static void ambush(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.SMOKE, 14, 0.4, 0.4, 0.4, 0.02);
        spawn(sl, target, ParticleTypes.ENCHANTED_HIT, 8, 0.3, 0.3, 0.3, 0.2);
        play(level, target, SoundEvents.PHANTOM_BITE, 0.5f, 1.6f);
    }

    public static void frostbite(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.SNOWFLAKE, 14, 0.4, 0.5, 0.4, 0.03);
        play(level, target, SoundEvents.GLASS_BREAK, 0.3f, 1.8f);
    }

    public static void venom(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.SCULK_SOUL, 6, 0.3, 0.4, 0.3, 0.02);
        spawn(sl, target, ParticleTypes.SPIT, 6, 0.3, 0.4, 0.3, 0.05);
        play(level, target, SoundEvents.SPIDER_HURT, 0.4f, 1.3f);
    }

    public static void shock(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.ELECTRIC_SPARK, 16, 0.4, 0.5, 0.4, 0.4);
        play(level, target, SoundEvents.LIGHTNING_BOLT_IMPACT, 0.25f, 1.8f);
    }

    public static void wither(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        spawn(sl, target, ParticleTypes.SQUID_INK, 10, 0.4, 0.5, 0.4, 0.02);
        spawn(sl, target, ParticleTypes.SMOKE, 8, 0.3, 0.4, 0.3, 0.02);
        play(level, target, SoundEvents.WITHER_SHOOT, 0.25f, 1.6f);
    }

    public static void healSuppress(Level level, Entity target) {
        if (!(level instanceof ServerLevel sl)) return;
        DustParticleOptions dark = new DustParticleOptions(0x8C0000, 1.3f);
        spawn(sl, target, dark, 12, 0.35, 0.45, 0.35, 0.02);
        spawn(sl, target, ParticleTypes.DAMAGE_INDICATOR, 4, 0.3, 0.4, 0.3, 0.05);
        play(level, target, SoundEvents.AXE_SCRAPE, 0.5f, 0.8f);
    }

    private static void spawn(ServerLevel sl, Entity target, ParticleOptions opt,
                              int count, double dx, double dy, double dz, double speed) {
        sl.sendParticles(opt,
            target.getX(),
            target.getY() + target.getBbHeight() * 0.55,
            target.getZ(),
            count, dx, dy, dz, speed);
    }

    private static void play(Level level, Entity target, SoundEvent sound, float vol, float pitch) {
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
            sound, SoundSource.PLAYERS, vol, pitch);
    }
}
