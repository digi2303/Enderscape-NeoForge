package net.penumbra.enderscape.mixin.neoforge;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.RandomState;
import net.penumbra.enderscape.neoforge.level.EndBiomeNoiseSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomState.class)
public abstract class RandomStateMixin {

    @Shadow
    @Final
    private long seed;

    @ModifyReturnValue(method = "createClimateSampler", at = @At("RETURN"))
    private Climate.Sampler enderscape$setEndBiomeNoiseSeed(Climate.Sampler sampler) {
        ((EndBiomeNoiseSampler) (Object) sampler).enderscape$setSeed(seed);
        return sampler;
    }
}
