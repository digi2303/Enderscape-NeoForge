package net.penumbra.enderscape.mixin.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.renderer.level.EnderscapeSkybox;
import net.penumbra.enderscape.renderer.value.EndFlashParameters;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    @Unique private static final Identifier ENDERSCAPE_END_FLASH = Enderscape.id("end_flash");
    @Unique private EnderscapeSkybox enderscape$skybox;
    @Unique private GpuBuffer enderscape$flashBuffer;

    @Shadow
    private static GpuBuffer buildCelestialQuad(String string, TextureAtlasSprite textureAtlasSprite) {
        return null;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void Enderscape$init(TextureManager textureManager, AtlasManager atlasManager, RenderTarget renderTarget, CallbackInfo ci) {
        enderscape$flashBuffer = buildCelestialQuad("Enderscape flash quad", atlasManager.getAtlasOrThrow(AtlasIds.CELESTIALS).getSprite(ENDERSCAPE_END_FLASH));
        enderscape$skybox = new EnderscapeSkybox();
    }

    @Inject(method = "renderEndSky", at = @At("HEAD"), cancellable = true)
    public void Enderscape$renderEndSky(RenderPass renderPass, CallbackInfo info) {
        Minecraft minecraft = Minecraft.getInstance();

        if (EnderscapeConfig.getInstance().skyboxUpdateEnabled && minecraft.level != null)  {
            enderscape$skybox.render(renderPass, new PoseStack(), minecraft.level, minecraft.gameRenderer.mainCamera(), minecraft.getDeltaTracker());
            info.cancel();
        }
    }

    @ModifyArgs(method = "renderEndFlash", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4f;<init>(FFFF)V"))
    private void Enderscape$changeColor(Args args) {
        if (EndFlashParameters.improved())  {
            float intensity = args.get(3);
            Vector3fc color = EndFlashParameters.getSkyLightColor();

            args.set(0, color.x() * intensity);
            args.set(1, color.y() * intensity);
            args.set(2, color.z() * intensity);
        }
    }

    @ModifyArg(method = "renderEndFlash", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;drawCelestialBody(Ljava/util/function/Supplier;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;I)V"), index = 4)
    private @Nullable GpuBuffer Enderscape$changeEndFlashBuffer(@Nullable GpuBuffer original) {
        return EndFlashParameters.improved() ? enderscape$flashBuffer : original;
    }

    @Inject(method = "close", at = @At("TAIL"))
    public void Enderscape$close(CallbackInfo info) {
        enderscape$flashBuffer.close();
        enderscape$skybox.close();
    }
}