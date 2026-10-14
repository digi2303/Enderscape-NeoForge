package net.penumbra.enderscape.neoforge.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EnderscapeDynamicRegistrySetup {

    private static final List<Consumer<View>> LISTENERS = new CopyOnWriteArrayList<>();

    private EnderscapeDynamicRegistrySetup() {
    }

    public static void register(Consumer<View> listener) {
        LISTENERS.add(listener);
    }

    public static void setup(Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries) {
        View view = new View(registries);
        LISTENERS.forEach(listener -> listener.accept(view));
    }

    public record View(Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries) {

        @SuppressWarnings("unchecked")
        public <T> Optional<Registry<T>> getOptional(ResourceKey<? extends Registry<? extends T>> key) {
            return Optional.ofNullable((Registry<T>) registries.get(key));
        }
    }
}
