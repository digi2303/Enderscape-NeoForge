package net.penumbra.enderscape.neoforge.item;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.Set;

public final class EnderscapeRecipeSynchronization {

    private static final Set<RecipeSerializer<?>> SERIALIZERS = new ReferenceOpenHashSet<>();

    static {
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, EnderscapeRecipeSynchronization::sendRecipes);
    }

    private EnderscapeRecipeSynchronization() {
    }

    public static void synchronizeRecipeSerializer(RecipeSerializer<?> serializer) {
        SERIALIZERS.add(serializer);
    }

    private static void sendRecipes(OnDatapackSyncEvent event) {
        Set<RecipeType<?>> types = new ReferenceOpenHashSet<>();

        for (RecipeHolder<?> holder : event.getPlayerList().getServer().getRecipeManager().getRecipes()) {
            Recipe<?> recipe = holder.value();

            if (SERIALIZERS.contains(recipe.getSerializer())) {
                types.add(recipe.getType());
            }
        }

        event.sendRecipes(types);
    }
}
