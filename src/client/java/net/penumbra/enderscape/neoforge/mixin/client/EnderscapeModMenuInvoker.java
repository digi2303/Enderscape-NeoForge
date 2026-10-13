package net.penumbra.enderscape.neoforge.mixin.client;

import net.minecraft.client.gui.screens.Screen;
import net.penumbra.enderscape.gui.screens.config.EnderscapeModMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EnderscapeModMenu.class)
public interface EnderscapeModMenuInvoker {

    @Invoker("buildMenu")
    static Screen enderscape$buildMenu(Screen parent) {
        throw new AssertionError();
    }
}
