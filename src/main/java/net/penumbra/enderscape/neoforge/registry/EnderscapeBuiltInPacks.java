package net.penumbra.enderscape.neoforge.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.resource.JarContentsPackResources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EnderscapeBuiltInPacks {

    private static final List<BuiltInPack> PACKS = new ArrayList<>();

    private EnderscapeBuiltInPacks() {
    }

    public static void register(Identifier name, ModContainer container, Component title, boolean enabledByDefault) {
        PACKS.add(new BuiltInPack(name, container, title, enabledByDefault));
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        for (BuiltInPack pack : PACKS) {
            if (pack.hasResources(event.getPackType())) {
                Pack created = Pack.readMetaAndCreate(pack.location(), pack.resources(), event.getPackType(), new PackSelectionConfig(false, Pack.Position.TOP, false));

                if (created != null) {
                    event.addRepositorySource(consumer -> consumer.accept(created));
                }
            }
        }
    }

    public static List<String> getEnabledByDefault(PackType type) {
        return PACKS.stream().filter(pack -> pack.enabledByDefault() && pack.hasResources(type)).map(pack -> pack.name().toString()).toList();
    }

    private record BuiltInPack(Identifier name, ModContainer container, Component title, boolean enabledByDefault) {

        private JarContentsPackResources.JarContentsResourcesSupplier resources() {
            return new JarContentsPackResources.JarContentsResourcesSupplier(container.getModInfo().getOwningFile().getFile().getContents(), "resourcepacks/" + name.getPath());
        }

        private PackLocationInfo location() {
            String id = name.toString();
            return new PackLocationInfo(id, title, PackSource.create(PackSource.BUILT_IN::decorate, enabledByDefault), Optional.of(new KnownPack("neoforge", id, container.getModInfo().getVersion().toString())));
        }

        private boolean hasResources(PackType type) {
            try (PackResources resources = resources().openMetadata(location())) {
                return !resources.getNamespaces(type).isEmpty();
            }
        }
    }
}
