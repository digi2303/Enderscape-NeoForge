package net.penumbra.enderscape.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.penumbra.enderscape.Enderscape;

@Mod(Enderscape.MOD_ID)
public class EnderscapeNeoForge {

    public EnderscapeNeoForge(IEventBus modBus, ModContainer container) {
        Enderscape enderscape = new Enderscape();

        modBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(enderscape::onInitialize));
    }
}
