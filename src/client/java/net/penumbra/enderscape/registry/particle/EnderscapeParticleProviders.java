package net.penumbra.enderscape.registry.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.particle.*;
import net.penumbra.enderscape.particle.GlowParticle;
import net.penumbra.enderscape.particle.SnowflakeParticle;
import org.joml.Vector3f;

import static net.penumbra.enderscape.registry.block.EnderscapeFluids.VOID_LACHRYMA;
import static net.penumbra.enderscape.registry.particle.EnderscapeParticles.*;

public class EnderscapeParticleProviders {

    public static final Vector3f DRIFT_JELLY_PARTICLE_COLOR = ARGB.vector3fFromRGB24(0x57C1A5);
    public static final Vector3f VOID_LACHRYMA_PARTICLE_COLOR = new Vector3f(0.18F, 0.0F, 0.27F);

    static {
        EnderscapeNeoForge.modEventBus().addListener(RegisterParticleProvidersEvent.class, event -> {
            event.registerSpriteSet(BLINKLIGHT_SPORES, BlinklightSporesParticle.Provider::new);
            event.registerSpriteSet(CELESTIAL_SPORES, CelestialSporesParticle.Provider::new);
            event.registerSpriteSet(CHORUS_POLLEN, ChorusPollenParticle.Provider::new);
            event.registerSpriteSet(DASH_JUMP_SHOCKWAVE, DashJumpShockwaveParticle.Provider::new);
            event.registerSpriteSet(DASH_JUMP_SPARKS, DashJumpSparksParticle.Provider::new);
            event.registerSpriteSet(DRIPPING_VOID_LACHRYMA, DrippingVoidLachrymaProvider::new);
            event.registerSpriteSet(ENDER_PEARL_ENDERSCAPE, EnderPearlParticle.Provider::new);
            event.registerSpriteSet(ENDER_PEARL_VANILLA, VanillaEnderPearlParticle.Provider::new);
            event.registerSpriteSet(END_PORTAL_STARS, EndPortalStarParticle.Provider::new);
            event.registerSpriteSet(END_TRIAL_SPAWNER_DETECTION, TrialSpawnerDetectionParticle.Provider::new);
            event.registerSpriteSet(END_TRIAL_SPAWNER_EXHALE, EndTrialSpawnerExhaleParticle.Provider::new);
            event.registerSpriteSet(END_VAULT_CONNECTION, FlyTowardsPositionParticle.VaultConnectionProvider::new);
            event.registerSpriteSet(ENTITY_EFFECTED_BY_MAGNIA, EntityAffectedByMagniaParticle.Provider::new);
            event.registerSpriteSet(FALLING_DRIFT_JELLY, DriftJellyFallingParticle.Provider::new);
            event.registerSpriteSet(FALLING_VOID_LACHRYMA, FallingVoidLachrymaProvider::new);
            event.registerSpriteSet(GLOW, GlowParticle.Provider::new);
            event.registerSpriteSet(GLOW_CHARGING, GlowParticle.Provider::new);
            event.registerSpriteSet(LANDING_DRIFT_JELLY, DriftJellyLandParticle.Provider::new);
            event.registerSpriteSet(LANDING_VOID_LACHRYMA, LandingVoidLachrymaProvider::new);
            event.registerSpriteSet(MAGNIA_BLISTERING, MagniaBlisteringParticle.Provider::new);
            event.registerSpriteSet(MAGNIA_SPROUT, MagniaSproutParticle.Provider::new);
            event.registerSpriteSet(MIRROR_TELEPORT_IN, MirrorTeleportInParticle.Provider::new);
            event.registerSpriteSet(MIRROR_TELEPORT_OUT, MirrorTeleportOutParticle.Provider::new);
            event.registerSpriteSet(NEBULITE_ORE, NebuliteOreParticle.Provider::new);
            event.registerSpriteSet(RUSTLE_CONVERTING, MagniaBlisteringParticle.Provider::new);
            event.registerSpriteSet(RUSTLE_SLEEPING_BUBBLE, RustleSleepingBubbleParticle.Provider::new);
            event.registerSpriteSet(RUSTLE_SLEEPING_BUBBLE_POP, RustleSleepingBubblePopParticle.Provider::new);
            event.registerSpriteSet(SNOWFLAKE, SnowflakeParticle.Provider::new);
            event.registerSpriteSet(VEILED_LEAVES, VeiledLeavesParticle.Provider::new);
            event.registerSpriteSet(VOID_ENTITY, EndermanVoidParticle.Provider::new);
            event.registerSpriteSet(VOID_ENTITY_DESTRUCTION, VoidEntityDestructionParticle.Provider::new);
            event.registerSpriteSet(VOID_POOF, VoidPoofParticle.Provider::new);
            event.registerSpriteSet(VOID_SPLASH, SplashParticle.Provider::new);
            event.registerSpriteSet(VOID_STARS, VoidStarParticle.Provider::new);
        });
    }

    public static class DrippingVoidLachrymaProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public DrippingVoidLachrymaProvider(final SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(
                final SimpleParticleType options,
                final ClientLevel level,
                final double x,
                final double y,
                final double z,
                final double xAux,
                final double yAux,
                final double zAux,
                final RandomSource random
        ) {
            return new DripParticle.CoolingDripHangParticle(level, x, y, z, VOID_LACHRYMA, FALLING_VOID_LACHRYMA, sprite.get(random));
        }
    }

    public static class FallingVoidLachrymaProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public FallingVoidLachrymaProvider(final SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(
                final SimpleParticleType options,
                final ClientLevel level,
                final double x,
                final double y,
                final double z,
                final double xAux,
                final double yAux,
                final double zAux,
                final RandomSource random
        ) {
            DripParticle particle = new DripParticle.FallAndLandParticle(level, x, y, z, VOID_LACHRYMA, LANDING_VOID_LACHRYMA, sprite.get(random));
            particle.setColor(
                    VOID_LACHRYMA_PARTICLE_COLOR.x(),
                    VOID_LACHRYMA_PARTICLE_COLOR.y(),
                    VOID_LACHRYMA_PARTICLE_COLOR.z()
            );
            return particle;
        }
    }

    public static class LandingVoidLachrymaProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public LandingVoidLachrymaProvider(final SpriteSet sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(
                final SimpleParticleType options,
                final ClientLevel level,
                final double x,
                final double y,
                final double z,
                final double xAux,
                final double yAux,
                final double zAux,
                final RandomSource random
        ) {
            DripParticle particle = new DripParticle.DripLandParticle(level, x, y, z, VOID_LACHRYMA, sprite.get(random));
            particle.setColor(
                    VOID_LACHRYMA_PARTICLE_COLOR.x(),
                    VOID_LACHRYMA_PARTICLE_COLOR.y(),
                    VOID_LACHRYMA_PARTICLE_COLOR.z()
            );
            return particle;
        }
    }
}