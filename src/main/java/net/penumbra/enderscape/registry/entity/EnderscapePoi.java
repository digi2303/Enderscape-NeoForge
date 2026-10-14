package net.penumbra.enderscape.registry.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.penumbra.enderscape.Enderscape;

import java.util.Set;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.Blocks.*;
import static net.penumbra.enderscape.registry.block.EnderscapeBlocks.*;

public class EnderscapePoi {

    public static final ResourceKey<PoiType> DRIFTER_HOME = create("drifter_home");
    public static final ResourceKey<PoiType> RUSTLE_SLEEPING_SPOT = create("rustle_sleeping_spot");

    private static ResourceKey<PoiType> create(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Enderscape.id(name));
    }

    private static void register(Identifier id, int ticketCount, int searchDistance, Set<BlockState> states) {
        Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, id, new PoiType(states, ticketCount, searchDistance));
    }

    static {
        register(
                DRIFTER_HOME.identifier(),
                4,
                8,
                ImmutableList.of(
                                RIPE_PURUBERRY_BLOCK,
                                UNRIPE_PURUBERRY_BLOCK,
                                PURUBERRY_FLOWER
                        )
                        .stream()
                        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                        .collect(ImmutableSet.toImmutableSet())
        );



        register(
                RUSTLE_SLEEPING_SPOT.identifier(),
                1,
                8,
                Stream.concat(Stream.of(
                                VEILED_LEAF_PILE,
                                MOSS_CARPET,
                                PALE_MOSS_CARPET
                        ), CARPET.asList().stream())
                        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                        .collect(ImmutableSet.toImmutableSet())
        );
    }
}