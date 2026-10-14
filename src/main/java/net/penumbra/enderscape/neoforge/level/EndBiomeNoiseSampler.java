package net.penumbra.enderscape.neoforge.level;

import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public interface EndBiomeNoiseSampler {

    void enderscape$setSeed(long seed);

    PerlinNoise enderscape$getEndBiomeNoise();
}
