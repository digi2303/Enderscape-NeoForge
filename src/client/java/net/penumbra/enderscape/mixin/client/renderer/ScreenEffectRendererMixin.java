package net.penumbra.enderscape.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.penumbra.enderscape.EnderscapeClient;
import net.penumbra.enderscape.manager.ClientsideVariables;
import net.penumbra.enderscape.network.ServerboundSpawnTotemParticlesPayload;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @Unique
    private Vector3f previousTranslation;

    @Unique
    private float previousAngleY;

    @Unique
    private float previousAngleX;

    @Unique
    private float previousAngleZ;

    @Unique
    private static final int TOTEM_GLITCH_TIMESTAMP = 20;

    @Unique
    private static final int TOTEM_SHATTER_TIMESTAMP = 12;

    @Unique
    private float glitchTime;

    @Unique
    private final RandomSource random = RandomSource.create();

    @WrapOperation(method = "renderItemActivationAnimation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 0))
    public void Enderscape$changeTotemTranslation(PoseStack pose, float x, float y, float z, Operation<Void> original,
                                                  @Local(argsOnly = true, name = "playerRenderState") PlayerRenderState playerRenderState,
                                                  @Local(argsOnly = true, name = "partialTicks") float partialTicks) {
        var itemActivationTicks = playerRenderState.itemActivation != null ? playerRenderState.itemActivation.ticks : 0;
        glitchTime += partialTicks;

        if (EnderscapeClient.clientsideVariables().displayVoidedTotemEffect) {
            if (itemActivationTicks <= TOTEM_SHATTER_TIMESTAMP) {
                original.call(pose, 0.0F, 0.0F, -9999.0F);

                if (itemActivationTicks == TOTEM_SHATTER_TIMESTAMP)
                    ClientPacketDistributor.sendToServer(new ServerboundSpawnTotemParticlesPayload());

                return;
            } else if (itemActivationTicks <= TOTEM_GLITCH_TIMESTAMP) {
                if (glitchTime > 0.1F) {
                    glitchTime = 0.0F;

                    float rx = previousTranslation.x + range(random, 0.05F);
                    float ry = previousTranslation.y + (range(random, 0.025F));
                    original.call(pose, rx, ry, previousTranslation.z);

                } else {
                    original.call(pose, previousTranslation.x, previousTranslation.y, previousTranslation.z);

                }
                return;
            }
        }

        original.call(pose, x, y, z);
        previousTranslation = new Vector3f(x, y, z);
    }

    @WrapOperation(method = "renderItemActivationAnimation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V", ordinal = 0))
    public void Enderscape$changeTotemRotationYP(PoseStack pose, Axis axis, float angle, Operation<Void> original,
                                                 @Local(argsOnly = true, name = "playerRenderState") PlayerRenderState playerRenderState) {
        boolean voided = EnderscapeClient.clientsideVariables().displayVoidedTotemEffect && playerRenderState.itemActivation != null && playerRenderState.itemActivation.ticks <= TOTEM_GLITCH_TIMESTAMP;
        if (!voided) previousAngleY = angle;
        original.call(pose, axis, voided ? previousAngleY : angle);
    }

    @WrapOperation(method = "renderItemActivationAnimation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V", ordinal = 1))
    public void Enderscape$changeTotemRotationXP(PoseStack pose, Axis axis, float angle, Operation<Void> original,
                                                 @Local(argsOnly = true, name = "playerRenderState") PlayerRenderState playerRenderState) {
        boolean voided = EnderscapeClient.clientsideVariables().displayVoidedTotemEffect && playerRenderState.itemActivation != null && playerRenderState.itemActivation.ticks  <= TOTEM_GLITCH_TIMESTAMP;
        if (!voided) previousAngleX = angle;
        original.call(pose, axis, voided ? previousAngleX : angle);
    }

    @WrapOperation(method = "renderItemActivationAnimation", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V", ordinal = 2))
    public void Enderscape$changeTotemRotationZP(PoseStack pose, Axis axis, float angle, Operation<Void> original,
                                                 @Local(argsOnly = true, name = "playerRenderState") PlayerRenderState playerRenderState) {
        boolean voided = EnderscapeClient.clientsideVariables().displayVoidedTotemEffect && playerRenderState.itemActivation != null && playerRenderState.itemActivation.ticks  <= TOTEM_GLITCH_TIMESTAMP;
        if (!voided) previousAngleZ = angle;
        original.call(pose, axis, voided ? previousAngleZ : angle);
    }

    @Unique
    private static float range(RandomSource random, float range) {
        return Mth.nextFloat(random, -range, range);
    }
}