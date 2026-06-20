/*
 * Better Bats - Chiroptera Enhancements
 * Copyright (C) 2026 Dasik (Rifaditya)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

// Verified against: Level.java (26.2+)
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * AI Goal: At night, bats seek out and circle nearby light sources (lanterns, torches, etc.).
 * Detection uses BLOCK light layer to find artificial light sources.
 *
 * Previous issues fixed in 1.1.15:
 * - Detection threshold was too high (>12 = within 2 blocks of lantern — unreachable for flying bats)
 * - Approach force was too weak (0.05/dist ≈ 0.005/tick, overwhelmed by ground avoidance)
 * - Search volume was too small (10 probes in 16×8 area, biased toward bat altitude not ground)
 */
public class BatHuntLightGoal extends Goal {
    private final Bat bat;
    private BlockPos targetLight;
    private int circlingTicks;
    private double targetCenterX;
    private double targetCenterY;
    private double targetCenterZ;

    /**
     * Minimum BLOCK light to count as a light source worth investigating.
     * Lantern = 15, Torch = 14, Soul Lantern = 10. At 8 blocks from a lantern, light = 7.
     * Threshold of 8 means detection within ~7 blocks of a lantern or ~6 blocks of a torch.
     */
    private static final int LIGHT_DETECT_THRESHOLD = 8;

    /**
     * Minimum BLOCK light at the target to keep circling.
     * Slightly lower than detection to avoid immediate stop after locking on.
     */
    private static final int LIGHT_CONTINUE_THRESHOLD = 6;

    public BatHuntLightGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.bat.isResting() || this.bat.getRandom().nextInt(30) != 0) {
            return false;
        }
        Level level = this.bat.level();
        if (level.isClientSide() || level.isBrightOutside()) return false;
        
        BlockPos pos = this.bat.blockPosition();

        // Quick self-check: is the bat already near a light?
        if (level.getBrightness(LightLayer.BLOCK, pos) > LIGHT_DETECT_THRESHOLD) {
            this.targetLight = pos;
            return true;
        }
        
        // Probe for light sources — 25 samples, wider range, heavy downward bias
        // Bats fly above terrain so lights are almost always below them
        for (int i = 0; i < 25; i++) {
            int dx = this.bat.getRandom().nextInt(24) - 12;
            int dy = this.bat.getRandom().nextInt(30) - 24; // Heavy downward bias: -24 to +5
            int dz = this.bat.getRandom().nextInt(24) - 12;
            BlockPos checkPos = pos.offset(dx, dy, dz);
            if (level.getBrightness(LightLayer.BLOCK, checkPos) > LIGHT_DETECT_THRESHOLD) {
                this.targetLight = checkPos;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.circlingTicks = 0;
        if (this.bat instanceof net.dasik.social.api.group.GroupMember gm) {
            gm.setLeader(null); 
        }
        if (this.targetLight != null) {
            this.targetCenterX = this.targetLight.getX() + 0.5;
            this.targetCenterY = this.targetLight.getY() + 0.5;
            this.targetCenterZ = this.targetLight.getZ() + 0.5;
        }
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetLight != null
            && this.circlingTicks < 200
            && !this.bat.isResting()
            && this.bat.level().getBrightness(LightLayer.BLOCK, this.targetLight) > LIGHT_CONTINUE_THRESHOLD;
    }

    @Override
    public void stop() {
        this.targetLight = null;
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(false);
        }
    }

    @Override
    public void tick() {
        this.circlingTicks++;
        if (this.targetLight != null) {
            double batX = this.bat.getX();
            double batY = this.bat.getY();
            double batZ = this.bat.getZ();

            double dx = this.targetCenterX - batX;
            double dy = this.targetCenterY - batY;
            double dz = this.targetCenterZ - batZ;
            
            double distSq = dx * dx + dy * dy + dz * dz;
            double dist = Math.sqrt(distSq);

            if (dist > 1.0E-4) {
                Vec3 currentMovement = this.bat.getDeltaMovement();
                if (dist > 2.5) {
                    // Approach phase: strong steering force toward the light source
                    // Separate horizontal and vertical to prevent Y-distance suppression
                    double horizDist = Math.sqrt(dx * dx + dz * dz);
                    double steerX = 0.0;
                    double steerZ = 0.0;
                    if (horizDist > 0.01) {
                        steerX = (dx / horizDist) * 0.12;
                        steerZ = (dz / horizDist) * 0.12;
                    }
                    double steerY = 0.0;
                    if (Math.abs(dy) > 0.01) {
                        steerY = Math.signum(dy) * 0.10;
                    }
                    this.bat.setDeltaMovement(currentMovement.add(steerX, steerY, steerZ));
                } else {
                    // Circling phase: orbit around the light source
                    // cross product with (0,1,0):
                    // cross.x = dy * 0 - dz * 1 = -dz
                    // cross.y = dz * 0 - dx * 0 = 0
                    // cross.z = dx * 1 - dy * 0 = dx
                    double crossX = -dz;
                    double crossZ = dx;
                    double crossLen = Math.sqrt(crossX * crossX + crossZ * crossZ);

                    if (crossLen > 1.0E-4) {
                        double scale = 0.12 / crossLen;
                        double finalX = (currentMovement.x + crossX * scale) * 0.9;
                        double finalY = currentMovement.y * 0.9;
                        double finalZ = (currentMovement.z + crossZ * scale) * 0.9;
                        this.bat.setDeltaMovement(finalX, finalY, finalZ);
                    } else {
                        this.bat.setDeltaMovement(currentMovement.x * 0.9, currentMovement.y * 0.9, currentMovement.z * 0.9);
                    }
                    
                    if (this.bat.getRandom().nextInt(10) == 0 && this.bat.level() instanceof ServerLevel sl) {
                        sl.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, batX, batY, batZ, 3, 0.2, 0.2, 0.2, 0.05);
                    }
                }
            }
        }
    }
}
