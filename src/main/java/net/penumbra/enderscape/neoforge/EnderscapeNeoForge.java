package net.penumbra.enderscape.neoforge;

import com.google.common.reflect.Reflection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.registry.EnderscapeBuiltInPacks;
import net.penumbra.enderscape.neoforge.registry.EnderscapeRegisterEvents;
import net.penumbra.enderscape.registry.EnderscapeRegistries;

@Mod(Enderscape.MOD_ID)
public class EnderscapeNeoForge {

    private static IEventBus modEventBus;

    public EnderscapeNeoForge(IEventBus modBus, ModContainer container) {
        modEventBus = modBus;

        Reflection.initialize(EnderscapeRegistries.class);

        Enderscape enderscape = new Enderscape();
        Enderscape.registerBuiltInPacks();

        modBus.addListener(RegisterEvent.class, EnderscapeRegisterEvents::register);
        modBus.addListener(AddPackFindersEvent.class, EnderscapeBuiltInPacks::addPackFinders);
        modBus.addListener(FMLCommonSetupEvent.class, event -> event.enqueueWork(enderscape::onInitialize));
    }

    public static IEventBus modEventBus() {
        return modEventBus;
    }
}
