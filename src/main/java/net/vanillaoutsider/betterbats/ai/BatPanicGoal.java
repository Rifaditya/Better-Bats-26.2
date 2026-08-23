/*
 * Better Bats - Chiroptera Enhancements
 * Copyright (C) 2026 Dasik (Rifaditya)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

// Verified against: Bat.java (26.1.2)
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterbats.BatStateAccessor;

import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI Goal for Bats to scatter in panic away from sound/vibration sources.
 */
public class BatPanicGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatPanicGoal.class);
    private final Bat bat;
    private final BatStateAccessor accessor;

    public BatPanicGoal(Bat bat) {
        this.bat = bat;
        this.accessor = (BatStateAccessor) bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.accessor.betterbats$isPanicked();
    }

    @Override
    public void start() {
        // Disperse from group when panicked
        if (this.bat instanceof net.dasik.social.api.group.GroupMember gm) {
            gm.setLeader(null);
        }
        // Play panic sounds (mixed takeoff and low-pitch flap)
        this.bat.playSound(net.minecraft.sounds.SoundEvents.BAT_TAKEOFF, 0.8f, 1.0f);
        this.bat.playSound(net.minecraft.sounds.SoundEvents.PHANTOM_FLAP, 0.5f, 0.6f);
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatPanicGoal] [Bat#{}] Panicked! Fleeing disturbance source at {}", this.bat.getId(), this.accessor.betterbats$getPanicSource());
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.accessor.betterbats$isPanicked();
    }

    @Override
    public void stop() {
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatPanicGoal] [Bat#{}] Panic state cleared", this.bat.getId());
        }
    }

    @Override
    public void tick() {
        Vec3 panicSource = this.accessor.betterbats$getPanicSource();
        double dirX;
        double dirY;
        double dirZ;

        if (panicSource != null) {
            // Fly away from the source
            dirX = this.bat.getX() - panicSource.x;
            dirY = this.bat.getY() - panicSource.y;
            dirZ = this.bat.getZ() - panicSource.z;
        } else {
            // Fly in a random direction
            dirX = this.bat.getRandom().nextDouble() - 0.5;
            dirY = this.bat.getRandom().nextDouble() - 0.5;
            dirZ = this.bat.getRandom().nextDouble() - 0.5;
        }
        
        double lenSqr = dirX * dirX + dirY * dirY + dirZ * dirZ;
        if (lenSqr < 0.01) {
            dirX = this.bat.getRandom().nextDouble() - 0.5;
            dirY = 0.5;
            dirZ = this.bat.getRandom().nextDouble() - 0.5;
            lenSqr = dirX * dirX + dirY * dirY + dirZ * dirZ;
        }
        
        // Normalize direction
        double invLen = 1.0 / Math.sqrt(lenSqr);
        dirX *= invLen;
        dirY *= invLen;
        dirZ *= invLen;
        
        // Rapid flight speed (with single Vec3 allocation inside setDeltaMovement)
        Vec3 motion = this.bat.getDeltaMovement();
        double nextX = (motion.x + dirX * 0.15) * 0.85;
        double nextY = (motion.y + dirY * 0.15) * 0.85;
        double nextZ = (motion.z + dirZ * 0.15) * 0.85;
        this.bat.setDeltaMovement(nextX, nextY, nextZ);
        
        // Tick down panic time
        int ticks = this.accessor.betterbats$getPanicTicks();
        if (ticks > 0) {
            this.accessor.betterbats$setPanicTicks(ticks - 1);
        }
    }
}

