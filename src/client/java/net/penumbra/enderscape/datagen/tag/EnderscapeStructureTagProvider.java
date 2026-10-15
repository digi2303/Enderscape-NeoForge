package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.registry.structure.EnderscapeStructures;

import java.util.concurrent.CompletableFuture;

public class EnderscapeStructureTagProvider extends TagsProvider<Structure> {

    public EnderscapeStructureTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.STRUCTURE, future, Enderscape.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(StructureTags.EYE_OF_ENDER_LOCATED).add(EnderscapeStructures.STRONGHOLD);
    }
}
