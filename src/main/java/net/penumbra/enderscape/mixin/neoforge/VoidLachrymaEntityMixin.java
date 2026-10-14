package net.penumbra.enderscape.mixin.neoforge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.penumbra.enderscape.neoforge.block.fluid.VoidLachrymaFluidType;
import net.penumbra.enderscape.registry.tag.EnderscapeFluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class VoidLachrymaEntityMixin {

    @Unique
    private boolean enderscape$wasTouchingVoidLachryma;

    @ModifyReturnValue(method = "updateFluidInteraction", at = @At("RETURN"))
    private boolean enderscape$updateVoidLachrymaInteraction(boolean inAnyFluid) {
        Entity entity = (Entity) (Object) this;

        if (entity.getFluidInteraction().isInFluid(EnderscapeFluidTags.VOID_LACHRYMA)) {
            if (!enderscape$wasTouchingVoidLachryma) {
                VoidLachrymaFluidType.BEHAVIOR.onFluidEntered(EnderscapeFluidTags.VOID_LACHRYMA, entity, entity.firstTick);
                enderscape$wasTouchingVoidLachryma = true;
            }

            VoidLachrymaFluidType.BEHAVIOR.handleFluidInteractionUpdate(EnderscapeFluidTags.VOID_LACHRYMA, entity, entity.getFluidInteraction(), entity.isPushedByFluid());
        } else {
            enderscape$wasTouchingVoidLachryma = false;
        }

        return inAnyFluid;
    }

    @ModifyReturnValue(method = "isInLiquid", at = @At("RETURN"))
    private boolean enderscape$isInVoidLachryma(boolean original) {
        return original || enderscape$wasTouchingVoidLachryma;
    }
}
