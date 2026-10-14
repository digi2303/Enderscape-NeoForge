package net.penumbra.enderscape.mixin.client.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.penumbra.enderscape.neoforge.client.EnderscapeNeoForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftInitMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;", ordinal = 0))
    private void enderscape$initializeClient(GameConfig gameConfig, CallbackInfo ci) {
        EnderscapeNeoForgeClient.onMinecraftInit();
    }
}
