package net.penumbra.enderscape.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.block.dispenser.LodestoneTeleportationDispenserBehavior;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.manager.EndHavenManager;
import net.penumbra.enderscape.manager.RustleItemConversionManager;
import net.penumbra.enderscape.manager.VoidLachrymaItemConversionManager;
import net.penumbra.enderscape.manager.VoidManager;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.neoforge.entity.EnderscapeLivingDamage;
import net.penumbra.enderscape.neoforge.registry.EnderscapeDynamicRegistrySetup;
import net.penumbra.enderscape.registry.enchantment.EnderscapeEnchantments;
import net.penumbra.enderscape.registry.entity.EnderscapeAttachments;
import net.penumbra.enderscape.registry.entity.EnderscapeMobEffects;
import net.penumbra.enderscape.registry.item.EnderscapeEquipmentAssets;
import net.penumbra.enderscape.registry.level.EnderscapeBiomes;
import net.penumbra.enderscape.registry.server.EnderscapeLootTables;
import net.penumbra.enderscape.registry.sound.EnderscapeItemSounds;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.item.Items.CHORUS_FRUIT;
import static net.minecraft.world.level.block.Blocks.CHORUS_FLOWER;
import static net.minecraft.world.level.block.Blocks.CHORUS_PLANT;
import static net.penumbra.enderscape.registry.block.EnderscapeBlocks.*;
import static net.penumbra.enderscape.registry.item.EnderscapeItems.*;

public class EnderscapeIntegration {

    private static final EnderscapeConfig CONFIG = EnderscapeConfig.getInstance();

    static {
        registerAliases();
        registerCompostableItems();
        registerDispenserBehavior();
        registerFlammableBlocks();

        EnderscapeNeoForge.modEventBus().addListener(ModifyDefaultComponentsEvent.class, (context) -> {
            context.modifyMatching(
                    (item, components) -> item == Items.SHULKER_SHELL,
                    (builder, registries, item) -> builder.set(
                            DataComponents.EQUIPPABLE,
                            Equippable.builder(ArmorType.HELMET.getSlot())
                                    .setSwappable(false)
                                    .setEquipSound(EnderscapeItemSounds.SHULKER_SHELL_EQUIP)
                                    .setAsset(EnderscapeEquipmentAssets.SHULKER_SHELL)
                                    .build()
                    )
            );

            context.modifyMatching(
                    (item, components) -> item == Items.TOTEM_OF_UNDYING,
                    (builder, registries, item) -> {
                        DeathProtection protection = builder.getOrDefault(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING);
                        builder.set(DataComponents.DEATH_PROTECTION, addDeathProtectionEffects(
                                protection,
                                new MobEffectInstance(EnderscapeMobEffects.VOID_RESISTANCE, 10 * 20))
                        );
                    }
            );
        });

        NeoForge.EVENT_BUS.addListener(LootTableLoadEvent.class, (event) -> {
            addLootTableInjection(
                    event.getRegistries(),
                    CONFIG.supplementVanillaStrongholdLibraryLoot,
                    BuiltInLootTables.STRONGHOLD_LIBRARY,
                    EnderscapeLootTables.STRONGHOLD_LIBRARY_CHEST_SUPPLEMENTS,
                    event.getKey(),
                    event.getTable()
            );
        });

        NeoForge.EVENT_BUS.addListener(LivingIncomingDamageEvent.class, (event) -> {
            if (!VoidManager.allowLivingEntityDamage(event.getEntity(), event.getSource(), event.getAmount())) event.setCanceled(true);
        });
        NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, (event) -> {
            if (!event.getEntity().isDeadOrDying()) VoidManager.afterLivingEntityDamage(event.getEntity(), event.getSource(), event.getOriginalDamage(), EnderscapeLivingDamage.damageTaken(event), event.getBlockedDamage() > 0.0F);
        });
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, LivingDeathEvent.class, (event) -> {
            if (!event.getEntity().level().isClientSide()) EnderscapeAttachments.afterLivingEntityDeath(event.getEntity(), event.getSource());
        });

        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, (event) -> RustleItemConversionManager.onServerStarting(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, (event) -> RustleItemConversionManager.onServerStopping(event.getServer()));

        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, (event) -> VoidLachrymaItemConversionManager.onServerStarting(event.getServer()));
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, (event) -> VoidLachrymaItemConversionManager.onServerStopping(event.getServer()));

        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, (event) -> {
            if (event.getPlayer() == null) {
                RustleItemConversionManager.onServerStarting(event.getPlayerList().getServer());
                VoidLachrymaItemConversionManager.onServerStarting(event.getPlayerList().getServer());
            }
        });

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerChangedDimensionEvent.class, (event) -> EndHavenManager.updateConnection(event.getEntity()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, PlayerEvent.Clone.class, (event) -> {
            if (event.getOriginal().level() != event.getEntity().level()) EndHavenManager.updateConnection(event.getEntity());
        });

        NeoForge.EVENT_BUS.addListener(MobEffectEvent.Added.class, (event) -> {
            MobEffectInstance instance = event.getEffectInstance();
            LivingEntity entity = event.getEntity();

            if (!entity.level().isClientSide() && instance.is(EnderscapeMobEffects.STUNNED)) {
                entity.setData(EnderscapeAttachments.STUN_DURATION, instance.getDuration());
                entity.setData(EnderscapeAttachments.STUN_TICKS, instance.getDuration());
            }
        });

        Consumer<MobEffectEvent> beforeRemove = (event) -> {
            MobEffectInstance instance = event.getEffectInstance();
            LivingEntity entity = event.getEntity();

            if (!entity.level().isClientSide() && instance != null && instance.is(EnderscapeMobEffects.STUNNED)) {
                entity.removeData(EnderscapeAttachments.STUN_DURATION);
                entity.removeData(EnderscapeAttachments.STUN_TICKS);
            }
        };
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MobEffectEvent.Remove.class, beforeRemove::accept);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MobEffectEvent.Expired.class, beforeRemove::accept);
    }

    private static DeathProtection addDeathProtectionEffects(DeathProtection protection, MobEffectInstance ... effects) {
        List<ConsumeEffect> original = new ArrayList<>(protection.deathEffects());

        for (MobEffectInstance instance : effects) {
            original.add(new ApplyStatusEffectsConsumeEffect(instance));
        }

        return new DeathProtection(original);
    }

    private static void addLootTableInjection(HolderGetter.Provider provider, boolean allowed, ResourceKey<LootTable> original, ResourceKey<LootTable> injection, ResourceKey<LootTable> key, LootTable table) {
        if (allowed && key.equals(original)) {
            table.addPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(NestedLootTable.lootTableReference(provider.getOrThrow(injection)).setWeight(1)).build());
        }
    }

    private static void registerAliases() {
        EnderscapeDynamicRegistrySetup.register((registryView) -> {
            dynamicRegistryAlias(registryView, Registries.BIOME, "magnia_crags", EnderscapeBiomes.MAGNIA_FIELDS.identifier().getPath());
            dynamicRegistryAlias(registryView, Registries.DATA_COMPONENT_TYPE, "current_nebulite_fuel", "current_fuel");
            dynamicRegistryAlias(registryView, Registries.ENCHANTMENT, "lightspeed", EnderscapeEnchantments.RESONANCE.identifier().getPath());
        });

        BuiltInRegistries.BLOCK.addAlias(Enderscape.id("flanger_berry_vine"), Enderscape.id("puruberry_vine"));
        BuiltInRegistries.BLOCK.addAlias(Enderscape.id("flanger_berry_flower"), Enderscape.id("puruberry_flower"));
        BuiltInRegistries.BLOCK.addAlias(Enderscape.id("unripe_flanger_berry_block"), Enderscape.id("unripe_puruberry_block"));
        BuiltInRegistries.BLOCK.addAlias(Enderscape.id("ripe_flanger_berry_block"), Enderscape.id("ripe_puruberry_block"));
        BuiltInRegistries.ITEM.addAlias(Enderscape.id("flanger_berry"), Enderscape.id("puruberry"));

        BuiltInRegistries.ENTITY_TYPE.addAlias(Enderscape.id("driftlet"), Enderscape.id("drifter"));

        BuiltInRegistries.ITEM.addAlias(Enderscape.id("end_stone_rubble_shield"), Enderscape.id("rubble_shield"));
        BuiltInRegistries.ITEM.addAlias(Enderscape.id("mirestone_rubble_shield"), Enderscape.id("rubble_shield"));
        BuiltInRegistries.ITEM.addAlias(Enderscape.id("veradite_rubble_shield"), Enderscape.id("rubble_shield"));
        BuiltInRegistries.ITEM.addAlias(Enderscape.id("kurodite_rubble_shield"), Enderscape.id("rubble_shield"));

        blockAndItemAlias("celestial_path_block", "celestial_path");
        blockAndItemAlias("corrupt_path_block", "corrupt_path");
    }

    private static <T> void dynamicRegistryAlias(EnderscapeDynamicRegistrySetup.View view, ResourceKey<Registry<T>> registry, String old_name, String new_name) {
        Optional<Registry<T>> optional = view.getOptional(registry);
        optional.ifPresent(value -> value.addAlias(Enderscape.id(old_name), Enderscape.id(new_name)));
    }

    private static void blockAndItemAlias(String previous, String current) {
        BuiltInRegistries.BLOCK.addAlias(Enderscape.id(previous), Enderscape.id(current));
        BuiltInRegistries.ITEM.addAlias(Enderscape.id(previous), Enderscape.id(current));
    }

    private static void registerCompostableItems() {
        EnderscapeNeoForge.modEventBus().addListener(ModifyDefaultComponentsEvent.class, modifyContext -> {
            modifyContext.modify(CHORUS_FRUIT, (ctx, registries, item) -> ctx.set(DataComponents.COMPOSTABLE, new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
        });
    }

    private static void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(RUSTLE_BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem item = (DispensibleContainerItem) stack.getItem();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();

                if (item.emptyContents(null, level, pos, null)) {
                    item.checkExtraContent(null, level, stack, pos);
                    return consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
                } else {
                    return defaultBehavior.dispense(source, stack);
                }
            }
        });

        DispenserBlock.registerBehavior(VOID_LACHRYMA_BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem item = (DispensibleContainerItem) stack.getItem();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                Level level = source.level();

                if (item.emptyContents(null, level, pos, null)) {
                    item.checkExtraContent(null, level, stack, pos);
                    return consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
                } else {
                    return defaultBehavior.dispense(source, stack);
                }
            }
        });

        DispenserBlock.registerBehavior(MIRROR, new LodestoneTeleportationDispenserBehavior());
    }

    private static void registerFlammableBlocks() {
        FireBlock fire = (FireBlock) Blocks.FIRE;

        fire.setFlammable(DRY_END_GROWTH, 60, 100);
        fire.setFlammable(CHORUS_SPROUTS, 60, 100);
        fire.setFlammable(WISP_GROWTH, 60, 100);
        fire.setFlammable(WISP_SPROUTS, 60, 100);
        fire.setFlammable(WISP_FLOWER, 60, 100);

        fire.setFlammable(CHORUS_PLANT, 5, 5);
        fire.setFlammable(CHORUS_FLOWER, 5, 5);

        fire.setFlammable(VEILED_LOG, 5, 5);
        fire.setFlammable(VEILED_WOOD, 5, 5);
        fire.setFlammable(STRIPPED_VEILED_LOG, 5, 5);
        fire.setFlammable(STRIPPED_VEILED_WOOD, 5, 5);

        fire.setFlammable(VEILED_PLANKS, 5, 5);
        fire.setFlammable(VEILED_SLAB, 5, 5);
        fire.setFlammable(VEILED_FENCE_GATE, 5, 5);
        fire.setFlammable(VEILED_FENCE, 5, 5);
        fire.setFlammable(VEILED_STAIRS, 5, 5);
        fire.setFlammable(VEILED_SHELF, 30, 20);

        fire.setFlammable(VEILED_SAPLING, 15, 60);
        fire.setFlammable(VEILED_LEAVES, 30, 60);
        fire.setFlammable(VEILED_LEAF_PILE, 60, 20);

        fire.setFlammable(CELESTIAL_STEM, 5, 5);
        fire.setFlammable(CELESTIAL_HYPHAE, 5, 5);
        fire.setFlammable(STRIPPED_CELESTIAL_STEM, 5, 5);
        fire.setFlammable(STRIPPED_CELESTIAL_HYPHAE, 5, 5);

        fire.setFlammable(CELESTIAL_PLANKS, 5, 5);
        fire.setFlammable(CELESTIAL_SLAB, 5, 5);
        fire.setFlammable(CELESTIAL_FENCE_GATE, 5, 5);
        fire.setFlammable(CELESTIAL_FENCE, 5, 5);
        fire.setFlammable(CELESTIAL_STAIRS, 5, 5);
        fire.setFlammable(CELESTIAL_SHELF, 30, 20);

        fire.setFlammable(CELESTIAL_CAP, 30, 60);

        fire.setFlammable(MURUBLIGHT_STEM, 5, 5);
        fire.setFlammable(MURUBLIGHT_HYPHAE, 5, 5);
        fire.setFlammable(STRIPPED_MURUBLIGHT_STEM, 5, 5);
        fire.setFlammable(STRIPPED_MURUBLIGHT_HYPHAE, 5, 5);

        fire.setFlammable(MURUBLIGHT_PLANKS, 5, 5);
        fire.setFlammable(MURUBLIGHT_SLAB, 5, 5);
        fire.setFlammable(MURUBLIGHT_FENCE_GATE, 5, 5);
        fire.setFlammable(MURUBLIGHT_FENCE, 5, 5);
        fire.setFlammable(MURUBLIGHT_STAIRS, 5, 5);
        fire.setFlammable(MURUBLIGHT_SHELF, 30, 20);

        fire.setFlammable(MURUBLIGHT_CAP, 30, 60);
        
        fire.setFlammable(CELESTIAL_GROWTH, 60, 60);
        fire.setFlammable(BULB_FLOWER, 60, 100);
        fire.setFlammable(PURUBERRY_VINE, 60, 100);
        fire.setFlammable(PURUBERRY_FLOWER, 60, 100);
        fire.setFlammable(UNRIPE_PURUBERRY_BLOCK, 60, 100);
        fire.setFlammable(RIPE_PURUBERRY_BLOCK, 60, 100);
        fire.setFlammable(CELESTIAL_CHANTERELLE, 60, 100);
        fire.setFlammable(MURUBLIGHT_CHANTERELLE, 60, 100);

        fire.setFlammable(CORRUPT_GROWTH, 60, 100);
        fire.setFlammable(BLINKLIGHT_VINES_BODY, 15, 60);
        fire.setFlammable(BLINKLIGHT_VINES_HEAD, 15, 60);
        fire.setFlammable(MURUBLIGHT_BRACKET, 60, 100);

        fire.setFlammable(VOID_SHALE, 5, 5);
    }
}