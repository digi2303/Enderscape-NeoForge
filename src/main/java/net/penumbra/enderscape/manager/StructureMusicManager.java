package net.penumbra.enderscape.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.penumbra.enderscape.network.ClientboundStructureChangedPayload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
    Credit to FusionSwarly for developing the original functionality
    https://github.com/FusionSwarly/structure-music
 */
public class StructureMusicManager {

    private static final Identifier NONE = Identifier.withDefaultNamespace("none");
    private static final int PLAYER_TIMER_DELAY = 60 * 20;
    private static final int STRUCTURE_CHANGE_TIMER_DELAY = 60;

    public static final Map<ServerPlayer, Integer> playerTimers = new HashMap<>();
    public static final Map<ServerPlayer, Identifier> playerStructures = new HashMap<>();

    public static int structureChangeTimer = 0;

    private static void tryStructureChange(ServerPlayer player, Identifier structure) {
        Identifier currentStructure = playerStructures.get(player);

        if (currentStructure == null || !currentStructure.equals(structure)) {
            playerStructures.put(player, structure);
            playerTimers.put(player, PLAYER_TIMER_DELAY);
        }
    }

    private static Identifier getStructure(ServerLevel level, ServerPlayer player) {
        if (level != null && player != null) {
            StructureStart start = level.structureManager().getStructureWithPieceAt(BlockPos.containing(player.position()), _ -> true);

            if (start.isValid()) {
                Identifier key = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getKey(start.getStructure());
                if (key != null) return key;
            }
        }

        return NONE;
    }

    private static void sendStructureToClient(ServerPlayer player, Identifier structure) {
        if (player != null) {
            PacketDistributor.sendToPlayer(player, new ClientboundStructureChangedPayload(structure));
        }
    }

    private static void removeFromAll(ServerPlayer player) {
        sendStructureToClient(player, NONE);
        playerStructures.remove(player);
        playerTimers.remove(player);
    }

    static {
        NeoForge.EVENT_BUS.addListener(LevelEvent.Unload.class, (event) -> {
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            for (ServerPlayer player : level.players()) {
                removeFromAll(player);
            }
        });

        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Pre.class, (event) -> {
            MinecraftServer server = event.getServer();

            if (!playerTimers.isEmpty()) {
                Iterator<Map.Entry<ServerPlayer, Integer>> iterator = playerTimers.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<ServerPlayer, Integer> entry = iterator.next();
                    int newTimer = entry.getValue() - 1;

                    if (newTimer <= 0) {
                        ServerPlayer player = entry.getKey();
                        Identifier queued = playerStructures.get(player);

                        if (player.isAlive() && getStructure(player.level(), player).equals(queued)) {
                            sendStructureToClient(player, queued);
                        }

                        iterator.remove();
                    } else {
                        entry.setValue(newTimer);
                    }
                }
            }

            if (structureChangeTimer-- <= 0) {
                server.getAllLevels().forEach(level -> {
                    for (ServerPlayer player : level.getPlayers(LivingEntity::isAlive)) tryStructureChange(player, getStructure(level, player));
                });

                structureChangeTimer = STRUCTURE_CHANGE_TIMER_DELAY;
            }
        });

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, (event) -> {
            removeFromAll((ServerPlayer) event.getEntity());
        });

        NeoForge.EVENT_BUS.addListener(EntityLeaveLevelEvent.class, (event) -> {
            if (event.getEntity() instanceof ServerPlayer player) removeFromAll(player);
        });

        NeoForge.EVENT_BUS.addListener(PlayerEvent.Clone.class, (event) -> {
            removeFromAll((ServerPlayer) event.getOriginal());
            sendStructureToClient((ServerPlayer) event.getEntity(), NONE);
        });
    }
}