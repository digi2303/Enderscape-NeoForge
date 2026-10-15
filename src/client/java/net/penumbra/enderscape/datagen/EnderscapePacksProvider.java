package net.penumbra.enderscape.datagen;

import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.ResourceFilterSection;
import net.minecraft.util.IdentifierPattern;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.PackGenerator;
import net.penumbra.enderscape.registry.EnderscapePacks;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EnderscapePacksProvider {
    static void initialize(GatherDataEvent generator) {
        createPack(generator, PackType.CLIENT_RESOURCES, EnderscapePacks.IMPROVED_VISUALS);

        createPack(generator, PackType.SERVER_DATA, EnderscapePacks.FIX_LEVITATION_ADVANCEMENT);
        createPack(generator, EnderscapePacks.FIX_VANILLA_RECIPES, fixVanillaRecipesMetadata());

        createPack(generator, PackType.SERVER_DATA, EnderscapePacks.NEW_END_CITIES);
        createPack(generator, PackType.SERVER_DATA, EnderscapePacks.NEW_STRONGHOLDS);
        createPack(generator, PackType.SERVER_DATA, EnderscapePacks.NEW_TERRAIN);
    }

    private static PackGenerator createPack(GatherDataEvent generator, PackType type, Identifier identifier) {
        PackGenerator pack = generator.getPackGenerator(generator.getGenerator().getPackOutput("resourcepacks/" + identifier.getPath()));
        pack.createProvider(createPackMetadata(type, identifier));
        return pack;
    }

    private static PackGenerator createPack(GatherDataEvent generator, Identifier identifier, PackGenerator.DataProviderFromOutput<PackMetadataGenerator> packMetadata) {
        PackGenerator pack = generator.getPackGenerator(generator.getGenerator().getPackOutput("resourcepacks/" + identifier.getPath()));
        pack.createProvider(packMetadata);
        return pack;
    }

    private static PackGenerator.DataProviderFromOutput<PackMetadataGenerator> createPackMetadata(PackType type, Identifier identifier) {
        return output -> {
            PackMetadataGenerator metadata = new PackMetadataGenerator(output);

            metadata.add(PackMetadataSection.forPackType(type), new PackMetadataSection(
                    Component.translatable(String.format("pack.%s.%s.description", identifier.getNamespace(), identifier.getPath())),
                    DetectedVersion.BUILT_IN.packVersion(type).minorRange()
            ));

            return metadata;
        };
    }

    private static PackGenerator.DataProviderFromOutput<PackMetadataGenerator> fixVanillaRecipesMetadata() {
        return output -> {
            PackMetadataGenerator metadata = new PackMetadataGenerator(output);

            Identifier identifier = EnderscapePacks.FIX_VANILLA_RECIPES;
            PackType type = PackType.SERVER_DATA;

            metadata.add(PackMetadataSection.forPackType(type), new PackMetadataSection(
                    Component.translatable(String.format("pack.%s.%s.description", identifier.getNamespace(), identifier.getPath())),
                    DetectedVersion.BUILT_IN.packVersion(type).minorRange()
            ));

            metadata.add(ResourceFilterSection.TYPE, new ResourceFilterSection(List.of(
                    new IdentifierPattern(Optional.of(Pattern.compile("minecraft")), Optional.of(Pattern.compile("recipe/end_stone_bricks\\.json"))),
                    new IdentifierPattern(Optional.of(Pattern.compile("minecraft")), Optional.of(Pattern.compile("advancement/recipes/building_blocks/end_stone_bricks\\.json")))
            )));

            return metadata;
        };
    }
}
