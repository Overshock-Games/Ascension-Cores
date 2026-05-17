package com.ascensioncores.gear;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** A stat that has been rolled onto a piece of gear, with its specific base amount locked in. */
public record RolledStat(String id, double amount) {

    public static final Codec<RolledStat> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(RolledStat::id),
            Codec.DOUBLE.fieldOf("amount").forGetter(RolledStat::amount)
        ).apply(instance, RolledStat::new)
    );
}
