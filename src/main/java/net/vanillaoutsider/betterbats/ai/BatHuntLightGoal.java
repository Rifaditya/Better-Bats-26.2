/*
 * Better Bats - Chiroptera Enhancements
 * Copyright (C) 2026 Dasik (Rifaditya)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

// Verified against: Level.java (26.1.2)
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BatHuntLightGoal extends Goal {
    private final Bat bat;
    private BlockPos targetLight;
    private int circlingTicks;
    private double targetCenterX;
    private double targetCenterY;
    private double targetCenterZ;

    public BatHuntLightGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.bat.isResting() || this.bat.getRandom().nextInt(40) != 0) {
            return false;
        }
        Level level = this.bat.level();
        if (level.isClientSide() || level.isBrightOutside()) return false;
        
        BlockPos pos = this.bat.blockPosition();
        if (level.getBrightness(LightLayer.BLOCK, pos) > 12) {
            this.targetLight = pos;
            return true;
        }
        
        for (int i = 0; i < 10; i++) {
            BlockPos checkPos = pos.offset(this.bat.getRandom().nextInt(16) - 8, this.bat.getRandom().nextInt(8) - 4, this.bat.getRandom().nextInt(16) - 8);
            if (level.getBrightness(LightLayer.BLOCK, checkPos) > 12) {
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
        return this.targetLight != null && this.circlingTicks < 200 && !this.bat.isResting() && this.bat.level().getBrightness(LightLayer.BLOCK, this.targetLight) > 12;
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
                if (dist > 1.5) {
                    double scale = 0.05 / dist;
                    double addX = dx * scale;
                    double addY = dy * scale;
                    double addZ = dz * scale;
                    this.bat.setDeltaMovement(currentMovement.add(addX, addY, addZ));
                } else {
                    // cross product with (0,1,0):
                    // cross.x = dy * 0 - dz * 1 = -dz
                    // cross.y = dz * 0 - dx * 0 = 0
                    // cross.z = dx * 1 - dy * 0 = dx
                    double crossX = -dz;
                    double crossY = 0.0;
                    double crossZ = dx;
                    double crossLen = Math.sqrt(crossX * crossX + crossZ * crossZ);

                    if (crossLen > 1.0E-4) {
                        double scale = 0.1 / crossLen;
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

