package net.penumbra.enderscape.mixin.client.renderer.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.penumbra.enderscape.renderer.VoidTicksPercentage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements VoidTicksPercentage {

    @Unique
    private float voidTicksPercentage;

    @Override
    public void setVoidTicksPercentage(float value) {
        voidTicksPercentage = value;
    }

    @Override
    public float getVoidTicksPercentage() {
        return voidTicksPercentage;
    }
}