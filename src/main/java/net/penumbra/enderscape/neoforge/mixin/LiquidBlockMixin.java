package net.penumbra.enderscape.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.penumbra.enderscape.block.fluid.VoidLachrymaFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    @Shadow
    @Final
    protected FlowingFluid fluid;

    @ModifyExpressionValue(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/FluidInteractionRegistry;canInteract(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean enderscape$interactWithVoidLachryma(boolean interacted, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (!interacted) {
            VoidLachrymaFluid.shouldSpreadLiquidTail(level, pos, fluid);
        }
        return interacted;
    }
}
