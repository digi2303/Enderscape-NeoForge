package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.penumbra.enderscape.Enderscape;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.effect.MobEffects.ABSORPTION;
import static net.penumbra.enderscape.registry.tag.EnderscapeMobEffectTags.UNSUPPORTED_WITH_VOIDED_HEALTH;

public class EnderscapeMobEffectTagProvider extends TagsProvider<MobEffect> {

    public EnderscapeMobEffectTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.MOB_EFFECT, future, Enderscape.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(UNSUPPORTED_WITH_VOIDED_HEALTH).add(keyFor(ABSORPTION));
    }

    private static ResourceKey<MobEffect> keyFor(Holder<MobEffect> effect) {
        return effect.unwrapKey().get();
    }
}