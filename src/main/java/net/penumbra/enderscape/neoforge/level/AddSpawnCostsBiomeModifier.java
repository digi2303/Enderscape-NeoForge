package net.penumbra.enderscape.neoforge.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record AddSpawnCostsBiomeModifier(
        HolderSet<Biome> biomes,
        HolderSet<EntityType<?>> entityTypes,
        MobSpawnSettings.MobSpawnCost spawnCost
) implements BiomeModifier {

    public static final MapCodec<AddSpawnCostsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnCostsBiomeModifier::biomes),
            RegistryCodecs.holderSet(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(AddSpawnCostsBiomeModifier::entityTypes),
            MobSpawnSettings.MobSpawnCost.CODEC.fieldOf("spawn_cost").forGetter(AddSpawnCostsBiomeModifier::spawnCost)
    ).apply(instance, AddSpawnCostsBiomeModifier::new));

    @Override
    public void modify(RegistryAccess registries, Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            entityTypes.forEach(entityType -> builder.getMobSpawnSettings().addMobSpawnCost(entityType.value(), spawnCost.charge(), spawnCost.energyBudget()));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
