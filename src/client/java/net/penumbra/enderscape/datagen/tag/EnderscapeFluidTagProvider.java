package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.material.Fluid;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.references.EnderscapeFluidIds;

import java.util.concurrent.CompletableFuture;

import static net.penumbra.enderscape.registry.tag.EnderscapeFluidTags.VOID_LACHRYMA;

public class EnderscapeFluidTagProvider extends TagsProvider<Fluid> {

    public EnderscapeFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.FLUID, registriesFuture, Enderscape.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(VOID_LACHRYMA).add(EnderscapeFluidIds.VOID_LACHRYMA, EnderscapeFluidIds.FLOWING_VOID_LACHRYMA);
    }
}
