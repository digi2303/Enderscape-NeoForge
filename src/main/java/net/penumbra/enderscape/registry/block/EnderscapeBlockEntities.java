package net.penumbra.enderscape.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.penumbra.enderscape.block.EndHavenCoreBlockEntity;
import net.penumbra.enderscape.block.MagniaRadioBlockEntity;
import net.penumbra.enderscape.block.MagniaSproutBlockEntity;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.references.EnderscapeBlockEntityIds;

public class EnderscapeBlockEntities {
    public static final BlockEntityType<MagniaSproutBlockEntity> MAGNIA_SPROUT = register(EnderscapeBlockEntityIds.MAGNIA_SPROUT, new BlockEntityType<>(MagniaSproutBlockEntity::new, EnderscapeBlocks.ALLURING_MAGNIA_SPROUT, EnderscapeBlocks.REPULSIVE_MAGNIA_SPROUT));
    public static final BlockEntityType<MagniaRadioBlockEntity> MAGNIA_RADIO = register(EnderscapeBlockEntityIds.MAGNIA_RADIO, new BlockEntityType<>(MagniaRadioBlockEntity::new, EnderscapeBlocks.MAGNIA_RADIO));
    public static final BlockEntityType<EndHavenCoreBlockEntity> END_HAVEN_CORE = register(EnderscapeBlockEntityIds.END_HAVEN_CORE, new BlockEntityType<>(EndHavenCoreBlockEntity::new, EnderscapeBlocks.END_HAVEN_CORE));

    public static <T extends BlockEntity, B extends BlockEntityType<T>> B register(ResourceKey<BlockEntityType<?>> key, B type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, type);
    }

    static {
        EnderscapeNeoForge.modEventBus().addListener(BlockEntityTypeAddBlocksEvent.class, event -> {
            event.modify(BlockEntityTypes.CAMPFIRE, EnderscapeBlocks.VOID_CAMPFIRE);
            event.modify(BlockEntityTypes.TRIAL_SPAWNER, EnderscapeBlocks.END_TRIAL_SPAWNER);
            event.modify(BlockEntityTypes.VAULT, EnderscapeBlocks.END_VAULT);

            // Shelves
            event.modify(BlockEntityTypes.SHELF, EnderscapeBlocks.CELESTIAL_SHELF);
            event.modify(BlockEntityTypes.SHELF, EnderscapeBlocks.MURUBLIGHT_SHELF);
            event.modify(BlockEntityTypes.SHELF, EnderscapeBlocks.VEILED_SHELF);

            // Signs
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.CELESTIAL_SIGN);
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.CELESTIAL_WALL_SIGN);
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.MURUBLIGHT_SIGN);
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.MURUBLIGHT_WALL_SIGN);
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.VEILED_SIGN);
            event.modify(BlockEntityTypes.SIGN, EnderscapeBlocks.VEILED_WALL_SIGN);

            // Hanging Signs
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.CELESTIAL_HANGING_SIGN);
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.CELESTIAL_WALL_HANGING_SIGN);
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.MURUBLIGHT_HANGING_SIGN);
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.MURUBLIGHT_WALL_HANGING_SIGN);
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.VEILED_HANGING_SIGN);
            event.modify(BlockEntityTypes.HANGING_SIGN, EnderscapeBlocks.VEILED_WALL_HANGING_SIGN);
        });
    }
}