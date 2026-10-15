package net.penumbra.enderscape.neoforge.client;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Transformable;

import java.util.concurrent.CompletableFuture;

import static net.penumbra.enderscape.registry.block.EnderscapeBlocks.*;

public class EnderscapeDataMapProvider extends DataMapProvider {

    public EnderscapeDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        Builder<Transformable, Block> transformables = builder(NeoForgeDataMaps.TRANSFORMABLES);

        addStripping(transformables, VEILED_LOG, STRIPPED_VEILED_LOG);
        addStripping(transformables, VEILED_WOOD, STRIPPED_VEILED_WOOD);
        addStripping(transformables, CELESTIAL_STEM, STRIPPED_CELESTIAL_STEM);
        addStripping(transformables, CELESTIAL_HYPHAE, STRIPPED_CELESTIAL_HYPHAE);
        addStripping(transformables, MURUBLIGHT_STEM, STRIPPED_MURUBLIGHT_STEM);
        addStripping(transformables, MURUBLIGHT_HYPHAE, STRIPPED_MURUBLIGHT_HYPHAE);
    }

    private static void addStripping(Builder<Transformable, Block> builder, Block original, Block stripped) {
        builder.add(original.builtInRegistryHolder(), Transformable.stripping(original, stripped), false);
    }
}
