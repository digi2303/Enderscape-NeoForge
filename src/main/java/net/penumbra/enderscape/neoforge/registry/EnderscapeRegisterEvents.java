package net.penumbra.enderscape.neoforge.registry;

import com.google.common.reflect.Reflection;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.penumbra.enderscape.entity.ai.EnderscapeActivity;
import net.penumbra.enderscape.entity.ai.EnderscapeMemory;
import net.penumbra.enderscape.entity.ai.EnderscapeSensors;
import net.penumbra.enderscape.item.crafting.RustleRecipe;
import net.penumbra.enderscape.item.crafting.VoidLachrymaRecipe;
import net.penumbra.enderscape.registry.EnderscapeIntegration;
import net.penumbra.enderscape.registry.block.EnderscapeBlockEntities;
import net.penumbra.enderscape.registry.block.EnderscapeBlocks;
import net.penumbra.enderscape.registry.block.EnderscapeFluids;
import net.penumbra.enderscape.registry.component.EnderscapeDataComponents;
import net.penumbra.enderscape.registry.enchantment.EnderscapeEnchantmentEffectComponents;
import net.penumbra.enderscape.registry.entity.EnderscapeAttachments;
import net.penumbra.enderscape.registry.entity.EnderscapeAttributes;
import net.penumbra.enderscape.registry.entity.EnderscapeEntities;
import net.penumbra.enderscape.registry.entity.EnderscapeEntityDataSerializers;
import net.penumbra.enderscape.registry.entity.EnderscapeMobEffects;
import net.penumbra.enderscape.registry.entity.EnderscapePoi;
import net.penumbra.enderscape.registry.entity.EnderscapeSpawnConditionTypes;
import net.penumbra.enderscape.registry.entity.EnderscapeStats;
import net.penumbra.enderscape.registry.entity.EnderscapeSubEntityPredicates;
import net.penumbra.enderscape.registry.item.EnderscapeCreativeModeTab;
import net.penumbra.enderscape.registry.item.EnderscapeItems;
import net.penumbra.enderscape.registry.item.EnderscapePotions;
import net.penumbra.enderscape.registry.item.EnderscapeRecipeSerializers;
import net.penumbra.enderscape.registry.level.EnderscapeDensityFunctionTypes;
import net.penumbra.enderscape.registry.level.EnderscapeEnvironmentAttributes;
import net.penumbra.enderscape.registry.level.EnderscapeFeatures;
import net.penumbra.enderscape.registry.level.EnderscapeGameEvents;
import net.penumbra.enderscape.registry.particle.EnderscapeParticles;
import net.penumbra.enderscape.registry.server.EnderscapeCriteria;
import net.penumbra.enderscape.registry.server.EnderscapeGameRules;
import net.penumbra.enderscape.registry.sound.EnderscapeBiomeSounds;
import net.penumbra.enderscape.registry.sound.EnderscapeBlockSounds;
import net.penumbra.enderscape.registry.sound.EnderscapeEntitySounds;
import net.penumbra.enderscape.registry.sound.EnderscapeItemSounds;
import net.penumbra.enderscape.registry.sound.EnderscapeMobEffectSounds;
import net.penumbra.enderscape.registry.sound.EnderscapeMusic;
import net.penumbra.enderscape.registry.sound.EnderscapeSoundTypes;
import net.penumbra.enderscape.registry.sound.EnderscapeUiSounds;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EnderscapeRegisterEvents {

    private static final Map<ResourceKey<? extends Registry<?>>, List<Class<?>>> CLASSES_BY_REGISTRY = new LinkedHashMap<>();

    static {
        add(Registries.ATTRIBUTE, EnderscapeAttributes.class);
        add(Registries.DATA_COMPONENT_TYPE, EnderscapeDataComponents.class);
        add(Registries.PARTICLE_TYPE, EnderscapeParticles.class);
        add(Registries.GAME_EVENT, EnderscapeGameEvents.class);
        add(Registries.SOUND_EVENT,
                EnderscapeBiomeSounds.class,
                EnderscapeBlockSounds.class,
                EnderscapeEntitySounds.class,
                EnderscapeMobEffectSounds.class,
                EnderscapeItemSounds.class,
                EnderscapeUiSounds.class,
                EnderscapeMusic.class,
                EnderscapeSoundTypes.class
        );
        add(Registries.FLUID, EnderscapeFluids.class);
        add(Registries.MOB_EFFECT, EnderscapeMobEffects.class);
        add(Registries.BLOCK, EnderscapeItems.class, EnderscapeBlocks.class);
        add(Registries.ENTITY_TYPE, EnderscapeEntities.class);
        add(Registries.ITEM, EnderscapeIntegration.class);
        add(Registries.POTION, EnderscapePotions.class);
        add(Registries.BLOCK_ENTITY_TYPE, EnderscapeBlockEntities.class);
        add(Registries.CUSTOM_STAT, EnderscapeStats.class);
        add(Registries.RECIPE_TYPE, RustleRecipe.class, VoidLachrymaRecipe.class);
        add(Registries.RECIPE_SERIALIZER, EnderscapeRecipeSerializers.class);
        add(Registries.POINT_OF_INTEREST_TYPE, EnderscapePoi.class);
        add(Registries.MEMORY_MODULE_TYPE, EnderscapeMemory.class);
        add(Registries.SENSOR_TYPE, EnderscapeSensors.class);
        add(Registries.ACTIVITY, EnderscapeActivity.class);
        add(Registries.FEATURE_TYPE, EnderscapeFeatures.class);
        add(Registries.DENSITY_FUNCTION_TYPE, EnderscapeDensityFunctionTypes.class);
        add(Registries.CREATIVE_MODE_TAB, EnderscapeCreativeModeTab.class);
        add(Registries.TRIGGER_TYPE, EnderscapeCriteria.class);
        add(Registries.GAME_RULE, EnderscapeGameRules.class);
        add(Registries.ENTITY_SUB_PREDICATE_TYPE, EnderscapeSubEntityPredicates.class);
        add(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EnderscapeEnchantmentEffectComponents.class);
        add(Registries.SPAWN_CONDITION_TYPE, EnderscapeSpawnConditionTypes.class);
        add(Registries.ENVIRONMENT_ATTRIBUTE, EnderscapeEnvironmentAttributes.class);
        add(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderscapeAttachments.class);
        add(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, EnderscapeEntityDataSerializers.class);
        add(NeoForgeRegistries.Keys.FLUID_TYPES, EnderscapeFluidTypes.class);
    }

    private EnderscapeRegisterEvents() {
    }

    private static void add(ResourceKey<? extends Registry<?>> registry, Class<?>... classes) {
        CLASSES_BY_REGISTRY.put(registry, List.of(classes));
    }

    public static void register(RegisterEvent event) {
        List<Class<?>> classes = CLASSES_BY_REGISTRY.get(event.getRegistryKey());

        if (classes != null) {
            Reflection.initialize(classes.toArray(Class<?>[]::new));
        }
    }
}
