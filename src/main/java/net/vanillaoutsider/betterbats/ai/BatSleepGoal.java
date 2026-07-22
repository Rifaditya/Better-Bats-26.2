// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * AI Goal for Bats to seek dark roosting spots during the day.
 * Verified against: Bat.java (26.1.2 Release)
 */
public class BatSleepGoal extends Goal {
    private final Bat bat;
    private BlockPos roostPos;

    public BatSleepGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Only trigger during the day if not already resting
        if (this.bat.isResting() || !this.bat.level().isBrightOutside() || this.bat.getRandom().nextInt(20) != 0) {
            return false;
        }

        Level level = this.bat.level();
        if (level.isClientSide()) return false;

        BlockPos pos = this.bat.blockPosition();

        // If currently in a dark spot, just rest immediately
        if (this.isSuitableRoost(level, pos)) {
            this.bat.setResting(true);
            return false;
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
        
        // Photophobia: Must be very dark during the day
        if (level.getBrightness(LightLayer.SKY, pos) > 0) return false;
        if (level.getBrightness(LightLayer.BLOCK, pos) > 7) return false;
        
        // Ceiling check: Must be a solid block above
        BlockPos above = pos.above();
        return level.getBlockState(above).isRedstoneConductor(level, above);
    }

    @Override
    public boolean canContinueToUse() {
        return this.roostPos != null && !this.bat.isResting() && this.bat.level().isBrightOutside() && this.isSuitableRoost(this.bat.level(), this.roostPos);
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
    }

    @Override
    public void tick() {
        if (this.roostPos != null) {
            Vec3 target = Vec3.atCenterOf(this.roostPos);
            Vec3 dir = target.subtract(this.bat.position());
            
            double distSqr = dir.lengthSqr();
            if (distSqr < 1.0) {
                this.bat.setResting(true);
            } else {
                // Move towards the dark spot
                this.bat.setDeltaMovement(this.bat.getDeltaMovement().add(dir.normalize().scale(0.08)));
            }
        }
    }

    @Override
    public void stop() {
        this.roostPos = null;
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(false);
        }
    }
}
