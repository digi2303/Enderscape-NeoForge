package net.penumbra.enderscape.renderer.level;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.penumbra.enderscape.manager.ClientsideVoidManager;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class VoidLachrymaFogEnvironment extends FogEnvironment {
    private static final Vector3fc BASE_COLOR = ARGB.vector3fFromRGB24(0x06000C);

    @Override
    public Vector3fc getBaseColor(final ClientLevel level, final Camera camera, final int renderDistance, final float partialTicks) {
        return BASE_COLOR;
    }

    @Override
    public void setupFog(final FogData fog, final Camera camera, final ClientLevel level, final float renderDistance, final DeltaTracker tracker) {
        if (camera.entity().isSpectator()) {
            fog.environmentalStart = -8.0F;
            fog.environmentalEnd = renderDistance * 0.5F;
        } else {
            fog.environmentalStart = 0.25F;
            fog.environmentalEnd = 1.0F;
        }

        fog.skyEnd = fog.environmentalEnd;
        fog.cloudEnd = fog.environmentalEnd;
    }

    @Override
    public boolean isApplicable(@Nullable final FogType fogType, final Entity entity) {
        return ClientsideVoidManager.cameraInVoidLachryma();
    }
}