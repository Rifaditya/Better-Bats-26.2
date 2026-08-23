// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.command;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterbats.BatStateAccessor;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

import java.util.Comparator;
import java.util.List;

/**
 * Diagnostic and testing helper for Better Bats commands.
 */
public class BatCommandHelper {

    public static int inspectNearestBat(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();

        List<Bat> bats = level.getEntitiesOfClass(Bat.class, new AABB(
                pos.x - 16, pos.y - 16, pos.z - 16,
                pos.x + 16, pos.y + 16, pos.z + 16
        ));

        if (bats.isEmpty()) {
            source.sendFailure(Component.literal("§c[Better Bats] No bats found within 16 blocks."));
            return 0;
        }

        Bat nearest = bats.stream()
                .min(Comparator.comparingDouble(b -> b.distanceToSqr(pos)))
                .orElse(bats.get(0));

        int guanoTicks = 0;
        boolean isGoalActive = false;
        boolean isPanicked = false;
        if (nearest instanceof BatStateAccessor accessor) {
            guanoTicks = accessor.betterbats$getGuanoTicks();
            isGoalActive = accessor.betterbats$isGoalActive();
            isPanicked = accessor.betterbats$isPanicked();
        }

        int threshold = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_GUANO_THRESHOLD);
        float scale = nearest.getScale();
        Vec3 vel = nearest.getDeltaMovement();
        double speed = vel.length();

        StringBuilder sb = new StringBuilder();
        sb.append("§6=== Better Bats: Bat Diagnostic Report ===§r\n");
        sb.append(" §7UUID: §f").append(nearest.getUUID().toString().substring(0, 8)).append("...\n");
        sb.append(" §7State: ").append(nearest.isResting() ? "§e[Roosting / Resting]§r" : "§b[Flying]§r").append("\n");
        sb.append(" §7Wingspan / Scale: §a").append(String.format("%.2fx", scale)).append(" §7(Genetics)§r\n");
        sb.append(" §7Guano Accumulation: §a").append(guanoTicks).append("§7/§f").append(threshold).append(" ticks§r\n");
        sb.append(" §7Velocity: §f[").append(String.format("%.2f", vel.x)).append(", ")
                .append(String.format("%.2f", vel.y)).append(", ")
                .append(String.format("%.2f", vel.z)).append("] §7(Speed: §a")
                .append(String.format("%.2f", speed)).append(" blk/t)§r\n");
        sb.append(" §7Goal Active: ").append(isGoalActive ? "§aYes§r" : "§7No§r")
                .append(" §7| Panicked: ").append(isPanicked ? "§cYes§r" : "§7No§r");

        source.sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    public static int spawnSwarm(CommandSourceStack source, int count) {
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();
        int clampedCount = Mth.clamp(count, 1, 30);

        int spawned = 0;
        for (int i = 0; i < clampedCount; i++) {
            Bat bat = EntityTypes.BAT.create(level, EntitySpawnReason.COMMAND);
            if (bat != null) {
                double offsetX = (level.getRandom().nextDouble() - 0.5) * 3.0;
                double offsetY = 1.0 + level.getRandom().nextDouble() * 2.0;
                double offsetZ = (level.getRandom().nextDouble() - 0.5) * 3.0;

                bat.setPos(pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ);
                bat.setYRot(level.getRandom().nextFloat() * 360.0F);
                bat.setResting(false);
                
                double vx = (level.getRandom().nextDouble() - 0.5) * 0.2;
                double vy = 0.05 + level.getRandom().nextDouble() * 0.1;
                double vz = (level.getRandom().nextDouble() - 0.5) * 0.2;
                bat.setDeltaMovement(vx, vy, vz);

                level.addFreshEntity(bat);
                spawned++;
            }
        }

        int finalSpawned = spawned;
        source.sendSuccess(() -> Component.literal("§a[Better Bats] Successfully spawned a murmuration swarm of " + finalSpawned + " bats."), true);
        return finalSpawned;
    }
}
