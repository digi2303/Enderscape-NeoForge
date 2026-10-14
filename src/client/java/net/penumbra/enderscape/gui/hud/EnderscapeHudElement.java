package net.penumbra.enderscape.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.penumbra.enderscape.EnderscapeClient;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.manager.ClientsideVariables;

public abstract class EnderscapeHudElement implements GuiLayer {

    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final EnderscapeConfig config = EnderscapeConfig.getInstance();
    protected final ClientsideVariables variables = EnderscapeClient.clientsideVariables();

    private boolean shouldReset;

    public void baseTick() {
        if (shouldTick()) {
            tick();
        }
    }

    public void tick() {
        LocalPlayer player = minecraft.player;

        boolean playerMissing = player == null || player.isDeadOrDying();

        if (shouldReset && !playerMissing) {
            reset();
        }

        shouldReset = playerMissing;
    }

    public boolean shouldTick() {
        return !minecraft.isPaused() && minecraft.level != null && minecraft.player != null;
    }

    public abstract void reset();

    public abstract void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!minecraft.gui.hud.isHidden()) {
            extractRenderState(graphics, deltaTracker);
        }
    }
}