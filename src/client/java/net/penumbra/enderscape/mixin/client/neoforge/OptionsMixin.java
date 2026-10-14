package net.penumbra.enderscape.mixin.client.neoforge;

import net.minecraft.client.Options;
import net.penumbra.enderscape.neoforge.client.EnderscapeDefaultResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Shadow
    public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void enderscape$enableDefaultResourcePacks(CallbackInfo ci) {
        resourcePacks = EnderscapeDefaultResourcePacks.process(resourcePacks);
    }
}
