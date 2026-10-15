package net.penumbra.enderscape.datagen;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.neoforged.neoforge.data.event.PackGenerator;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.datagen.advancement.EnderscapeAdvancementProvider;
import net.penumbra.enderscape.datagen.loot.EnderscapeBlockLootProvider;
import net.penumbra.enderscape.datagen.loot.EnderscapeChestLootProvider;
import net.penumbra.enderscape.datagen.loot.EnderscapeEntityLootProvider;
import net.penumbra.enderscape.datagen.loot.EnderscapeVaultLootProvider;
import net.penumbra.enderscape.datagen.model.EnderscapeModelProvider;
import net.penumbra.enderscape.datagen.recipe.EnderscapeRecipeProvider;
import net.penumbra.enderscape.datagen.tag.*;
import net.penumbra.enderscape.neoforge.client.EnderscapeRecipeIds;
import net.penumbra.enderscape.neoforge.registry.EnderscapeBiomeModifiers;
import net.penumbra.enderscape.registry.EnderscapeRegistries;
import net.penumbra.enderscape.registry.block.EnderscapeBannerPatterns;
import net.penumbra.enderscape.registry.block.EnderscapeTrialSpawnerConfigs;
import net.penumbra.enderscape.registry.enchantment.EnderscapeEnchantments;
import net.penumbra.enderscape.registry.entity.EnderscapeDamageTypes;
import net.penumbra.enderscape.registry.entity.EnderscapePaintingVariants;
import net.penumbra.enderscape.registry.entity.EnderscapeRubblemiteVariants;
import net.penumbra.enderscape.registry.item.EnderscapeTrimMaterials;
import net.penumbra.enderscape.registry.item.EnderscapeTrimPatterns;
import net.penumbra.enderscape.registry.level.*;
import net.penumbra.enderscape.registry.lithostitched.EnderscapeWorldgenModifiers;
import net.penumbra.enderscape.registry.sound.EnderscapeJukeboxSongs;
import net.penumbra.enderscape.registry.sound.EnderscapeMagniaRadioSongs;
import net.penumbra.enderscape.registry.sound.EnderscapeStructureMusic;
import net.penumbra.enderscape.registry.structure.EnderscapeProcessorLists;
import net.penumbra.enderscape.registry.structure.EnderscapeStructureSets;
import net.penumbra.enderscape.registry.structure.EnderscapeStructures;
import net.penumbra.enderscape.registry.structure.EnderscapeTemplatePools;

public class EnderscapeDataGenerator {

	public static void onInitializeDataGenerator(GatherDataEvent.Client generator) {
		PackGenerator pack = generator.getDefaultPackGenerator();

		pack.createProvider(EnderscapeBannerPatternTagProvider::new);
		pack.createProvider(EnderscapeBiomeTagProvider::new);
		pack.createProvider(EnderscapeBlockTagProvider::new);
		pack.createProvider(EnderscapeDamageTypeTagProvider::new);
		pack.createProvider(EnderscapeEnchantmentTagProvider::new);
		pack.createProvider(EnderscapeEntityTagProvider::new);
		pack.createProvider(EnderscapeFluidTagProvider::new);
		pack.createProvider(EnderscapeItemTagProvider::new);
		pack.createProvider(EnderscapeMobEffectTagProvider::new);
		pack.createProvider(EnderscapeModelProvider::new);
		pack.createProvider(EnderscapePaintingVariantTagProvider::new);
		pack.createProvider(EnderscapePoiTagProvider::new);
		pack.createProvider(EnderscapeSoundEventTagProvider::new);
		pack.createProvider(EnderscapeStructureTagProvider::new);

		EnderscapePacksProvider.initialize(generator);
	}

	public static void buildRegistry(GatherDataRegistryEntriesEvent builder) {
		builder.add(Registries.BANNER_PATTERN, EnderscapeBannerPatterns::bootstrap);
		builder.add(Registries.BIOME, EnderscapeBiomes::bootstrap);
		builder.add(Registries.FEATURE, EnderscapeConfiguredFeatures::bootstrap);
		builder.add(Registries.DAMAGE_TYPE, EnderscapeDamageTypes::bootstrap);
		builder.add(Registries.ENCHANTMENT, EnderscapeEnchantments::bootstrap);
		builder.add(Registries.JUKEBOX_SONG, EnderscapeJukeboxSongs::bootstrap);
		builder.add(Registries.MATERIAL_RULE, EnderscapeSurfaceRules::bootstrap);
		builder.add(Registries.NOISE, EnderscapeNoiseParameters::bootstrap);
		builder.add(Registries.PAINTING_VARIANT, EnderscapePaintingVariants::bootstrap);
		builder.add(Registries.PLACED_FEATURE, EnderscapePlacedFeatures::bootstrap);
		builder.add(Registries.PROCESSOR_LIST, EnderscapeProcessorLists::bootstrap);
		builder.add(Registries.STRUCTURE, EnderscapeStructures::bootstrap);
		builder.add(Registries.STRUCTURE_SET, EnderscapeStructureSets::bootstrap);
		builder.add(Registries.TEMPLATE_POOL, EnderscapeTemplatePools::bootstrap);
		builder.add(Registries.TRIAL_SPAWNER_CONFIG, EnderscapeTrialSpawnerConfigs::bootstrap);
		builder.add(Registries.TRIM_MATERIAL, EnderscapeTrimMaterials::bootstrap);
		builder.add(Registries.TRIM_PATTERN, EnderscapeTrimPatterns::bootstrap);

		builder.add(EnderscapeRegistries.MAGNIA_RADIO_SONG, EnderscapeMagniaRadioSongs::bootstrap);
		builder.add(EnderscapeRegistries.RUBBLEMITE_VARIANT, EnderscapeRubblemiteVariants::bootstrap);
		builder.add(EnderscapeRegistries.STRUCTURE_MUSIC, EnderscapeStructureMusic::bootstrap);

		builder.add(LithostitchedRegistries.WORLDGEN_MODIFIER, EnderscapeWorldgenModifiers::bootstrap);

		builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, EnderscapeBiomeModifiers::bootstrap);

		builder.lootTable(
				new LootTableProvider.SubProviderEntry(EnderscapeBlockLootProvider::new, LootContextParamSets.BLOCK),
				new LootTableProvider.SubProviderEntry(EnderscapeChestLootProvider::new, LootContextParamSets.CHEST),
				new LootTableProvider.SubProviderEntry(EnderscapeEntityLootProvider::new, LootContextParamSets.ENTITY),
				new LootTableProvider.SubProviderEntry(EnderscapeVaultLootProvider::new, LootContextParamSets.VAULT)
		);
		builder.advancement(EnderscapeAdvancementProvider::new);
		builder.gatherFor(Enderscape.MOD_ID, "minecraft").add(EnderscapeRecipeIds.inNamespace(Enderscape.MOD_ID, EnderscapeRecipeProvider::createRecipeProvider));
	}
}