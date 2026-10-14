package net.penumbra.enderscape.neoforge.client;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.loading.FMLPaths;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.registry.EnderscapeBuiltInPacks;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class EnderscapeDefaultResourcePacks {

    private static final Path TRACKER_FILE = FMLPaths.GAMEDIR.get().resolve("data").resolve("enderscape_default_resource_packs.json");
    private static final Codec<List<String>> CODEC = Codec.STRING.listOf().fieldOf("values").codec();

    private EnderscapeDefaultResourcePacks() {
    }

    public static List<String> process(List<String> resourcePacks) {
        Set<String> tracked = new LinkedHashSet<>(read());
        Set<String> removed = new HashSet<>(tracked);
        Set<String> result = new LinkedHashSet<>(resourcePacks);

        for (String id : EnderscapeBuiltInPacks.getEnabledByDefault(PackType.CLIENT_RESOURCES)) {
            if (tracked.add(id)) {
                result.add(id);
            } else {
                removed.remove(id);
            }
        }

        tracked.removeAll(removed);
        write(tracked);

        return new ArrayList<>(result);
    }

    private static List<String> read() {
        if (Files.notExists(TRACKER_FILE)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(TRACKER_FILE)) {
            return CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).getOrThrow();
        } catch (IOException | RuntimeException e) {
            Enderscape.LOGGER.warn("Could not read {}", TRACKER_FILE.toAbsolutePath(), e);
            return List.of();
        }
    }

    private static void write(Set<String> values) {
        try {
            Files.createDirectories(TRACKER_FILE.getParent());
            Files.writeString(TRACKER_FILE, CODEC.encodeStart(JsonOps.INSTANCE, List.copyOf(values)).getOrThrow().toString());
        } catch (IOException | RuntimeException e) {
            Enderscape.LOGGER.warn("Could not write {}", TRACKER_FILE.toAbsolutePath(), e);
        }
    }
}
