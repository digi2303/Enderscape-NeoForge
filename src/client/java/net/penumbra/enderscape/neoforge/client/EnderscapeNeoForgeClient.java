package net.penumbra.enderscape.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.EnderscapeClient;
import net.penumbra.enderscape.gui.screens.config.EnderscapeModMenu;

@Mod(value = Enderscape.MOD_ID, dist = Dist.CLIENT)
public class EnderscapeNeoForgeClient {

    private static EnderscapeClient enderscapeClient;

    public EnderscapeNeoForgeClient(ModContainer container) {
        enderscapeClient = new EnderscapeClient();

        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> EnderscapeModMenu.buildMenu(parent));
        NeoForge.EVENT_BUS.addListener(RecipesReceivedEvent.class, EnderscapeSyncedRecipes::onRecipesReceived);
    }

    public static void onMinecraftInit() {
        enderscapeClient.onInitializeClient();
    }
}
