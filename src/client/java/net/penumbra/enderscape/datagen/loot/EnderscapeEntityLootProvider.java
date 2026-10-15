package net.penumbra.enderscape.datagen.loot;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.penumbra.enderscape.entity.rubblemite.RubblemiteVariant;
import net.penumbra.enderscape.registry.EnderscapeRegistries;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import net.penumbra.enderscape.registry.entity.EnderscapeEntityLootTables;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static net.penumbra.enderscape.registry.entity.EnderscapeEntities.*;
import static net.penumbra.enderscape.registry.entity.EnderscapeEntityLootTables.*;
import static net.penumbra.enderscape.registry.item.EnderscapeItems.RUBBLE_CHITIN;

public class EnderscapeEntityLootProvider implements LootTableSubProvider {

    private final LootTableSubProvider.Context lookup;

    public EnderscapeEntityLootProvider(LootTableSubProvider.Context lookup) {
        this.lookup = lookup;
    }

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        try {
            HolderGetter<RubblemiteVariant> variants = lookup.lookup(EnderscapeRegistries.RUBBLEMITE_VARIANT);

            consumer.accept(
                    getLootTable(DRIFTER),
                    LootTable.lootTable()
            );

            consumer.accept(
                    getLootTable(RUBBLEMITE),
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ContextIntProviders.exactly(1))
                                            .add(
                                                    LootItem.lootTableItem(RUBBLE_CHITIN)
                                                            .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(1)))
                                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(lookup.lookup(Registries.ENCHANTMENT), ContextFloatProviders.between(0, 1)))
                                                            .when(LootItemRandomChanceCondition.randomChance(0.65F))
                                            )
                                            .when(LootItemKilledByPlayerCondition.killedByPlayer())
                            )
            );

            createRubblemiteExtraDropItems(consumer, RUBBLEMITE_END_STONE, Blocks.END_STONE);
            createRubblemiteExtraDropItems(consumer, RUBBLEMITE_MIRESTONE, EnderscapeBlocks.MIRESTONE);
            createRubblemiteExtraDropItems(consumer, RUBBLEMITE_VERADITE, EnderscapeBlocks.VERADITE);
            createRubblemiteExtraDropItems(consumer, RUBBLEMITE_KURODITE, EnderscapeBlocks.KURODITE);

            consumer.accept(
                    getLootTable(RUSTLE),
                    LootTable.lootTable()
            );

            consumer.accept(
                    EnderscapeEntityLootTables.SHEARING_RUSTLE,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ContextIntProviders.exactly(1))
                                            .add(LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(ContextIntProviders.between(1, 3))))
                            )
            );

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRubblemiteExtraDropItems(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer, ResourceKey<LootTable> table, ItemLike item) throws InterruptedException, ExecutionException {
        consumer.accept(
                table,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ContextIntProviders.exactly(1))
                                        .add(
                                                LootItem.lootTableItem(item)
                                                        .apply(SetItemCountFunction.setCount(ContextIntProviders.between(0, 1)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(lookup.lookup(Registries.ENCHANTMENT), ContextFloatProviders.between(0, 1)))
                                        )
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        )
        );
    }

    private ResourceKey<LootTable> getLootTable(EntityType<?> type) {
        return type.getDefaultLootTable().orElseThrow(() -> new IllegalStateException("Entity " + type + " has no loot table"));
    }

    @Override
    public void run() {
        generate(lookup::accept);
    }
}