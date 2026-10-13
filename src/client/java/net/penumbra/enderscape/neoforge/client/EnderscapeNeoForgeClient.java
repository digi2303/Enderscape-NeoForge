package net.penumbra.enderscape.neoforge.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.mixin.client.EnderscapeModMenuInvoker;

@Environment(EnvType.CLIENT)
public class EnderscapeNeoForgeClient implements ClientModInitializer {

    private static final String RRV_MOD_ID = "rrv";

    @Override
    public void onInitializeClient() {
        ModContainer container = ModList.get().getModContainerById(Enderscape.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Mod container for " + Enderscape.MOD_ID + " is not registered"));

        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> EnderscapeModMenuInvoker.enderscape$buildMenu(parent));

        if (ModList.get().isLoaded(RRV_MOD_ID)) {
            container.getEventBus().addListener(FMLClientSetupEvent.class, event -> EnderscapeNeoForgeRRV.initialize());
        }
    }
}
