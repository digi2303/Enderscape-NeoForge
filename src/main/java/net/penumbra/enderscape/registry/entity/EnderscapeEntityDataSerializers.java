package net.penumbra.enderscape.registry.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.entity.rubblemite.Rubblemite;
import net.penumbra.enderscape.entity.rubblemite.RubblemiteVariant;
import net.penumbra.enderscape.entity.rustle.RustleConversionPhase;
import net.penumbra.enderscape.item.crafting.RustleRecipe;

import java.util.Optional;

public class EnderscapeEntityDataSerializers {

    public static final EntityDataSerializer<Rubblemite.State> RUBBLEMITE_STATE = EntityDataSerializer.forValueType(Rubblemite.State.STREAM_CODEC);
    public static final EntityDataSerializer<Holder<RubblemiteVariant>> RUBBLEMITE_VARIANT = EntityDataSerializer.forValueType(RubblemiteVariant.STREAM_CODEC);
    public static final EntityDataSerializer<RustleConversionPhase> RUSTLE_CONVERSION_PHASE = EntityDataSerializer.forValueType(RustleConversionPhase.STREAM_CODEC);
    public static final EntityDataSerializer<Optional<RustleRecipe>> OPTIONAL_RUSTLE_RECIPE = EntityDataSerializer.forValueType(RustleRecipe.STREAM_CODEC.apply(ByteBufCodecs::optional));

    static {
        Registry.register(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Enderscape.id("rubblemite_state"), RUBBLEMITE_STATE);
        Registry.register(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Enderscape.id("rubblemite_variant"), RUBBLEMITE_VARIANT);
        Registry.register(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Enderscape.id("rustle_conversion_phase"), RUSTLE_CONVERSION_PHASE);
        Registry.register(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Enderscape.id("optional_rustle_recipe"), OPTIONAL_RUSTLE_RECIPE);
    }
}