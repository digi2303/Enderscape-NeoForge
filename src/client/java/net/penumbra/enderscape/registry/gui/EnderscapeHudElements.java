package net.penumbra.enderscape.registry.gui;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.gui.hud.*;
import net.penumbra.enderscape.gui.value.RenderPhase;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;

import java.util.ArrayList;
import java.util.List;

public class EnderscapeHudElements {

    public static final List<EnderscapeHudElement> HUD_ELEMENTS = new ArrayList<>();

    public static final EnderscapeHudElement LODESTONE_TELEPORTATION_SCREEN_EFFECT = create(
            "lodestone_teleportation_screen_effect",
            VanillaGuiLayers.CAMERA_OVERLAYS,
            RenderPhase.BEFORE,
            new LodestoneTeleportationScreenEffect()
    );

    public static final EnderscapeHudElement VOID_NOISE_SCREEN_EFFECT = create(
            "void_noise_screen_effect",
            VanillaGuiLayers.CAMERA_OVERLAYS,
            RenderPhase.BEFORE,
            new VoidNoiseScreenEffect()
    );

    public static final EnderscapeHudElement OUTER_VOID_WARNING_SCREEN_EFFECT = create(
            "outer_void_warning_screen_effect",
            VanillaGuiLayers.CAMERA_OVERLAYS,
            RenderPhase.BEFORE,
            new OuterVoidWarningScreenEffect()
    );

    public static final EnderscapeHudElement FUELED_TOOL_HUD = create(
            "fueled_tool_hud",
            VanillaGuiLayers.BOSS_OVERLAY,
            RenderPhase.AFTER,
            new FueledToolHud()
    );

    private static EnderscapeHudElement create(String name, Identifier vanillaElement, RenderPhase phase, EnderscapeHudElement element) {
        HUD_ELEMENTS.add(element);

        EnderscapeNeoForge.modEventBus().addListener(RegisterGuiLayersEvent.class, event -> {
            switch (phase) {
                case BEFORE -> event.registerBelow(
                        vanillaElement,
                        Enderscape.id(name),
                        element
                );
                case AFTER -> event.registerAbove(
                        vanillaElement,
                        Enderscape.id(name),
                        element
                );
            }
        });

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> element.baseTick());

        return element;
    }
}