package net.penumbra.enderscape.mixin.neoforge;

import com.google.common.base.Suppliers;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.penumbra.enderscape.neoforge.level.EnderscapeEndBiomes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin {

    @Shadow
    @Final
    @Mutable
    public static MapCodec<TheEndBiomeSource> CODEC;

    @Unique
    private Supplier<EnderscapeEndBiomes.Overrides> enderscape$overrides;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void enderscape$retrieveBiomeGetter(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(RegistryOps.retrieveGetter(Registries.BIOME)).apply(instance, instance.stable(TheEndBiomeSource::create)));
    }

    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private static TheEndBiomeSource enderscape$createOverrides(TheEndBiomeSource source, HolderGetter<Biome> biomes) {
        ((TheEndBiomeSourceMixin) (Object) source).enderscape$overrides = Suppliers.memoize(() -> EnderscapeEndBiomes.createOverrides(biomes));
        return source;
    }

    @ModifyReturnValue(method = "getNoiseBiome", at = @At("RETURN"))
    private Holder<Biome> enderscape$pickEndBiome(Holder<Biome> original, int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        return enderscape$overrides.get().pick(quartX, quartZ, sampler, original);
    }

    @ModifyReturnValue(method = "collectPossibleBiomes", at = @At("RETURN"))
    private Stream<Holder<Biome>> enderscape$addEndBiomes(Stream<Holder<Biome>> original) {
        return Stream.concat(original, enderscape$overrides.get().customBiomes().stream());
    }
}
