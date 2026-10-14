package net.penumbra.enderscape.neoforge.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record AddFeaturesAlongsideBiomeModifier(
        HolderSet<Biome> biomes,
        HolderSet<PlacedFeature> alongside,
        HolderSet<Biome> unconditionalBiomes,
        HolderSet<PlacedFeature> features,
        GenerationStep.Decoration step
) implements BiomeModifier {

    public static final MapCodec<AddFeaturesAlongsideBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesAlongsideBiomeModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("alongside").forGetter(AddFeaturesAlongsideBiomeModifier::alongside),
            Biome.LIST_CODEC.optionalFieldOf("unconditional_biomes", HolderSet.empty()).forGetter(AddFeaturesAlongsideBiomeModifier::unconditionalBiomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesAlongsideBiomeModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesAlongsideBiomeModifier::step)
    ).apply(instance, AddFeaturesAlongsideBiomeModifier::new));

    @Override
    public void modify(RegistryAccess registries, Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome) && (unconditionalBiomes.contains(biome) || hasAlongsideFeature(biome.value()))) {
            BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
            features.forEach(feature -> generation.addFeature(step, feature));
        }
    }

    private boolean hasAlongsideFeature(Biome biome) {
        return biome.getGenerationSettings().features().stream().anyMatch(step -> step.stream().anyMatch(alongside::contains));
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
