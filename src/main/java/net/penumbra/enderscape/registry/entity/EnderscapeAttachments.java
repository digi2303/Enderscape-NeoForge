package net.penumbra.enderscape.registry.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.penumbra.enderscape.Enderscape;

import java.util.ArrayList;
import java.util.List;

public class EnderscapeAttachments {

    public static final List<AttachmentType<?>> ATTACHMENT_TYPES = new ArrayList<>();
    public static final List<AttachmentType<?>> PERSIST_WHILE_DYING = new ArrayList<>();

    public static final AttachmentType<LevelData.RespawnData> END_HAVEN_RESPAWN_DATA = register(
            "end_haven_respawn_data",
            true,
            AttachmentType.builder(
                    () -> new LevelData.RespawnData(new GlobalPos(Level.OVERWORLD, BlockPos.containing(0, 0, 0)), 0.0F, 0.0F)
            ).sync((holder, player) -> holder == player, LevelData.RespawnData.STREAM_CODEC).serialize(LevelData.RespawnData.CODEC.fieldOf("value")).copyOnDeath()
    );

    public static final AttachmentType<Unit> PENDING_END_HAVEN_RESPAWN = register(
            "pending_end_haven_respawn",
            true,
            AttachmentType.builder(() -> Unit.INSTANCE).serialize(Unit.CODEC.fieldOf("value"))
    );

    public static final AttachmentType<Unit> PERFORMING_STUN_ATTACK = register(
            "performing_stun_attack",
            AttachmentType.builder(() -> Unit.INSTANCE)
    );

    public static final AttachmentType<Integer> IDLE_TICKS = register(
            "idle_ticks",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> VOID_DESTRUCTION_TICKS = register(
            "void_destruction_ticks",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Vec3> LAST_POSITION = register(
            "last_position",
            AttachmentType.builder(() -> Vec3.ZERO).serialize(Vec3.CODEC.fieldOf("value"))
    );

    public static final AttachmentType<Integer> MIDAIR_TICKS = register(
            "midair_ticks",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> START_GLIDING_SOUND_COOLDOWN = register(
            "start_gliding_sound_cooldown",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> STOP_GLIDING_SOUND_COOLDOWN = register(
            "stop_gliding_sound_cooldown",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> ELYTRA_GROUND_TICKS = register(
            "elytra_ground_ticks",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> MAGNIA_COOLDOWN_TIME = register(
            "magnia_cooldown_time",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> DASH_JUMP_CHARGING_TICKS = register(
            "dash_jump_charging_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> holder == player, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> DASH_JUMP_TICKS = register(
            "dash_jump_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Boolean> HAS_END_TRIAL_SPAWNER_EFFECTS = register(
            "has_end_trial_spawner_effects",
            AttachmentType.builder(() -> false).sync((holder, player) -> holder != player, ByteBufCodecs.BOOL).serialize(Codec.BOOL.fieldOf("value"))
    );

    public static final AttachmentType<Float> VOIDED_HEALTH = register(
            "voided_health",
            true,
            AttachmentType.builder(() -> 0.0F).sync((holder, player) -> true, ByteBufCodecs.FLOAT).serialize(Codec.FLOAT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> VOID_IMMUNITY_TICKS = register(
            "void_immunity_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> holder == player, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> OUTER_VOID_TICKS = register(
            "outer_void_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> OUTER_VOID_TICK_REMOVAL_DELAY = register(
            "outer_void_tick_removal_delay",
            true,
            AttachmentType.builder(() -> 0).sync((holder, player) -> holder == player, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> VOID_TICKS = register(
            "void_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> VOID_TICK_DOWN_DELAY = register(
            "void_tick_down_delay",
            AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("value")).sync((holder, player) -> holder == player, ByteBufCodecs.INT)
    );

    public static final AttachmentType<Integer> STUN_DURATION = register(
            "stun_duration",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> STUN_TICKS = register(
            "stun_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    public static final AttachmentType<Integer> ATTACK_ANIMATION_REMAINING_TICKS = register(
            "attack_animation_remaining_ticks",
            AttachmentType.builder(() -> 0).sync((holder, player) -> true, ByteBufCodecs.INT).serialize(Codec.INT.fieldOf("value"))
    );

    private static <A> AttachmentType<A> register(String name, AttachmentType.Builder<A> builder) {
        return register(name, false, builder);
    }

    private static <A> AttachmentType<A> register(String name, boolean persistWhileDying, AttachmentType.Builder<A> builder) {
        AttachmentType<A> type = Registry.register(NeoForgeRegistries.ATTACHMENT_TYPES, Enderscape.id(name), builder.build());
        ATTACHMENT_TYPES.add(type);
        if (persistWhileDying) PERSIST_WHILE_DYING.add(type);
        return type;
    }

    public static void afterLivingEntityDeath(LivingEntity entity, DamageSource source) {
        ATTACHMENT_TYPES.forEach(type -> {
            if (!PERSIST_WHILE_DYING.contains(type)) entity.removeData(type);
        });
    }
}