package net.penumbra.enderscape.neoforge.level;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class EnderscapeEndBiomes {

    private static final Set<ResourceKey<Biome>> ADDED_BIOMES = new LinkedHashSet<>();
    private static final Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> END_BIOMES = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> END_MIDLANDS = new IdentityHashMap<>();
    private static final Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> END_BARRENS = new IdentityHashMap<>();

    static {
        add(END_BIOMES, Biomes.THE_END, Biomes.THE_END, 1.0);
        add(END_BIOMES, Biomes.END_HIGHLANDS, Biomes.END_HIGHLANDS, 1.0);
        add(END_BIOMES, Biomes.SMALL_END_ISLANDS, Biomes.SMALL_END_ISLANDS, 1.0);
        add(END_MIDLANDS, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, 1.0);
        add(END_BARRENS, Biomes.END_HIGHLANDS, Biomes.END_BARRENS, 1.0);
    }

    private EnderscapeEndBiomes() {
    }

    public static void addHighlandsBiome(ResourceKey<Biome> biome, double weight) {
        addModded(END_BIOMES, Biomes.END_HIGHLANDS, biome, weight);
    }

    public static void addMidlandsBiome(ResourceKey<Biome> highlands, ResourceKey<Biome> midlands, double weight) {
        addModded(END_MIDLANDS, highlands, midlands, weight);
    }

    public static void addBarrensBiome(ResourceKey<Biome> highlands, ResourceKey<Biome> barrens, double weight) {
        addModded(END_BARRENS, highlands, barrens, weight);
    }

    public static Overrides createOverrides(HolderGetter<Biome> biomes) {
        return new Overrides(biomes);
    }

    private static void addModded(Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> map, ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double weight) {
        Preconditions.checkNotNull(replaced, "replaced entry is null");
        Preconditions.checkNotNull(variant, "variant entry is null");
        Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
        add(map, replaced, variant, weight);
        ADDED_BIOMES.add(variant);
    }

    private static void add(Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> map, ResourceKey<Biome> replaced, ResourceKey<Biome> variant, double weight) {
        map.computeIfAbsent(replaced, key -> new WeightedPicker<>()).add(variant, weight);
    }

    public static final class Overrides {

        private final Set<Holder<Biome>> customBiomes;
        private final @Nullable Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> endBiomes;
        private final @Nullable Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> endMidlands;
        private final @Nullable Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> endBarrens;
        private final Holder<Biome> highlands;

        private Overrides(HolderGetter<Biome> biomes) {
            Set<Holder<Biome>> custom = new LinkedHashSet<>();
            ADDED_BIOMES.forEach(key -> custom.add(biomes.getOrThrow(key)));
            this.customBiomes = Collections.unmodifiableSet(custom);
            this.endBiomes = resolve(biomes, END_BIOMES, Biomes.THE_END);
            this.endMidlands = resolve(biomes, END_MIDLANDS, Biomes.END_MIDLANDS);
            this.endBarrens = resolve(biomes, END_BARRENS, Biomes.END_BARRENS);
            this.highlands = biomes.getOrThrow(Biomes.END_HIGHLANDS);
        }

        public Set<Holder<Biome>> customBiomes() {
            return customBiomes;
        }

        public Holder<Biome> pick(int quartX, int quartZ, Climate.Sampler sampler, Holder<Biome> vanilla) {
            boolean isMidlands = vanilla.is(Biomes.END_MIDLANDS);

            if (isMidlands || vanilla.is(Biomes.END_BARRENS)) {
                Holder<Biome> highlandsReplacement = pick(Biomes.END_HIGHLANDS, highlands, endBiomes, quartX, quartZ, sampler);
                return pick(highlandsReplacement.unwrapKey().orElseThrow(), vanilla, isMidlands ? endMidlands : endBarrens, quartX, quartZ, sampler);
            }

            ResourceKey<Biome> key = vanilla.unwrapKey().orElseThrow();

            if (!END_BIOMES.containsKey(key)) {
                throw new IllegalStateException("Biome is not an End biome: " + vanilla);
            }

            return pick(key, vanilla, endBiomes, quartX, quartZ, sampler);
        }

        private static Holder<Biome> pick(ResourceKey<Biome> key, Holder<Biome> fallback, @Nullable Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> pickers, int quartX, int quartZ, Climate.Sampler sampler) {
            if (pickers == null) return fallback;

            WeightedPicker<Holder<Biome>> picker = pickers.get(key);
            if (picker == null) return fallback;

            int count = picker.size();
            if (count == 0 || (count == 1 && key == Biomes.END_HIGHLANDS)) return fallback;

            return picker.pick(((EndBiomeNoiseSampler) (Object) sampler).enderscape$getEndBiomeNoise(), quartX / 64.0, 0, quartZ / 64.0);
        }

        private static @Nullable Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> resolve(HolderGetter<Biome> biomes, Map<ResourceKey<Biome>, WeightedPicker<ResourceKey<Biome>>> pickers, ResourceKey<Biome> vanillaKey) {
            Map<ResourceKey<Biome>, WeightedPicker<Holder<Biome>>> result = new IdentityHashMap<>();

            pickers.forEach((key, picker) -> {
                int count = picker.size();
                if (count == 0 || (count == 1 && key == vanillaKey)) return;
                result.put(key, picker.map(biomes::getOrThrow));
            });

            return result.isEmpty() ? null : result;
        }
    }

    private static final class WeightedPicker<T> {

        private final List<Entry<T>> entries;
        private double total;

        private WeightedPicker() {
            this(new ArrayList<>(), 0);
        }

        private WeightedPicker(List<Entry<T>> entries, double total) {
            this.entries = entries;
            this.total = total;
        }

        private void add(T value, double weight) {
            total += weight;
            entries.add(new Entry<>(value, total));
        }

        private int size() {
            return entries.size();
        }

        private <U> WeightedPicker<U> map(Function<T, U> mapper) {
            return new WeightedPicker<>(entries.stream().map(entry -> new Entry<>(mapper.apply(entry.value()), entry.upperBound())).toList(), total);
        }

        private T pick(PerlinNoise noise, double x, double y, double z) {
            double target = Mth.clamp(Math.abs(noise.get(x, y, z)), 0, 1) * total;
            int low = 0;
            int high = entries.size() - 1;

            while (low < high) {
                int mid = (high + low) >>> 1;

                if (target < entries.get(mid).upperBound()) {
                    high = mid;
                } else {
                    low = mid + 1;
                }
            }

            return entries.get(low).value();
        }

        private record Entry<T>(T value, double upperBound) {
        }
    }
}
