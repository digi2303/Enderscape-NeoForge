package net.penumbra.enderscape.neoforge.registry;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.EndPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.AndHolderSet;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.level.AddFeaturesAlongsideBiomeModifier;
import net.penumbra.enderscape.neoforge.level.AddSpawnCostsBiomeModifier;
import net.penumbra.enderscape.neoforge.level.DefaultEndAmbienceBiomeModifier;
import net.penumbra.enderscape.registry.entity.EnderscapeEntities;
import net.penumbra.enderscape.registry.level.EnderscapeBiomes;
import net.penumbra.enderscape.registry.level.EnderscapePlacedFeatures;
import net.penumbra.enderscape.registry.tag.EnderscapeBiomeTags;

public class EnderscapeBiomeModifiers {

    public static final ResourceKey<BiomeModifier> UPDATE_END_BIOME_GRASS_COLOR_WHEN_DEFAULT = key("update_end_biome_grass_color_when_default");
    public static final ResourceKey<BiomeModifier> UPDATE_END_BIOME_FOLIAGE_COLOR_WHEN_DEFAULT = key("update_end_biome_foliage_color_when_default");
    public static final ResourceKey<BiomeModifier> UPDATE_END_BIOME_WATER_COLOR_WHEN_DEFAULT = key("update_end_biome_water_color_when_default");
    public static final ResourceKey<BiomeModifier> UPDATE_END_BIOME_WATER_FOG_COLOR_WHEN_DEFAULT = key("update_end_biome_water_fog_color_when_default");

    public static final ResourceKey<BiomeModifier> ADD_GLOBAL_FEATURES = key("add_global_features");
    public static final ResourceKey<BiomeModifier> ADD_BARRENS_ENTITIES = key("add_barrens_entities");
    public static final ResourceKey<BiomeModifier> ADD_BARRENS_ENTITY_SPAWN_COSTS = key("add_barrens_entity_spawn_costs");
    public static final ResourceKey<BiomeModifier> ADD_ENDERSCAPE_CHORUS_PLANTS = key("add_enderscape_chorus_plants");
    public static final ResourceKey<BiomeModifier> REMOVE_MINECRAFT_CHORUS_PLANTS = key("remove_minecraft_chorus_plants");
    public static final ResourceKey<BiomeModifier> ADD_ENDERSCAPE_ISLANDS = key("add_enderscape_islands");
    public static final ResourceKey<BiomeModifier> REMOVE_SMALL_END_ISLANDS = key("remove_small_end_islands");
    public static final ResourceKey<BiomeModifier> REMOVE_MINECRAFT_END_GATEWAYS = key("remove_minecraft_end_gateways");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> isEnd = biomes.getOrThrow(BiomeTags.IS_END);
        HolderSet<Biome> overridesDefaultAmbience = biomes.getOrThrow(EnderscapeBiomeTags.OVERRIDES_DEFAULT_AMBIENCE);
        HolderSet<Biome> hasBarrensAdditions = new AndHolderSet<>(isEnd, biomes.getOrThrow(EnderscapeBiomeTags.HAS_BARRENS_ADDITIONS));

        context.register(UPDATE_END_BIOME_GRASS_COLOR_WHEN_DEFAULT, new DefaultEndAmbienceBiomeModifier(isEnd, overridesDefaultAmbience, DefaultEndAmbienceBiomeModifier.Property.GRASS_COLOR));
        context.register(UPDATE_END_BIOME_FOLIAGE_COLOR_WHEN_DEFAULT, new DefaultEndAmbienceBiomeModifier(isEnd, overridesDefaultAmbience, DefaultEndAmbienceBiomeModifier.Property.FOLIAGE_COLOR));
        context.register(UPDATE_END_BIOME_WATER_COLOR_WHEN_DEFAULT, new DefaultEndAmbienceBiomeModifier(isEnd, overridesDefaultAmbience, DefaultEndAmbienceBiomeModifier.Property.WATER_COLOR));
        context.register(UPDATE_END_BIOME_WATER_FOG_COLOR_WHEN_DEFAULT, new DefaultEndAmbienceBiomeModifier(isEnd, overridesDefaultAmbience, DefaultEndAmbienceBiomeModifier.Property.WATER_FOG_COLOR));

        context.register(ADD_GLOBAL_FEATURES, new BiomeModifiers.AddFeaturesBiomeModifier(
                new AndHolderSet<>(isEnd, new NotHolderSet<>((HolderLookup.RegistryLookup<Biome>) biomes, biomes.getOrThrow(EnderscapeBiomeTags.EXCLUDED_FROM_GLOBAL_FEATURE_ADDITIONS))),
                HolderSet.direct(
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.VOID_SHALE),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.VOID_SHALE_BLOBS),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.VERADITE),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.MIRESTONE_BLOBS),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.SHADOLINE_ORE),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.SCATTERED_SHADOLINE_ORE),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.NEBULITE_ORE),
                        placedFeatures.getOrThrow(EnderscapePlacedFeatures.CEILING_NEBULITE_ORE)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(ADD_BARRENS_ENTITIES, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                hasBarrensAdditions,
                new Weighted<>(new MobSpawnSettings.SpawnerData(EnderscapeEntities.RUBBLEMITE, UniformInt.of(2, 3)), EnderscapeBiomes.RUBBLEMITE_DEFAULT_WEIGHT)
        ));

        context.register(ADD_BARRENS_ENTITY_SPAWN_COSTS, new AddSpawnCostsBiomeModifier(
                hasBarrensAdditions,
                HolderSet.direct(EnderscapeEntities.RUBBLEMITE.builtInRegistryHolder()),
                new MobSpawnSettings.MobSpawnCost(0.6, 0.2)
        ));

        context.register(ADD_ENDERSCAPE_CHORUS_PLANTS, new AddFeaturesAlongsideBiomeModifier(
                isEnd,
                HolderSet.direct(placedFeatures.getOrThrow(EndPlacements.CHORUS_PLANT)),
                HolderSet.direct(biomes.getOrThrow(Biomes.END_MIDLANDS)),
                HolderSet.direct(placedFeatures.getOrThrow(EnderscapePlacedFeatures.CHORUS_PLANTS)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(REMOVE_MINECRAFT_CHORUS_PLANTS, BiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                isEnd,
                HolderSet.direct(placedFeatures.getOrThrow(EndPlacements.CHORUS_PLANT))
        ));

        context.register(ADD_ENDERSCAPE_ISLANDS, new AddFeaturesAlongsideBiomeModifier(
                isEnd,
                HolderSet.direct(placedFeatures.getOrThrow(EndPlacements.END_ISLAND_DECORATED)),
                HolderSet.empty(),
                HolderSet.direct(placedFeatures.getOrThrow(EnderscapePlacedFeatures.SMALL_ISLANDS)),
                GenerationStep.Decoration.RAW_GENERATION
        ));

        context.register(REMOVE_SMALL_END_ISLANDS, BiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                isEnd,
                HolderSet.direct(placedFeatures.getOrThrow(EndPlacements.END_ISLAND_DECORATED))
        ));

        context.register(REMOVE_MINECRAFT_END_GATEWAYS, BiomeModifiers.RemoveFeaturesBiomeModifier.allSteps(
                isEnd,
                HolderSet.direct(placedFeatures.getOrThrow(EndPlacements.END_GATEWAY_RETURN))
        ));
    }

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Enderscape.id(name));
    }
}
