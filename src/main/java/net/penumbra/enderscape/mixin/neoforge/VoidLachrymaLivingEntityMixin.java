package net.penumbra.enderscape.mixin.neoforge;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.penumbra.enderscape.neoforge.block.fluid.VoidLachrymaFluidType;
import net.penumbra.enderscape.registry.tag.EnderscapeFluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class VoidLachrymaLivingEntityMixin {

    @Inject(method = "travelFlying(Lnet/minecraft/world/phys/Vec3;FFF)V", at = @At("HEAD"), cancellable = true)
    private void enderscape$travelFlyingInVoidLachryma(Vec3 input, float waterSpeed, float lavaSpeed, float airSpeed, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.getFluidInteraction().isInFluid(EnderscapeFluidTags.VOID_LACHRYMA)) {
            VoidLachrymaFluidType.BEHAVIOR.travelFlyingInFluid(EnderscapeFluidTags.VOID_LACHRYMA, entity, input, waterSpeed, lavaSpeed, airSpeed);
            info.cancel();
        }
    }

    @ModifyExpressionValue(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
    private boolean enderscape$skipRepeatedVoidLachrymaUpdate(boolean original) {
        return original || ((LivingEntity) (Object) this).getFluidInteraction().isInFluid(EnderscapeFluidTags.VOID_LACHRYMA);
    }
}
