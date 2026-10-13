package net.penumbra.enderscape.neoforge.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.plugins.jei.EnderscapeJEI;

@JeiPlugin
@Environment(EnvType.CLIENT)
public class EnderscapeNeoForgeJEI implements IModPlugin {

    private IModPlugin delegate;
    private IJeiConfigManager pendingConfigManager;

    private IModPlugin delegate() {
        if (delegate == null) {
            delegate = new EnderscapeJEI();

            if (pendingConfigManager != null) {
                delegate.onConfigManagerAvailable(pendingConfigManager);
                pendingConfigManager = null;
            }
        }

        return delegate;
    }

    @Override
    public Identifier getPluginUid() {
        return Enderscape.id("jei");
    }

    @Override
    public void onConfigManagerAvailable(IJeiConfigManager configManager) {
        if (delegate == null) {
            pendingConfigManager = configManager;
        } else {
            delegate.onConfigManagerAvailable(configManager);
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        delegate().registerItemSubtypes(registration);
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        delegate().registerFluidSubtypes(registration, platformFluidHelper);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        delegate().registerIngredients(registration);
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        delegate().registerExtraIngredients(registration);
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        delegate().registerIngredientAliases(registration);
    }

    @Override
    public void registerModInfo(IModInfoRegistration registration) {
        delegate().registerModInfo(registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        delegate().registerCategories(registration);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        delegate().registerVanillaCategoryExtensions(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        delegate().registerRecipes(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        delegate().registerRecipeTransferHandlers(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        delegate().registerRecipeCatalysts(registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        delegate().registerGuiHandlers(registration);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        delegate().registerAdvanced(registration);
    }

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        delegate().registerRuntime(registration);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        delegate().onRuntimeAvailable(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        delegate().onRuntimeUnavailable();
    }
}
