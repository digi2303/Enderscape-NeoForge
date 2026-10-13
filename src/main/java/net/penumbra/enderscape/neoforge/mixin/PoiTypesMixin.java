package net.penumbra.enderscape.neoforge.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(PoiTypes.class)
public abstract class PoiTypesMixin {

    @Shadow
    @Final
    private static Map<BlockState, Holder<PoiType>> TYPE_BY_STATE;

    @Inject(method = "registerBlockStates", at = @At("HEAD"), cancellable = true)
    private static void enderscape$skipStatesMappedByRegistryCallback(Holder<PoiType> type, Set<BlockState> matchingStates, CallbackInfo info) {
        if (!matchingStates.isEmpty() && matchingStates.stream().allMatch(state -> TYPE_BY_STATE.get(state) == type)) {
            info.cancel();
        }
    }
}
