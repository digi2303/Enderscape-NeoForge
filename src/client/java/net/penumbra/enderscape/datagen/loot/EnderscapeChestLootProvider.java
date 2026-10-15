package net.penumbra.enderscape.datagen.loot;

import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.SingleEntryContainerBase;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.penumbra.enderscape.registry.item.EnderscapePotions;
import net.penumbra.enderscape.registry.level.EnderscapeBiomes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static net.minecraft.world.item.Items.*;
import static net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE;
import static net.penumbra.enderscape.registry.enchantment.EnderscapeEnchantments.TRANSDIMENSIONAL;
import static net.penumbra.enderscape.registry.item.EnderscapeItems.*;
import static net.penumbra.enderscape.registry.server.EnderscapeLootTables.*;

public class EnderscapeChestLootProvider implements LootTableSubProvider {

    private final LootTableSubProvider.Context lookup;

    public EnderscapeChestLootProvider(LootTableSubProvider.Context lookup) {
        this.lookup = lookup;
    }

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        try {
            registerStrongholdLoot(consumer);
            registerEndCityChestLoot(consumer);
            registerEndCitySpawnerLoot(consumer);
            registerEndHavenChestLoot(consumer);
            registerMirestoneRuinsLoot(consumer);

        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException("Error processing loot table", exception);
        }
    }

    private void registerStrongholdLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) throws InterruptedException, ExecutionException {
        LootItemConditionalFunction.Builder<?> toolDamage = SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.4F, 0.8F));
        LootItemConditionalFunction.Builder<?> secretDamage = SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.6F, 0.95F));
        EnchantWithLevelsFunction.Builder toolEnchantments = EnchantWithLevelsFunction.enchantWithLevels(lookup.lookup(Registries.ENCHANTMENT), ContextIntProviders.between(0, 10));
        
        consumer.accept(
                STRONGHOLD_LIBRARY_CHEST_SUPPLEMENTS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 10))
                                        .add(EmptyLootItem.emptyItem().setWeight(56))
                                        .add(createItem(MIRROR, 2))
                                        .add(createItem(BOOK, 2).apply(
                                                new EnchantRandomlyFunction.Builder()
                                                        .withOneOf(
                                                                HolderSet.direct(getEnchantmentReference(TRANSDIMENSIONAL))
                                                        )
                                                )
                                        )
                        )
        );

        consumer.accept(
                STRONGHOLD_CHEST_ALTAR,
                LootTable.lootTable().withPool(strongholdAltarLootPool(toolEnchantments, toolDamage, ContextIntProviders.between(2, 4)))
        );

        consumer.accept(
                STRONGHOLD_CHEST_ARMORY,
                LootTable.lootTable().withPool(strongholdValuableLootPool(secretDamage, ContextIntProviders.between(1, 3)))
        );

        consumer.accept(
                STRONGHOLD_CHEST_BEDROOM,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 3))

                                        .add(createItem(BOOK, 3).apply(countBetween(1, 3)))
                                        .add(createItem(EMERALD, 3).apply(countBetween(2, 4)))

                                        .add(createItem(BREAD, 2).apply(countBetween(3, 6)))
                                        .add(createItem(BUCKET, 2))
                                        .add(createItem(CARROT, 2).apply(countBetween(3, 8)))
                                        .add(createItem(COAL, 2).apply(countBetween(3, 6)))
                                        .add(createItem(LEATHER, 2).apply(countBetween(3, 6)))
                                        .add(createItem(POTATO, 2).apply(countBetween(3, 8)))

                                        .add(createItem(LEAD, 1))
                                        .add(createItem(NAME_TAG, 1))
                        )
        );

        consumer.accept(
                STRONGHOLD_CHEST_GARDEN,
                LootTable.lootTable()
                        .withPool(strongholdValuableLootPool(secretDamage, ContextIntProviders.between(2, 3)))
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 4))

                                        .add(createItem(ALLIUM, 3))
                                        .add(createItem(BLUE_ORCHID, 3))
                                        .add(createItem(DANDELION, 3))
                                        .add(createItem(POPPY, 3))

                                        .add(createItem(SUSPICIOUS_STEW, 1).apply(SetStewEffectFunction.stewEffect().withEffect(MobEffects.REGENERATION, ContextIntProviders.between(7, 10))))
                        )
        );

        consumer.accept(
                STRONGHOLD_CHEST_LIBRARY,
                LootTable.lootTable()
                        .withPool(strongholdLibraryLootPool(ContextIntProviders.between(2, 10)))
                        .withPool(strongholdLibraryEyeTrimPool(ContextIntProviders.exactly(1)))
        );

        consumer.accept(
                STRONGHOLD_CHEST_MANSION,
                LootTable.lootTable()
                        .withPool(strongholdLibraryLootPool(ContextIntProviders.between(2, 6)))
                        .withPool(strongholdLibraryEyeTrimPool(ContextIntProviders.exactly(1)))
                        .withPool(strongholdMansionUniqueLootPool(toolEnchantments, toolDamage))
        );

        consumer.accept(
                STRONGHOLD_CHEST_SECRET,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(3, 8))

                                        .add(createItem(BONE, 4).apply(countBetween(1, 3)))
                                        .add(createItem(GRAVEL, 4).apply(countBetween(1, 3)))
                                        .add(createItem(ARROW, 3).apply(countBetween(3, 4)))
                        )
                        .withPool(strongholdValuableLootPool(secretDamage, ContextIntProviders.between(1, 2)))
        );

        consumer.accept(
                STRONGHOLD_INFESTATION_DISPENSER,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(1, 2))
                                        .add(createItem(SPLASH_POTION, 1).apply(potionEffect(Potions.INFESTED)).apply(countBetween(1, 3)))
                                        .add(createItem(SPLASH_POTION, 1).apply(potionEffect(Potions.LONG_WEAKNESS)).apply(countBetween(1, 3)))
                        )
        );

        consumer.accept(
                STRONGHOLD_SPAWNER_BASIC,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(1, 3))
                                        .add(createItem(BREAD, 16).apply(countBetween(1, 3)))
                                        .add(createItem(GOLDEN_CARROT, 4).apply(countBetween(2, 4)))
                                        .add(createItem(GOLDEN_APPLE, 1).apply(countBetween(1, 2)))
                        )
        );
    }

    private LootPool.Builder strongholdAltarLootPool(EnchantWithLevelsFunction.Builder toolEnchantments, LootItemConditionalFunction.Builder<?> toolDamage, Holder<ContextIntProvider> rolls) throws InterruptedException, ExecutionException {
        return LootPool.lootPool()
                .setRolls(rolls)

                .add(createItem(ARROW, 14).apply(countBetween(4, 10)))
                .add(createItem(DYE.black(), 14).apply(countBetween(1, 3)))

                .add(createItem(BOOK, 11).apply(countBetween(1, 3)))
                .add(createItem(BREAD, 11).apply(countBetween(1, 6)))
                .add(createItem(GLASS_BOTTLE, 11).apply(countBetween(1, 2)))

                .add(createItem(BONE, 9).apply(countBetween(2, 8)))
                .add(createItem(CARROT, 9).apply(countBetween(1, 4)))
                .add(createItem(POTATO, 9).apply(countBetween(1, 4)))
                .add(createItem(ROTTEN_FLESH, 9).apply(countBetween(3, 12)))

                .add(createItem(ENDER_PEARL, 8))
                .add(createItem(REDSTONE, 8).apply(countBetween(4, 9)))

                .add(createItem(GOLDEN_PICKAXE, 6).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(GOLD_INGOT, 6).apply(countBetween(1, 4)))
                .add(createItem(IRON_INGOT, 6).apply(countBetween(1, 6)))

                .add(createEnchantedItem(BOW, 0, 20, 4).apply(toolDamage))
                .add(createItem(DIAMOND, 4).apply(countBetween(1, 2)))
                .add(createItem(FLINT_AND_STEEL, 4).apply(toolDamage))
                .add(createItem(IRON_CHESTPLATE, 4).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(IRON_SWORD, 4).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(SHIELD, 4).apply(SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.2F, 0.6F))))

                .add(createEnchantedItem(BOOK, ContextIntProviders.exactly(30), 3))

                .add(createItem(IRON_BOOTS, 2).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(IRON_HELMET, 2).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(IRON_HOE, 2).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(IRON_LEGGINGS, 2).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(IRON_PICKAXE, 2).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(POTION, 2).apply(potionEffect(Potions.HEALING)))
                .add(createItem(POTION, 2).apply(potionEffect(Potions.STRENGTH)))

                .add(createItem(DIAMOND_PICKAXE, 1).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(GOLDEN_APPLE, 1))
                .add(createItem(MUSIC_DISC_OTHERSIDE, 1))
                .add(createItem(POISONOUS_POTATO, 1));
    }

    private LootPool.Builder strongholdValuableLootPool(LootItemConditionalFunction.Builder<?> secretDamage, Holder<ContextIntProvider> rolls) throws InterruptedException, ExecutionException {
        return LootPool.lootPool()
                .setRolls(rolls)

                .add(createItem(BLAZE_POWDER, 5))
                .add(createItem(LAPIS_LAZULI, 5).apply(countBetween(3, 6)))

                .add(createItem(DIAMOND_BLOCK, 5))
                .add(createItem(IRON_BLOCK, 5))
                .add(createItem(GOLD_BLOCK, 5))

                .add(createEnchantedItem(BOOK, ContextIntProviders.exactly(30), 5))
                .add(createItem(GOLDEN_APPLE, 5).apply(countBetween(1, 3)))
                .add(createItem(GOLDEN_CARROT, 5).apply(countBetween(3, 6)))
                .add(createItem(POTION, 5).apply(potionEffect(Potions.STRONG_HEALING)).apply(countBetween(1, 3)))
                .add(createItem(POTION, 5).apply(potionEffect(Potions.STRONG_STRENGTH)).apply(countBetween(1, 3)))

                .add(createEnchantedItem(IRON_SWORD, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(IRON_PICKAXE, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(IRON_HELMET, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(IRON_CHESTPLATE, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(IRON_LEGGINGS, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(IRON_BOOTS, ContextIntProviders.exactly(30), 3).apply(secretDamage))

                .add(createEnchantedItem(DIAMOND_SWORD, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(DIAMOND_PICKAXE, ContextIntProviders.exactly(30), 3).apply(secretDamage))
                .add(createEnchantedItem(DIAMOND_CHESTPLATE, ContextIntProviders.exactly(30), 3).apply(secretDamage))

                .add(createItem(MUSIC_DISC_FAR, 1))
                .add(createItem(MIRROR, 1));
    }

    private LootPool.Builder strongholdLibraryLootPool(Holder<ContextIntProvider> rolls) throws InterruptedException, ExecutionException {
        return LootPool.lootPool()
                .setRolls(rolls)

                .add(createItem(BOOK, 20).apply(countBetween(1, 3)))
                .add(createItem(PAPER, 20).apply(countBetween(2, 7)))

                .add(createEnchantedItem(BOOK, ContextIntProviders.exactly(30), 10))

                .add(createItem(MAP, 3))
                .add(createItem(COMPASS, 3))

                .add(createItem(MIRROR, 2))
                .add(createItem(BOOK, 2).apply(
                                new EnchantRandomlyFunction.Builder()
                                        .withOneOf(
                                                HolderSet.direct(getEnchantmentReference(TRANSDIMENSIONAL))
                                        )
                        )
                );
    }

    private LootPool.Builder strongholdLibraryEyeTrimPool(Holder<ContextIntProvider> rolls) {
        return LootPool.lootPool()
                .setRolls(rolls)
                .add(createItem(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1));
    }

    private LootPool.Builder strongholdMansionUniqueLootPool(EnchantWithLevelsFunction.Builder toolEnchantments, LootItemConditionalFunction.Builder<?> toolDamage) {
        return LootPool.lootPool()
                .setRolls(ContextIntProviders.between(2, 4))

                .add(EmptyLootItem.emptyItem().setWeight(12))

                .add(createItem(BELL, 1))
                .add(createItem(BONE, 6).apply(countBetween(1, 8)))
                .add(createItem(CLOCK, 3))
                .add(createItem(COD, 1))
                .add(createItem(CROSSBOW, 3).apply(toolEnchantments).apply(toolDamage))
                .add(createItem(EGG, 1))
                .add(createItem(GLASS_BOTTLE, 3).apply(countBetween(1, 8)))
                .add(createItem(IRON_INGOT, 3).apply(countBetween(1, 3)))
                .add(createItem(PAINTING, 3))
                .add(createItem(ROTTEN_FLESH, 6).apply(countBetween(1, 8)));
    }

    private Holder<LootTable> fakeLootTable(LootTableSubProvider.Context provider, ResourceKey<LootTable> resourceKey) {
        return provider.lookup(Registries.LOOT_TABLE).getOrThrow(resourceKey);
    }

    private void registerEndCityChestLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) throws InterruptedException, ExecutionException {
        LootItemConditionalFunction.Builder<?> toolDamage = SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.4F, 0.8F));
        EnchantWithLevelsFunction.Builder toolEnchantments = EnchantWithLevelsFunction.enchantWithLevels(lookup.lookup(Registries.ENCHANTMENT), ContextIntProviders.between(20, 30));

        consumer.accept(
                END_CITY_TREASURE_POST_SUPPLEMENTS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))
                                        .add(NestedLootTable.lootTableReference(fakeLootTable(this.lookup, END_CITY_TREASURE)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))
                                        .add(NestedLootTable.lootTableReference(fakeLootTable(this.lookup, END_CITY_TREASURE_SUPPLEMENTS)))
                        )
        );

        consumer.accept(
                END_CITY_TREASURE_SUPPLEMENTS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(3, 5))

                                        .add(EmptyLootItem.emptyItem().setWeight(36))

                                        .add(createItem(CHORUS_FRUIT, 12).apply(countBetween(2, 6)))
                                        .add(addBiomeExclusiveItem(PURUBERRY, EnderscapeBiomes.CELESTIAL_GROVE, 10).apply(countBetween(2, 4)))
                                        .add(createItem(BREAD, 8).apply(countBetween(2, 5)))
                                        .add(createItem(ENDER_PEARL, 8).apply(countBetween(1, 2)))
                                        .add(createItem(SPECTRAL_ARROW, 6).apply(countBetween(4, 8)))
                                        .add(createItem(TIPPED_ARROW, 6).apply(potionEffect(EnderscapePotions.LONG_LOW_GRAVITY)).apply(countBetween(4, 8)))

                                        .add(createItem(HONEY_BOTTLE, 6).apply(countBetween(1, 3)))

                                        .add(createItem(GLASS_BOTTLE, 4).apply(countBetween(1, 3)))
                                        .add(createItem(GUNPOWDER, 4).apply(countBetween(2, 4)))
                                        .add(createItem(LODESTONE, 4))

                                        .add(createItem(SHIELD, 3).apply(SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.2F, 0.6F))))

                                        .add(createEnchantedItem(BOW, 5, 15, 3).apply(toolDamage))

                                        .add(createItem(DYE.magenta(), 1).apply(countBetween(3, 6)))
                                        .add(createItem(DYE.black(), 1).apply(countBetween(3, 6)))

                                        .add(createItem(POTION, 1).apply(potionEffect(Potions.SLOW_FALLING)))
                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(Potions.SLOWNESS)))
                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(EnderscapePotions.LOW_GRAVITY)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 3))

                                        .add(EmptyLootItem.emptyItem().setWeight(100))

                                        .add(createItem(BLAZE_ROD, 6).apply(countBetween(2, 4)))
                                        .add(createItem(NEBULITE_SHARDS, 3).apply(countBetween(2, 12)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(0, 1))

                                        .add(EmptyLootItem.emptyItem().setWeight(40))

                                        .add(createItem(GOLDEN_CARROT, 40).apply(countBetween(4, 12)))
                                        .add(createItem(GOLDEN_APPLE, 10).apply(countBetween(1, 2)))
                                        .add(createItem(ENCHANTED_GOLDEN_APPLE, 1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))

                                        .add(EmptyLootItem.emptyItem().setWeight(40))

                                        .add(createItem(MIRROR, 6))
                                        .add(createItem(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 6))

                                        .add(createItem(MUSIC_DISC_GLARE, 3))
                                        .add(createItem(CRESCENT_BANNER_PATTERN, 3))
                                        .add(createItem(STASIS_ARMOR_TRIM_SMITHING_TEMPLATE, 3))
                        )
        );

        consumer.accept(
                END_CITY_CHEST,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(3, 5))

                                        .add(createItem(CHORUS_FRUIT, 12).apply(countBetween(2, 6)))
                                        .add(addBiomeExclusiveItem(PURUBERRY, EnderscapeBiomes.CELESTIAL_GROVE, 10).apply(countBetween(2, 4)))
                                        .add(createItem(BREAD, 8).apply(countBetween(2, 5)))
                                        .add(createItem(ENDER_PEARL, 8).apply(countBetween(1, 2)))
                                        .add(createItem(SPECTRAL_ARROW, 6).apply(countBetween(4, 8)))
                                        .add(createItem(TIPPED_ARROW, 6).apply(potionEffect(EnderscapePotions.LONG_LOW_GRAVITY)).apply(countBetween(4, 8)))

                                        .add(createItem(HONEY_BOTTLE, 6).apply(countBetween(1, 3)))

                                        .add(createItem(DIAMOND, 4).apply(countBetween(1, 2)))
                                        .add(createItem(GOLD_INGOT, 4).apply(countBetween(1, 4)))
                                        .add(createItem(IRON_INGOT, 4).apply(countBetween(2, 8)))

                                        .add(createItem(GLASS_BOTTLE, 4).apply(countBetween(1, 3)))
                                        .add(createItem(GUNPOWDER, 4).apply(countBetween(2, 4)))
                                        .add(createItem(LODESTONE, 4))

                                        .add(createItem(IRON_SWORD, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_SPEAR, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_PICKAXE, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_AXE, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_HOE, 3).apply(toolEnchantments).apply(toolDamage))

                                        .add(createItem(SHIELD, 3).apply(SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.2F, 0.6F))))

                                        .add(createItem(IRON_HELMET, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_CHESTPLATE, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_LEGGINGS, 3).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_BOOTS, 3).apply(toolEnchantments).apply(toolDamage))

                                        .add(createEnchantedItem(BOW, 5, 15, 3).apply(toolDamage))

                                        .add(createItem(DYE.magenta(), 1).apply(countBetween(3, 6)))
                                        .add(createItem(DYE.black(), 1).apply(countBetween(3, 6)))

                                        .add(createItem(POTION, 1).apply(potionEffect(Potions.SLOW_FALLING)))
                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(Potions.SLOWNESS)))
                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(EnderscapePotions.LOW_GRAVITY)))
                        )
        );
    }

    private void registerEndCitySpawnerLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) throws InterruptedException, ExecutionException {
        consumer.accept(
                END_CITY_SPAWNER_BASIC,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))

                                        .add(createItem(BREAD, 12).apply(countBetween(2, 8)))
                                        .add(createItem(GOLDEN_CARROT, 4).apply(countBetween(2, 8)))
                                        .add(createItem(GOLDEN_APPLE, 1).apply(countBetween(1, 3)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))

                                        .add(EmptyLootItem.emptyItem().setWeight(3))

                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(Potions.STRONG_HEALING)))
                                        .add(createItem(LINGERING_POTION, 1).apply(potionEffect(Potions.SLOW_FALLING)))
                        )
        );

        consumer.accept(
                END_CITY_SPAWNER_KEY,
                LootTable.lootTable().withPool(LootPool.lootPool().add(createItem(END_CITY_KEY, 2)))
        );
    }

    private void registerMirestoneRuinsLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(
                MIRESTONE_RUINS_CHEST,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 4))

                                        .add(createItem(BONE, 12).apply(countBetween(3, 6)))
                                        .add(createItem(CHORUS_FRUIT, 12).apply(countBetween(2, 6)))

                                        .add(createItem(BREAD, 8).apply(countBetween(2, 4)))
                                        .add(createItem(ENDER_PEARL, 8).apply(countBetween(1, 6)))
                                        .add(createItem(MURUBLIGHT_BRACKET_ITEM, 8).apply(countBetween(1, 2)))

                                        .add(createItem(GUNPOWDER, 6).apply(countBetween(4, 8)))
                                        .add(createItem(LEATHER, 6).apply(countBetween(4, 8)))
                                        .add(createItem(PAPER, 6).apply(countBetween(4, 8)))
                                        .add(createItem(SUGAR, 6).apply(countBetween(3, 6)))

                                        .add(createItem(PURUBERRY, 3).apply(countBetween(2, 8)))
                                        .add(createItem(DYE.black(), 3).apply(countBetween(1, 6)))
                        ).withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))

                                        .add(EmptyLootItem.emptyItem().setWeight(8))

                                        .add(createItem(DAGGER, 1))
                                        .add(createItem(MUSIC_DISC_DECAY, 1))
                        )
        );
    }

    private void registerEndHavenChestLoot(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) throws InterruptedException, ExecutionException {
        LootItemConditionalFunction.Builder<?> toolDamage = SetItemDamageFunction.setDamage(ContextFloatProviders.between(0.4F, 0.8F));
        EnchantWithLevelsFunction.Builder toolEnchantments = EnchantWithLevelsFunction.enchantWithLevels(lookup.lookup(Registries.ENCHANTMENT), ContextIntProviders.between(20, 30));

        consumer.accept(
                END_HAVEN_CHEST,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 3))

                                        .add(createItem(BREAD, 5).apply(countBetween(3, 6)))
                                        .add(createItem(PURUBERRY, 3).apply(countBetween(1, 2)))
                                        .add(createItem(GOLDEN_CARROT, 1).apply(countBetween(3, 6)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(2, 4))

                                        .add(EmptyLootItem.emptyItem().setWeight(16))

                                        .add(createItem(ENDER_PEARL, 3).apply(countBetween(2, 3)))
                                        .add(createItem(HONEY_BOTTLE, 3).apply(countBetween(2, 3)))

                                        .add(createItem(POTION, 1).apply(potionEffect(Potions.SLOW_FALLING)))
                                        .add(createItem(POTION, 1).apply(potionEffect(Potions.REGENERATION)))
                                        .add(createItem(POTION, 1).apply(potionEffect(EnderscapePotions.LOW_GRAVITY)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.between(1, 2))

                                        .add(EmptyLootItem.emptyItem().setWeight(80))

                                        .add(createItem(IRON_SWORD, 8).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_SPEAR, 8).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_PICKAXE, 8).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_AXE, 8).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_HOE, 8).apply(toolEnchantments).apply(toolDamage))

                                        .add(createItem(IRON_HELMET, 6).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_CHESTPLATE, 6).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_LEGGINGS, 6).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(IRON_BOOTS, 6).apply(toolEnchantments).apply(toolDamage))

                                        .add(createItem(DIAMOND_SWORD, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_SPEAR, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_PICKAXE, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_AXE, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_HOE, 1).apply(toolEnchantments).apply(toolDamage))

                                        .add(createItem(DIAMOND_HELMET, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_CHESTPLATE, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_LEGGINGS, 1).apply(toolEnchantments).apply(toolDamage))
                                        .add(createItem(DIAMOND_BOOTS, 1).apply(toolEnchantments).apply(toolDamage))
                        )
        );
    }

    @NotNull
    private LootItemConditionalFunction.Builder<?> potionEffect(Holder<Potion> potion) throws InterruptedException, ExecutionException {
        return SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(getPotion(potion)));
    }

    @NotNull
    private Holder.Reference<Potion> getPotion(Holder<Potion> potion) throws InterruptedException, ExecutionException {
        return lookup.lookup(Registries.POTION).getOrThrow(potion.unwrapKey().get());
    }

    @NotNull
    private LootItemConditionalFunction.Builder<?> countBetween(int f, int g) {
        return SetItemCountFunction.setCount(ContextIntProviders.between(f, g));
    }

    @NotNull
    private SingleEntryContainerBase.Builder<?> createItem(Item item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight);
    }

    @NotNull
    private SingleEntryContainerBase.Builder<?> addBiomeExclusiveItem(Item item, ResourceKey<Biome> biome, int weight) throws InterruptedException, ExecutionException {
        return createItem(item, weight).when(LocationCheck.checkLocation(LocationPredicate.Builder.inBiome(lookup.lookup(Registries.BIOME).getOrThrow(biome))));
    }

    @NotNull
    private SingleEntryContainerBase.Builder<?> createEnchantedItem(Item item, int minEnchant, int maxEnchant, int weight) throws InterruptedException, ExecutionException {
        return createItem(item, weight).apply(EnchantWithLevelsFunction.enchantWithLevels(lookup.lookup(Registries.ENCHANTMENT), ContextIntProviders.between(minEnchant, maxEnchant)));
    }

    @NotNull
    private SingleEntryContainerBase.Builder<?> createEnchantedItem(Item item, Holder<ContextIntProvider> provider, int weight) throws InterruptedException, ExecutionException {
        return createItem(item, weight).apply(EnchantWithLevelsFunction.enchantWithLevels(lookup.lookup(Registries.ENCHANTMENT), provider));
    }

    @NotNull
    private SingleEntryContainerBase.Builder<?> createEnchantedItem(Item item, ResourceKey<Enchantment> enchantment, Holder<ContextIntProvider> uniform, int weight) throws InterruptedException, ExecutionException {
        return createItem(item, weight).apply(new SetEnchantmentsFunction.Builder().withEnchantment(lookup.lookup(Registries.ENCHANTMENT).getOrThrow(enchantment), uniform));
    }

    @NotNull
    private Holder.Reference<Enchantment> getEnchantmentReference(ResourceKey<Enchantment> enchantment) throws InterruptedException, ExecutionException {
        return lookup.lookup(Registries.ENCHANTMENT).getOrThrow(enchantment);
    }

    @Override
    public void run() {
        generate(lookup::accept);
    }
}