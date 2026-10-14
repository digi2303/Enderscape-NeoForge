package net.penumbra.enderscape;

import com.google.common.reflect.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.penumbra.enderscape.gui.tooltip.FueledToolTooltip;
import net.penumbra.enderscape.item.tooltip.FueledToolComponent;
import net.penumbra.enderscape.manager.ClientsideLodestoneTeleportationManager;
import net.penumbra.enderscape.manager.ClientsideVariables;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.registry.block.EnderscapeBlockEntities;
import net.penumbra.enderscape.registry.block.EnderscapeBlockTintSources;
import net.penumbra.enderscape.registry.block.EnderscapeFluids;
import net.penumbra.enderscape.registry.entity.EnderscapeEntityRenderers;
import net.penumbra.enderscape.registry.entity.EnderscapeMobEffects;
import net.penumbra.enderscape.registry.gui.EnderscapeDebugScreenEntries;
import net.penumbra.enderscape.registry.gui.EnderscapeHudElements;
import net.penumbra.enderscape.registry.networking.EnderscapeClientNetworking;
import net.penumbra.enderscape.registry.particle.EnderscapeParticleProviders;
import net.penumbra.enderscape.registry.renderer.EnderscapeModelLayers;
import net.penumbra.enderscape.registry.renderer.EnderscapeRenderPipelines;
import net.penumbra.enderscape.renderer.block.EndHavenCoreRenderer;
import net.penumbra.enderscape.renderer.block.MagniaSproutRenderer;

public class EnderscapeClient {

    private static final ClientsideVariables CLIENTSIDE_VARIABLES = new ClientsideVariables();

    public static ClientsideVariables clientsideVariables() {
        return CLIENTSIDE_VARIABLES;
    }

    public void onInitializeClient() {

        Reflection.initialize(
                EnderscapeClientNetworking.class,
                EnderscapeDebugScreenEntries.class,
                EnderscapeRenderPipelines.class,
                EnderscapeParticleProviders.class,
                EnderscapeModelLayers.class,
                EnderscapeEntityRenderers.class,
                EnderscapeBlockTintSources.class,
                EnderscapeHudElements.class
        );

        BlockEntityRenderers.register(EnderscapeBlockEntities.MAGNIA_SPROUT, MagniaSproutRenderer::new);
        BlockEntityRenderers.register(EnderscapeBlockEntities.END_HAVEN_CORE, EndHavenCoreRenderer::new);

        EnderscapeNeoForge.modEventBus().addListener(RegisterClientTooltipComponentFactoriesEvent.class, event -> event.register(FueledToolComponent.class, component -> new FueledToolTooltip(component.stack())));
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, event -> ClientsideLodestoneTeleportationManager.appendTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));

        EnderscapeNeoForge.modEventBus().addListener(RegisterFluidModelsEvent.class, event -> event.register(
                new FluidModel.Unbaked(
                        new Material(Enderscape.id("block/void_lachryma_still")),
                        new Material(Enderscape.id("block/void_lachryma_flow")),
                        null,
                        EnderscapeBlockTintSources.voidLachryma()
                ),
                EnderscapeFluids.VOID_LACHRYMA,
                EnderscapeFluids.FLOWING_VOID_LACHRYMA
        ));

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;

            if (EnderscapeMobEffects.isStunned(player) && minecraft.gui.screen() instanceof AbstractContainerScreen<?>) {
                player.closeContainer();
            }
        });
    }
}