package net.penumbra.enderscape.neoforge;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class EnderscapeNeoForgeMixinPlugin implements IMixinConfigPlugin {

    private static final String MOD_MENU_ENTRYPOINT = "net.penumbra.enderscape.gui.screens.config.EnderscapeModMenu";
    private static final String MOD_MENU_API = "com/terraformersmc/modmenu/api/ModMenuApi";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClassName.equals(MOD_MENU_ENTRYPOINT) && EnderscapeNeoForgeMixinPlugin.class.getClassLoader().getResource(MOD_MENU_API + ".class") == null) {
            targetClass.interfaces.remove(MOD_MENU_API);
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
