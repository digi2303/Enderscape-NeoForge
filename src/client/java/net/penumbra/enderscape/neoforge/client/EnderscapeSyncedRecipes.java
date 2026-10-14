package net.penumbra.enderscape.neoforge.client;

import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

public final class EnderscapeSyncedRecipes {

    private static RecipeMap recipes = RecipeMap.EMPTY;

    private EnderscapeSyncedRecipes() {
    }

    public static RecipeMap get() {
        return recipes;
    }

    public static void onRecipesReceived(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }
}
