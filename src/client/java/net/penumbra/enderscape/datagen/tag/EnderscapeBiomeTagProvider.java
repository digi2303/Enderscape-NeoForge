package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.penumbra.enderscape.Enderscape;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BiomeTags.HAS_END_CITY;
import static net.minecraft.tags.BiomeTags.IS_END;
import static net.minecraft.world.level.biome.Biomes.*;
import static net.penumbra.enderscape.registry.level.EnderscapeBiomes.*;
import static net.penumbra.enderscape.registry.tag.EnderscapeBiomeTags.*;

public class EnderscapeBiomeTagProvider extends TagsProvider<Biome> {

    public EnderscapeBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.BIOME, future, Enderscape.MOD_ID);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider lookup) {
        tag(ENDERSCAPE_BIOMES).addAll(BIOMES);

        tag(DEEP_VOID_BIOMES).add(VOID_DEPTHS);
        tag(SKY_VOID_BIOMES).add(VOID_SKIES, VOID_SKY_ISLANDS);
        tag(VOID_BIOMES).addTag(SKY_VOID_BIOMES).addTag(DEEP_VOID_BIOMES);

        tag(EXCLUDED_FROM_GLOBAL_FEATURE_ADDITIONS).add(THE_END).addTag(ENDERSCAPE_BIOMES);
        tag(HAS_BARRENS_ADDITIONS).add(END_HIGHLANDS, END_MIDLANDS);
        tag(OVERRIDES_DEFAULT_AMBIENCE).add(THE_END, END_HIGHLANDS, END_MIDLANDS, END_BARRENS, SMALL_END_ISLANDS);

        tag(HAS_END_CITY).add(VEILED_WOODLANDS, MAGNIA_FIELDS, CELESTIAL_GROVE);
        tag(HAS_GATEWAYS).addTag(HAS_END_CITY).add(CORRUPT_BARRENS);
        tag(HAS_MIRESTONE_RUINS).add(CORRUPT_BARRENS);

        tag(IS_END).addTag(ENDERSCAPE_BIOMES);
        tag(Tags.Biomes.IS_END).addTag(ENDERSCAPE_BIOMES);
    }
}