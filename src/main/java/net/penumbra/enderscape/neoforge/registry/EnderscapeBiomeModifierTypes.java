package net.penumbra.enderscape.neoforge.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.level.AddFeaturesAlongsideBiomeModifier;
import net.penumbra.enderscape.neoforge.level.AddSpawnCostsBiomeModifier;
import net.penumbra.enderscape.neoforge.level.DefaultEndAmbienceBiomeModifier;

public class EnderscapeBiomeModifierTypes {

    public static final MapCodec<AddFeaturesAlongsideBiomeModifier> ADD_FEATURES_ALONGSIDE = register("add_features_alongside", AddFeaturesAlongsideBiomeModifier.CODEC);
    public static final MapCodec<AddSpawnCostsBiomeModifier> ADD_SPAWN_COSTS = register("add_spawn_costs", AddSpawnCostsBiomeModifier.CODEC);
    public static final MapCodec<DefaultEndAmbienceBiomeModifier> DEFAULT_END_AMBIENCE = register("default_end_ambience", DefaultEndAmbienceBiomeModifier.CODEC);

    private static <T extends BiomeModifier> MapCodec<T> register(String name, MapCodec<T> codec) {
        return Registry.register(NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, Enderscape.id(name), codec);
    }
}
