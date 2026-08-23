// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI Goal for Bats to seek dark roosting spots during the day and storms, clustering with peers.
 */
public class BatSleepGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatSleepGoal.class);
    private final Bat bat;
    private BlockPos roostPos;

    public BatSleepGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = this.bat.level();
        if (level.isClientSide()) return false;

        BlockPos pos = this.bat.blockPosition();
        boolean shouldSleep = level.isBrightOutside() || (level.isRaining() && level.canSeeSky(pos));

        // Only trigger during daytime or storms if not already resting
        if (this.bat.isResting() || !shouldSleep || this.bat.getRandom().nextInt(20) != 0) {
            return false;
        }

        // If currently in a dark spot, just rest immediately
        if (this.isSuitableRoost(level, pos)) {
            this.bat.setResting(true);
            return false;
        }

        // Roost Clustering: Prefer spots near existing resting bats
        java.util.List<Bat> restingNeighbors = level.getEntitiesOfClass(
            Bat.class,
            this.bat.getBoundingBox().inflate(16.0),
            b -> b != this.bat && b.isAlive() && b.isResting()
        );

        if (!restingNeighbors.isEmpty()) {
            Bat clusterTarget = restingNeighbors.get(this.bat.getRandom().nextInt(restingNeighbors.size()));
            BlockPos clusterPos = clusterTarget.blockPosition();
            for (int i = 0; i < 10; i++) {
                BlockPos check = clusterPos.offset(
                    this.bat.getRandom().nextInt(5) - 2,
                    this.bat.getRandom().nextInt(3) - 1,
                    this.bat.getRandom().nextInt(5) - 2
                );
                if (this.isSuitableRoost(level, check)) {
                    this.roostPos = check;
                    return true;
                }
            }
        }

        // Search for a suitable dark roost nearby (16 block radius)
        for (int i = 0; i < 15; i++) {
            BlockPos check = pos.offset(
                this.bat.getRandom().nextInt(16) - 8, 
                this.bat.getRandom().nextInt(10) - 2, 
                this.bat.getRandom().nextInt(16) - 8
            );
            
            if (this.isSuitableRoost(level, check)) {
                this.roostPos = check;
                return true;
            }
        }

        return false;
    }

    private boolean isSuitableRoost(Level level, BlockPos pos) {
        if (!level.isEmptyBlock(pos)) return false;
        
        // Photophobia: Must be very dark during daytime/storms
        if (level.getBrightness(LightLayer.SKY, pos) > 0) return false;
        if (level.getBrightness(LightLayer.BLOCK, pos) > 7) return false;
        
        // Roost check: Solid ceiling, dripstone, chains, lanterns, fences, walls, leaves
        BlockPos above = pos.above();
        return BatRoostHelper.isSuitableRoost(level, pos, above);
    }

    @Override
    public boolean canContinueToUse() {
        boolean shouldSleep = this.bat.level().isBrightOutside() || (this.bat.level().isRaining() && this.bat.level().canSeeSky(this.bat.blockPosition()));
        return this.roostPos != null && !this.bat.isResting() && shouldSleep && this.isSuitableRoost(this.bat.level(), this.roostPos);
    }

    @Override
    public void start() {
        // Leave any active swarm to find a personal roost
        if (this.bat instanceof net.dasik.social.api.group.GroupMember gm) {
            gm.setLeader(null);
        }
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(true);
        }
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatSleepGoal] [Bat#{}] Seeking roost at {}", this.bat.getId(), this.roostPos);
        }
    }

    @Override
    public void tick() {
        if (this.roostPos != null) {
            Vec3 target = Vec3.atCenterOf(this.roostPos);
            Vec3 dir = target.subtract(this.bat.position());
            
            double distSqr = dir.lengthSqr();
            if (distSqr < 1.0) {
                this.bat.setResting(true);
                if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
                    LOGGER.info("[BetterBats:BatSleepGoal] [Bat#{}] Successfully reached roost at {}", this.bat.getId(), this.roostPos);
                }
            } else {
                // Move towards the dark spot
                this.bat.setDeltaMovement(this.bat.getDeltaMovement().add(dir.normalize().scale(0.08)));
            }
        }
    }

    @Override
    public void stop() {
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatSleepGoal] [Bat#{}] Stopped sleep goal (Resting: {})", this.bat.getId(), this.bat.isResting());
        }
        this.roostPos = null;
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(false);
        }
    }
}
