// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI Goal: At night, bats seek out and circle nearby light sources (lanterns, torches, etc.).
 * 
 * 1.1.16 Overhaul:
 * - Deterministic native BlockPos search instead of random probing
 * - "Moth effect": bats will hop between lanterns instead of orbiting one forever
 * - Curved banking approach instead of straight lines
 * - Vertical sine-wave bobbing during orbit
 */
public class BatHuntLightGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatHuntLightGoal.class);
    private final Bat bat;
    private BlockPos targetLight;
    private int circlingTicks;
    private int maxCirclingTicks;
    private int nextAllowedTick;
    private double targetCenterX;
    private double targetCenterY;
    private double targetCenterZ;

    private static final int LIGHT_DETECT_THRESHOLD = 8;
    private static final int LIGHT_CONTINUE_THRESHOLD = 6;

    public BatHuntLightGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.nextAllowedTick = 0;
    }

    @Override
    public boolean canUse() {
        if (this.bat.isResting() || this.bat.tickCount < this.nextAllowedTick) {
            return false;
        }
        // Only run the heavy search every ~30 ticks to save performance
        if (this.bat.getRandom().nextInt(30) != 0) {
            return false;
        }
        Level level = this.bat.level();
        if (level.isClientSide() || level.isBrightOutside()) return false;
        
        BlockPos pos = this.bat.blockPosition();

        // Self-check
        if (level.getBrightness(LightLayer.BLOCK, pos) > LIGHT_DETECT_THRESHOLD) {
            this.targetLight = pos;
            return true;
        }
        
        // Native deterministic search with tight spatial bounds (8 horizontal, 6 vertical)
        this.targetLight = BlockPos.findClosestMatch(pos, 8, 6, 
            checkPos -> level.getBrightness(LightLayer.BLOCK, checkPos) > LIGHT_DETECT_THRESHOLD
        ).orElse(null);

        return this.targetLight != null;
    }

    @Override
    public void start() {
        this.circlingTicks = 0;
        // Orbit for 10 to 30 seconds
        this.maxCirclingTicks = 200 + this.bat.getRandom().nextInt(400);

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
        if (this.targetLight != null && net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatHuntLightGoal] [Bat#{}] Circling light source at {}", this.bat.getId(), this.targetLight);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetLight != null
            && this.circlingTicks < this.maxCirclingTicks
            && !this.bat.isResting()
            && this.bat.level().getBrightness(LightLayer.BLOCK, this.targetLight) > LIGHT_CONTINUE_THRESHOLD;
    }

    @Override
    public void stop() {
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatHuntLightGoal] [Bat#{}] Completed light circling after {} ticks", this.bat.getId(), this.circlingTicks);
        }
        this.targetLight = null;
        // Cooldown: 10 to 20 seconds before hunting another light
        this.nextAllowedTick = this.bat.tickCount + 200 + this.bat.getRandom().nextInt(200);

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
                    // Curved approach phase
                    double horizDist = Math.sqrt(dx * dx + dz * dz);
                    double steerX = 0.0;
                    double steerZ = 0.0;
                    if (horizDist > 0.01) {
                        // Blend direct vector with tangential curve for banking
                        double curveFactor = 0.5;
                        double curveX = -dz * curveFactor;
                        double curveZ = dx * curveFactor;
                        
                        steerX = ((dx + curveX) / horizDist) * 0.12;
                        steerZ = ((dz + curveZ) / horizDist) * 0.12;
                    }
                    // Smooth vertical approach
                    double steerY = 0.0;
                    if (Math.abs(dy) > 0.01) {
                        steerY = Math.signum(dy) * Math.min(0.10, Math.abs(dy) * 0.03);
                    }
                    this.bat.setDeltaMovement(currentMovement.add(steerX, steerY, steerZ));
                } else {
                    // Circling phase
                    double crossX = -dz;
                    double crossZ = dx;
                    double crossLen = Math.sqrt(crossX * crossX + crossZ * crossZ);

                    if (crossLen > 1.0E-4) {
                        double scale = 0.12 / crossLen;
                        // Add sine-wave bobbing
                        double bobbing = Math.sin(this.circlingTicks * 0.15) * 0.03;
                        
                        double finalX = (currentMovement.x + crossX * scale) * 0.9;
                        double finalY = currentMovement.y * 0.9 + bobbing;
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
