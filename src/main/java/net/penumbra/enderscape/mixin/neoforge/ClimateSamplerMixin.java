package net.penumbra.enderscape.mixin.neoforge;

import com.google.common.base.Preconditions;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.penumbra.enderscape.neoforge.level.EndBiomeNoiseSampler;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Climate.Sampler.class)
public abstract class ClimateSamplerMixin implements EndBiomeNoiseSampler {

    @Unique
    private @Nullable Long enderscape$seed;

    @Unique
    private @Nullable PerlinNoise enderscape$endBiomeNoise;

    @Override
    public void enderscape$setSeed(long seed) {
        enderscape$seed = seed;
    }

    @Override
    public PerlinNoise enderscape$getEndBiomeNoise() {
        if (enderscape$endBiomeNoise == null) {
            Preconditions.checkState(enderscape$seed != null, "Climate sampler has no seed, it was not created by RandomState");
            enderscape$endBiomeNoise = new PerlinNoise(new WorldgenRandom(new LegacyRandomSource(enderscape$seed)));
        }

        return enderscape$endBiomeNoise;
    }
}
