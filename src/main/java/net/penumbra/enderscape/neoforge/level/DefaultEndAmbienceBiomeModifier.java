package net.penumbra.enderscape.neoforge.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ARGB;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.registry.level.EnderscapeBiomes;

import static net.minecraft.world.attribute.EnvironmentAttributes.WATER_FOG_COLOR;

public record DefaultEndAmbienceBiomeModifier(
        HolderSet<Biome> biomes,
        HolderSet<Biome> overridingBiomes,
        Property property
) implements BiomeModifier {

    public static final MapCodec<DefaultEndAmbienceBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(DefaultEndAmbienceBiomeModifier::biomes),
            Biome.LIST_CODEC.fieldOf("overriding_biomes").forGetter(DefaultEndAmbienceBiomeModifier::overridingBiomes),
            Property.CODEC.fieldOf("property").forGetter(DefaultEndAmbienceBiomeModifier::property)
    ).apply(instance, DefaultEndAmbienceBiomeModifier::new));

    @Override
    public void modify(RegistryAccess registries, Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.MODIFY && isEnabled() && biomes.contains(biome) && (isDefault(biome.value()) || overridingBiomes.contains(biome))) {
            apply(builder);
        }
    }

    private boolean isEnabled() {
        EnderscapeConfig config = EnderscapeConfig.getInstance();

        return switch (property) {
            case GRASS_COLOR -> config.ambienceUpdateGrassColors;
            case FOLIAGE_COLOR -> config.ambienceUpdateFoliageColors;
            case WATER_COLOR -> config.ambienceUpdateWaterColors;
            case WATER_FOG_COLOR -> config.ambienceUpdateWaterFogColors;
        };
    }

    private boolean isDefault(Biome biome) {
        return switch (property) {
            case GRASS_COLOR -> biome.getSpecialEffects().grassColorOverride().isEmpty();
            case FOLIAGE_COLOR -> biome.getSpecialEffects().foliageColorOverride().isEmpty();
            case WATER_COLOR -> biome.getWaterColor() == 4159204;
            case WATER_FOG_COLOR -> !biome.getAttributes().contains(WATER_FOG_COLOR) || biome.getAttributes().get(WATER_FOG_COLOR).argument().equals(329011);
        };
    }

    private void apply(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        switch (property) {
            case GRASS_COLOR -> builder.getSpecialEffects().grassColorOverride(EnderscapeBiomes.DEFAULT_GRASS_COLOR);
            case FOLIAGE_COLOR -> builder.getSpecialEffects().foliageColorOverride(EnderscapeBiomes.DEFAULT_FOLIAGE_COLOR);
            case WATER_COLOR -> builder.getSpecialEffects().waterColor(EnderscapeBiomes.DEFAULT_WATER_COLOR);
            case WATER_FOG_COLOR -> builder.getAttributes().set(WATER_FOG_COLOR, ARGB.vector3fFromRGB24(EnderscapeBiomes.DEFAULT_WATER_FOG_COLOR));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }

    public enum Property implements StringRepresentable {
        GRASS_COLOR("grass_color"),
        FOLIAGE_COLOR("foliage_color"),
        WATER_COLOR("water_color"),
        WATER_FOG_COLOR("water_fog_color");

        public static final Codec<Property> CODEC = StringRepresentable.fromEnum(Property::values);

        private final String name;

        Property(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
