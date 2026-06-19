// Verified against: Silverfish.java (26.1.2)
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

import java.util.EnumSet;
import java.util.List;

public class BatDiveBombGoal extends Goal {
    private final Bat bat;
    private LivingEntity targetPest;

    public BatDiveBombGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.bat.level().isClientSide() || !this.bat.level().getServer().getGameRules().get(BetterBatsFabric.BAT_PEST_CONTROL)) {
            return false;
        }
        if (this.bat.isResting() || this.bat.getRandom().nextInt(20) != 0) {
            return false;
        }
        
        List<LivingEntity> pests = this.bat.level().getEntitiesOfClass(LivingEntity.class, this.bat.getBoundingBox().inflate(8.0), e -> (e instanceof Silverfish || e instanceof Endermite) && e.isAlive());
        
        if (!pests.isEmpty()) {
            this.targetPest = pests.get(this.bat.getRandom().nextInt(pests.size()));
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if (this.bat instanceof net.dasik.social.api.group.GroupMember gm) {
            gm.setLeader(null); 
        }
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(true);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPest != null && this.targetPest.isAlive() && !this.bat.isResting();
    }

    @Override
    public void stop() {
        this.targetPest = null;
        if (this.bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            accessor.betterbats$setGoalActive(false);
        }
    }

    @Override
    public void tick() {
        if (this.targetPest != null) {
            Vec3 dir = this.targetPest.position().subtract(this.bat.position());
            double dist = dir.length();
            
            if (dist < 1.0) {
                this.targetPest.hurt(this.bat.damageSources().mobAttack(this.bat), 20.0f);
                this.bat.playSound(net.minecraft.sounds.SoundEvents.BAT_AMBIENT, 1.0f, 0.5f); 
                this.targetPest = null;
            } else {
                this.bat.setDeltaMovement(dir.normalize().scale(0.3));
            }
        }
    }
}
