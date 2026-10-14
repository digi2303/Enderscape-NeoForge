package net.penumbra.enderscape.registry.gui;

import net.minecraft.client.gui.components.debug.DebugEntryNoop;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.gui.debug.MusicDebugEntry;
import net.penumbra.enderscape.gui.debug.PlayerAttachmentsDebugEntry;
import net.penumbra.enderscape.gui.debug.PlayerSpeedDebugEntry;
import net.penumbra.enderscape.gui.debug.ServerStructureMusicDebugEntry;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;

public class EnderscapeDebugScreenEntries {
    public static final Identifier MUSIC = register(Enderscape.id("music"), new MusicDebugEntry());
    public static final Identifier PLAYER_SPEED = register(Enderscape.id("player_speed"), new PlayerSpeedDebugEntry());
    public static final Identifier SERVER_STRUCTURE_MUSIC = register(Enderscape.id("server_structure_music"), new ServerStructureMusicDebugEntry());
    public static final Identifier PLAYER_ATTACHMENTS = register(Enderscape.id("player_attachments"), new PlayerAttachmentsDebugEntry());

    public static final Identifier MAGNIA_SPROUT_RANGE = register(Enderscape.id("magnia_sprout_range"), new DebugEntryNoop());

    private static Identifier register(Identifier id, DebugScreenEntry entry) {
        EnderscapeNeoForge.modEventBus().addListener(RegisterDebugEntriesEvent.class, event -> event.register(id, entry));
        return id;
    }
}
