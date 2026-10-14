package net.penumbra.enderscape.registry.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.*;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;

import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED_SNIPPET;

public class EnderscapeRenderPipelines {

    public static final RenderPipeline IMPROVED_LIGHTMAP = register(
            RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
                    .withLocation(Enderscape.id("pipeline/improved_lightmap"))
                    .withVertexShader("core/screenquad")
                    .withFragmentShader(Enderscape.id("enderscape/improved_lightmap"))
                    .withBindGroupLayout(BindGroupLayouts.LIGHTMAP_INFO)
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build()
    );

    public static final RenderPipeline MIDNIGHT_LIGHTMAP = register(
            RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
                    .withLocation(Enderscape.id("pipeline/midnight_lightmap"))
                    .withVertexShader("core/screenquad")
                    .withFragmentShader(Enderscape.id("enderscape/midnight_lightmap"))
                    .withBindGroupLayout(BindGroupLayouts.LIGHTMAP_INFO)
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build()
    );

    public static final RenderPipeline ALPHA_BASED_OVERLAY = register(
            RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
                    .withLocation(Enderscape.id("pipeline/alpha_based_overlay"))
                    .withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA)))
                    .build()
    );
    public static final RenderPipeline NEBULAE_PIPELINE = register(
            RenderPipeline.builder()
                    .withLocation(Enderscape.id("pipeline/nebulae"))
                    .withVertexShader("core/position_tex")
                    .withFragmentShader("core/position_tex")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
                    .withPrimitiveTopology(PrimitiveTopology.QUADS)
                    .build()
    );

    private static RenderPipeline register(RenderPipeline pipeline) {
        EnderscapeNeoForge.modEventBus().addListener(RegisterRenderPipelinesEvent.class, event -> event.registerPipeline(pipeline));
        return pipeline;
    }
}
