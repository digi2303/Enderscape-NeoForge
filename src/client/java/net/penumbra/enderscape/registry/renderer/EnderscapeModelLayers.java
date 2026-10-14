package net.penumbra.enderscape.registry.renderer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.renderer.entity.drifter.DrifterModel;
import net.penumbra.enderscape.renderer.entity.enderman.ImprovedEndermanModel;
import net.penumbra.enderscape.renderer.entity.rubblemite.RubblemiteModel;
import net.penumbra.enderscape.renderer.entity.rustle.BabyRustleModel;
import net.penumbra.enderscape.renderer.entity.rustle.RustleModel;

import java.util.function.Supplier;

public class EnderscapeModelLayers {

    public static final ModelLayerLocation DRIFTER = register("drifter", DrifterModel::createDrifterLayer);
    public static final ModelLayerLocation DRIFTLET = register("driftlet", DrifterModel::createDriftletLayer);
    public static final ModelLayerLocation ENDERMAN = register("enderman", ImprovedEndermanModel::createLayer);
    public static final ModelLayerLocation RUBBLEMITE = register("rubblemite", RubblemiteModel::createLayer);
    public static final ModelLayerLocation RUSTLE = register("rustle", RustleModel::createLayer);
    public static final ModelLayerLocation RUSTLE_BABY = register("rustle_baby", BabyRustleModel::createLayer);

    private static ModelLayerLocation register(String name, Supplier<LayerDefinition> provider) {
        return register(name, "main", provider);
    }

    private static ModelLayerLocation register(String name, String layer, Supplier<LayerDefinition> provider) {
        ModelLayerLocation location = new ModelLayerLocation(Enderscape.id(name), layer);
        EnderscapeNeoForge.modEventBus().addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> event.registerLayerDefinition(location, provider));
        return location;
    }
}