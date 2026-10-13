package net.penumbra.enderscape.neoforge.client;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.penumbra.enderscape.Enderscape;

@Environment(EnvType.CLIENT)
public final class EnderscapeNeoForgeRRV {

    private static final String ENTRYPOINT = "rrv_client";

    private EnderscapeNeoForgeRRV() {
    }

    public static void initialize() {
        for (EntrypointContainer<ReliableRecipeViewerClientPlugin> entrypoint : FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT, ReliableRecipeViewerClientPlugin.class)) {
            if (entrypoint.getProvider().getMetadata().getId().equals(Enderscape.MOD_ID)) {
                entrypoint.getEntrypoint().onIntegrationInitialize();
                Enderscape.LOGGER.info("Initialized {} entrypoint {}", ENTRYPOINT, entrypoint.getDefinition());
            }
        }
    }
}
