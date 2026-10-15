package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.penumbra.enderscape.Enderscape;

import java.util.concurrent.CompletableFuture;

import static net.penumbra.enderscape.registry.entity.EnderscapePaintingVariants.FALLEN_STAR;
import static net.penumbra.enderscape.registry.entity.EnderscapePaintingVariants.GRAPE_STATIC;

public class EnderscapePaintingVariantTagProvider extends TagsProvider<PaintingVariant> {

    public EnderscapePaintingVariantTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.PAINTING_VARIANT, future, Enderscape.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(PaintingVariantTags.PLACEABLE).add(FALLEN_STAR, GRAPE_STATIC);
    }
}
