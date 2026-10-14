package net.penumbra.enderscape.neoforge.entity;

import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public final class EnderscapeLivingDamage {

    private EnderscapeLivingDamage() {
    }

    public static float damageTaken(LivingDamageEvent.Post event) {
        float damage = event.getHealthDamage();

        for (DamageContainer.Reduction reduction : DamageContainer.Reduction.values()) {
            damage += event.getReduction(reduction);
        }

        return damage;
    }
}
