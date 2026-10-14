package net.penumbra.enderscape.mixin.neoforge;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;
import net.penumbra.enderscape.neoforge.registry.EnderscapeDynamicRegistrySetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {

    @Inject(method = "createContext", at = @At("HEAD"))
    private static void enderscape$setupDynamicRegistries(List<HolderLookup.RegistryLookup<?>> contextRegistries, List<RegistryLoadTask<?>> newRegistriesAndLoaders, CallbackInfoReturnable<RegistryOps.RegistryInfoLookup> info) {
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries = new IdentityHashMap<>();

        for (RegistryLoadTask<?> task : newRegistriesAndLoaders) {
            if (task instanceof ResourceManagerRegistryLoadTask<?>) {
                registries.put(task.registry.key(), task.registry);
            }
        }

        if (!registries.isEmpty()) {
            EnderscapeDynamicRegistrySetup.setup(registries);
        }
    }
}
