package net.penumbra.enderscape.manager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.penumbra.enderscape.config.EnderscapeConfig;
import net.penumbra.enderscape.registry.entity.EnderscapeAttributes;
import net.penumbra.enderscape.registry.sound.EnderscapeItemSounds;

import java.util.function.UnaryOperator;

import static net.penumbra.enderscape.registry.entity.EnderscapeAttachments.*;

public class ElytraManager {

    public static final int MAX_ELYTRA_GROUND_TICKS = 5;

    public static void modifyElytraGroundTicks(LivingEntity entity, UnaryOperator<Integer> modifier) {
        if (!entity.hasData(ELYTRA_GROUND_TICKS)) {
            setElytraGroundTicks(entity, 0);
        }

        setElytraGroundTicks(entity, modifier.apply(elytraGroundTicks(entity)));
    }

    public static boolean canReboundFromGround(LivingEntity entity) {
        return hasGlidingJumpStrength(entity) && elytraGroundTicks(entity) <= MAX_ELYTRA_GROUND_TICKS;
    }

    public static boolean canReboundFromFluid(LivingEntity entity) {
        return canReboundFromGround(entity) && entity.isFallFlying() && entity.getDeltaMovement().y() > -0.5;
    }

    public static void applyGlidingExhaustion(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(0.01F * distance * 0.01F);
    }

    public static void tickEntityOnGround(LivingEntity entity) {
        if (entity.onGround()) {
            damageElytraOnGround(entity);
            modifyElytraGroundTicks(entity, value -> value + 1);
        }
    }

    public static void tickEntityAtTail(LivingEntity entity) {
        if (!entity.isAlive() || entity.isSpectator()) return;

        if (!entity.isFallFlying() || !entity.onGround()) entity.removeData(ELYTRA_GROUND_TICKS);
    }

    public static void tickPlayerAtTail(Player player) {
        if (!player.isAlive() || player.isSpectator()) return;

        if (!player.onGround() && !player.isInLiquid()) {
            if (player.getExistingData(MIDAIR_TICKS).orElse(0) < 10) {
                player.setData(MIDAIR_TICKS, player.getExistingData(MIDAIR_TICKS).orElse(0) + 1);
            }
        } else {
            player.removeData(MIDAIR_TICKS);
        }

        tickDownThenRemove(player, START_GLIDING_SOUND_COOLDOWN);
        tickDownThenRemove(player, STOP_GLIDING_SOUND_COOLDOWN);

        boolean stopFlyingManually = EnderscapeConfig.getInstance().elytraSneakToStopGliding && player.isFallFlying() && player.isShiftKeyDown();
        boolean submergedWhileFlying = player.isFallFlying() && player.isUnderWater();

        if (stopFlyingManually || submergedWhileFlying) {
            player.stopFallFlying();
        }
    }

    public static void onStartFallFlying(Player player) {
        playStartOrStopGlidingSound(player, true);

        player.level().broadcastEntityEvent(player, (byte) -68);
    }

    public static void onStopFallFlying(LivingEntity entity) {
        playStartOrStopGlidingSound(entity, false);
    }

    public static boolean shouldDisallowFallFlying(LivingEntity entity, boolean onGround) {
        boolean noMidairTime = !entity.isFallFlying() && entity.getExistingData(MIDAIR_TICKS).orElse(0) < 1;
        boolean sneaking = entity.isShiftKeyDown() && EnderscapeConfig.getInstance().elytraSneakToStopGliding;
        boolean cannotRebound = onGround && !ElytraManager.canReboundFromGround(entity);

        return noMidairTime || sneaking || cannotRebound;
    }

    public static void tryPlayLandingEffects(LivingEntity entity, double start, double last) {
        if (entity.onGround() && entity.getDeltaMovement().lengthSqr() > 0.4 && !hasGlidingJumpStrength(entity)) playLandingEffects(entity, start, last);
    }

    public static boolean hasGlidingJumpStrength(LivingEntity entity) {
        return EnderscapeAttributes.getBounceHeight(entity) > 0.0;
    }

    private static int elytraGroundTicks(LivingEntity entity) {
        return entity.getExistingData(ELYTRA_GROUND_TICKS).orElse(0);
    }

    private static void setElytraGroundTicks(LivingEntity entity, int value) {
        entity.setData(ELYTRA_GROUND_TICKS, value);
    }

    private static void playStartOrStopGlidingSound(LivingEntity entity, boolean starting) {
        // TODO: Create data component for custom open/closing sounds later on

        AttachmentType<Integer> cooldown = starting ? START_GLIDING_SOUND_COOLDOWN : STOP_GLIDING_SOUND_COOLDOWN;
        SoundEvent soundEvent = starting ? EnderscapeItemSounds.ELYTRA_START_GLIDING : EnderscapeItemSounds.ELYTRA_STOP_GLIDING;

        boolean configAllows = EnderscapeConfig.getInstance().elytraAddOpenCloseSounds;
        boolean hasGlider = !findFirstGlider(entity).isEmpty();

        if (entity.level() instanceof ServerLevel server && !entity.hasData(cooldown) && configAllows && hasGlider) {
            server.playSound(
                    null,
                    entity,
                    soundEvent,
                    entity.getSoundSource(),
                    1.0F,
                    Mth.nextFloat(entity.getRandom(), 0.8F, 1.2F)
            );

            entity.setData(cooldown, 2);
        }
    }

    private static void damageElytraOnGround(LivingEntity entity) {
        if (entity.isFallFlying() && elytraGroundTicks(entity) > 0 && elytraGroundTicks(entity) % 3 == 0) {
            ItemStack stack = findFirstGlider(entity);
            if (!stack.isEmpty()) stack.hurtAndBreak(1, entity, entity.getEquipmentSlotForItem(stack));
        }
    }

    private static ItemStack findFirstGlider(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (LivingEntity.canGlideUsing(stack, slot)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static void playLandingEffects(LivingEntity entity, double start, double last) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), EnderscapeItemSounds.ELYTRA_LAND, SoundSource.PLAYERS, 1, 1);

        if (entity.level() instanceof ServerLevel server) {
            double difference = start - last;
            float severity = (float) (difference * 10.0 - 3.0);

            Vec3 pos = entity.position();
            int count = (int) (Mth.clamp(entity.getDeltaMovement().lengthSqr() * 40, 5, 100) + (entity.fallDistance + severity) * 5);

            server.sendParticles(ParticleTypes.POOF, pos.x, pos.y + 0.2, pos.z, count, 0, 0, 0, 0.3);
            server.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.2, pos.z, 1, 0, 0, 0, 0.3);
        }
    }

    private static void tickDownThenRemove(Player player, AttachmentType<Integer> type) {
        if (player.hasData(type)) {
            player.setData(type, player.getData(type) - 1);
            if (player.getExistingDataOrNull(type) <= 0) player.removeData(type);
        }
    }
}
