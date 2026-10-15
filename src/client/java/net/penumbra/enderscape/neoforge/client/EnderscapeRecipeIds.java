package net.penumbra.enderscape.neoforge.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;
import java.util.function.BiFunction;

public final class EnderscapeRecipeIds {

    private static final ThreadLocal<String> NAMESPACE = new ThreadLocal<>();

    private EnderscapeRecipeIds() {
    }

    public static MultiRegistryBootstrap inNamespace(String namespace, BiFunction<BootstrapContext<Recipe<?>>, BootstrapContext<Advancement>, ? extends RecipeProvider> factory) {
        MultiRegistryBootstrap bootstrap = RecipeProvider.asBootstrap(factory);

        return new MultiRegistryBootstrap() {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                return bootstrap.requestedRegistries();
            }

            @Override
            public void run(BootstrapGetter registries) {
                NAMESPACE.set(namespace);

                try {
                    bootstrap.run(registries);
                } finally {
                    NAMESPACE.remove();
                }
            }
        };
    }

    public static ResourceKey<Recipe<?>> remap(ResourceKey<Recipe<?>> id) {
        String namespace = NAMESPACE.get();
        return namespace == null ? id : ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(namespace, id.identifier().getPath()));
    }
}
