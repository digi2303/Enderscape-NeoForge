package net.penumbra.enderscape.registry.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.penumbra.enderscape.item.component.value.LodestoneTeleportationVisuals;
import net.penumbra.enderscape.manager.EndHavenManager;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import net.penumbra.enderscape.network.*;

import java.util.function.Predicate;

public class EnderscapeServerNetworking {

    public static void sendLodestoneTeleportationInfoPayload(ServerPlayer player, boolean isDifferentDimension, LodestoneTeleportationVisuals visuals) {
        PacketDistributor.sendToPlayer(player, new ClientboundLodestoneTeleportationInfoPayload(isDifferentDimension, visuals.overlayTexture().asset().texturePath(), visuals.vignetteTexture().asset().texturePath()));
    }

    public static void sendNebuliteOreSoundPayload(ServerLevel world, BlockPos pos) {
        Predicate<ServerPlayer> dimension = (player) -> player.level().dimension() == world.dimension() && player.blockPosition().closerThan(pos, 32);
        world.players().stream().filter((dimension)).forEach((player) -> PacketDistributor.sendToPlayer(player, new ClientboundNebuliteOreSoundPayload(GlobalPos.of(world.dimension(), pos))));
    }

    public static void sendTargetedByEndermanPayload(ServerPlayer player, int endermanId, Boolean playStereoStareSound) {
        PacketDistributor.sendToPlayer(player, new ClientboundTargetedByEndermanPayload(endermanId, playStereoStareSound));
    }

    private static void receiveServerboundRespawnFromEndHavenPayload(ServerboundRespawnFromEndHavenPayload payload, IPayloadContext context) {
        MinecraftServer server = context.player().level().getServer();
        ServerPlayer player = (ServerPlayer) context.player();

        server.execute(() -> EndHavenManager.setPendingRespawn(player));
    }

    private static void receiveServerboundSpawnTotemParticlesPayload(ServerboundSpawnTotemParticlesPayload payload, IPayloadContext context) {
        MinecraftServer server = context.player().level().getServer();
        ServerPlayer player = (ServerPlayer) context.player();

        server.execute(() -> player.level().sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, Items.TOTEM_OF_UNDYING),
                player.getX(),
                player.getY(),
                player.getZ(),
                8,
                0.2,
                0.1,
                0.2,
                0.1
        ));
    }

    static {
        EnderscapeNeoForge.modEventBus().addListener(RegisterPayloadHandlersEvent.class, event -> {
            PayloadRegistrar registrar = event.registrar("1");

            registrar.playToClient(ClientboundDashJumpPayload.TYPE, ClientboundDashJumpPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundDashJumpSoundPayload.TYPE, ClientboundDashJumpSoundPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundHealthVoidPurifySoundPayload.TYPE, ClientboundHealthVoidPurifySoundPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundLodestoneTeleportationInfoPayload.TYPE, ClientboundLodestoneTeleportationInfoPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundNebuliteOreSoundPayload.TYPE, ClientboundNebuliteOreSoundPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundRubbleShieldCooldownSoundPayload.TYPE, ClientboundRubbleShieldCooldownSoundPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundSkipSongPayload.TYPE, ClientboundSkipSongPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundStructureChangedPayload.TYPE, ClientboundStructureChangedPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundSystemMessagePayload.TYPE, ClientboundSystemMessagePayload.STREAM_CODEC);
            registrar.playToClient(ClientboundTargetedByEndermanPayload.TYPE, ClientboundTargetedByEndermanPayload.STREAM_CODEC);
            registrar.playToClient(ClientboundTransdimensionalTravelSoundPayload.TYPE, ClientboundTransdimensionalTravelSoundPayload.STREAM_CODEC);

            registrar.playToServer(ServerboundRespawnFromEndHavenPayload.TYPE, ServerboundRespawnFromEndHavenPayload.STREAM_CODEC, EnderscapeServerNetworking::receiveServerboundRespawnFromEndHavenPayload);
            registrar.playToServer(ServerboundSpawnTotemParticlesPayload.TYPE, ServerboundSpawnTotemParticlesPayload.STREAM_CODEC, EnderscapeServerNetworking::receiveServerboundSpawnTotemParticlesPayload);
        });
    }
}