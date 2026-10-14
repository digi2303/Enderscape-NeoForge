package net.penumbra.enderscape.mixin.client;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicManager.class)
public interface MusicManagerAccess {

    @Accessor
    int getNextSongDelay();
}