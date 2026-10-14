package net.penumbra.enderscape.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.EnderscapeClient;
import net.penumbra.enderscape.gui.screens.config.EnderscapeModMenu;

@Mod(value = Enderscape.MOD_ID, dist = Dist.CLIENT)
public class EnderscapeNeoForgeClient {

    public EnderscapeNeoForgeClient(IEventBus modBus, ModContainer container) {
        EnderscapeClient enderscapeClient = new EnderscapeClient();

        modBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(enderscapeClient::onInitializeClient));

        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> EnderscapeModMenu.buildMenu(parent));
    }
}
