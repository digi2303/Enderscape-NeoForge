package net.penumbra.enderscape.mixin.client.neoforge;

import net.minecraft.data.recipes.CustomCraftingRecipeBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.penumbra.enderscape.neoforge.client.EnderscapeRecipeIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({
        SimpleCookingRecipeBuilder.class,
        ShapedRecipeBuilder.class,
        ShapelessRecipeBuilder.class,
        SingleItemRecipeBuilder.class,
        TransmuteRecipeBuilder.class,
        CustomCraftingRecipeBuilder.class,
        SpecialRecipeBuilder.class,
        SmithingTransformRecipeBuilder.class,
        SmithingTrimRecipeBuilder.class
})
public abstract class RecipeBuilderIdMixin {

    @ModifyVariable(method = "save(Lnet/minecraft/data/recipes/RecipeOutput;Lnet/minecraft/resources/ResourceKey;)V", at = @At("HEAD"), argsOnly = true)
    private ResourceKey<Recipe<?>> enderscape$remapRecipeId(ResourceKey<Recipe<?>> id) {
        return EnderscapeRecipeIds.remap(id);
    }
}
