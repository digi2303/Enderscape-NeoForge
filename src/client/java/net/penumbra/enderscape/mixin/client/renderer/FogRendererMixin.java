package net.penumbra.enderscape.mixin.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.Level;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.renderer.level.EnderscapeSkybox;
import net.penumbra.enderscape.renderer.level.VoidLachrymaFogEnvironment;
import net.penumbra.enderscape.renderer.value.EndFlashParameters;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

import static net.penumbra.enderscape.renderer.level.EnderscapeSkybox.scaleWithoutOverflow;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Shadow
    @Final
    private static List<FogEnvironment> FOG_ENVIRONMENTS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void Enderscape$addNewFogEnvironment(CallbackInfo info) {
        FOG_ENVIRONMENTS.addFirst(new VoidLachrymaFogEnvironment());
    }

    @ModifyArgs(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;getFogColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IFFFFLorg/joml/Vector4f;)V"))
    public void Enderscape$getBrightnessDependentFogColor(Args args) {
        ClientLevel level = Minecraft.getInstance().level;
        Vector3f original = new Vector3f(args.get(5), args.get(6), args.get(7));

        if (level != null && level.dimension() == Level.END) {
            if (EnderscapeConfig.getInstance().skyboxUpdateEnabled) {
                float gamma = EnderscapeSkybox.gammaFactor();
                original = scaleWithoutOverflow(original, gamma);
            }

            float brightness = EndFlashParameters.skyboxBrightness();
            original = scaleWithoutOverflow(original, brightness);

            args.set(5, original.x());
            args.set(6, original.y());
            args.set(7, original.z());
        }
    }
}