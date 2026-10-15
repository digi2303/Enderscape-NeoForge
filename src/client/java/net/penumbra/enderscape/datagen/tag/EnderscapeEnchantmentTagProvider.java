package net.penumbra.enderscape.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.penumbra.enderscape.Enderscape;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.EnchantmentTags.*;
import static net.minecraft.world.item.enchantment.Enchantments.FEATHER_FALLING;
import static net.penumbra.enderscape.registry.enchantment.EnderscapeEnchantments.*;
import static net.penumbra.enderscape.registry.tag.EnderscapeEnchantmentTags.PREVENTS_DRIFTER_STOMP_DAMAGE;

public class EnderscapeEnchantmentTagProvider extends TagsProvider<Enchantment> {

    public EnderscapeEnchantmentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.ENCHANTMENT, future, Enderscape.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(PREVENTS_DRIFTER_STOMP_DAMAGE).add(FEATHER_FALLING);

        tag(TOOLTIP_ORDER).add(
                REBOUND,
                TRANSDIMENSIONAL,
                BUNDLING,
                STUN_BURST,
                RESONANCE
        );
        tag(IN_ENCHANTING_TABLE).add(BUNDLING, RESONANCE, STUN_BURST);
        tag(NON_TREASURE).add(BUNDLING, RESONANCE, STUN_BURST);
        tag(TREASURE).add(REBOUND, TRANSDIMENSIONAL);
    }
}