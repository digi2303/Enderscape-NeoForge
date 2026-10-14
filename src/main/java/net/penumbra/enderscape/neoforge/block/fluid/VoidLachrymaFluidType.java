package net.penumbra.enderscape.neoforge.block.fluid;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import net.penumbra.enderscape.block.fluid.VoidLachrymaBehavior;
import net.penumbra.enderscape.entity.ai.EnderscapePathTypes;
import net.penumbra.enderscape.registry.tag.EnderscapeFluidTags;

public class VoidLachrymaFluidType extends FluidType {

    public static final VoidLachrymaBehavior BEHAVIOR = new VoidLachrymaBehavior();

    public VoidLachrymaFluidType() {
        super(Properties.create()
                .descriptionId("block.enderscape.void_lachryma")
                .motionScale(0.009)
                .canPushEntity(true)
                .canSwim(false)
                .canDrown(false)
                .canExtinguish(false)
                .canHydrate(false)
                .supportsBoating(false)
                .fallDistanceModifier(0.0F)
                .pathType(EnderscapePathTypes.VOID_LACHRYMA)
                .adjacentPathType(null)
        );
    }

    @Override
    public boolean canSwim(Entity entity) {
        return entity instanceof Mob && BEHAVIOR.shouldTryFloatingInFluid(EnderscapeFluidTags.VOID_LACHRYMA, entity);
    }

    @Override
    public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        if (entity.isInWater()) {
            return false;
        }

        BEHAVIOR.travelInFluid(EnderscapeFluidTags.VOID_LACHRYMA, entity, movementVector, gravity, entity.getDeltaMovement().y <= 0.0, entity.getY());
        return true;
    }
}
