// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

import java.util.EnumSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatDiveBombGoal extends Goal {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatDiveBombGoal.class);
    private final Bat bat;
    private LivingEntity targetPest;

    public BatDiveBombGoal(Bat bat) {
        this.bat = bat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = this.bat.level();
        if (level.isClientSide() || !net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_PEST_CONTROL)) {
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
        if (this.targetPest != null && net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatDiveBombGoal] [Bat#{}] Initiated pest dive-bomb on {} at {}", this.bat.getId(), this.targetPest.getType().getDescription().getString(), this.targetPest.blockPosition());
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPest != null && this.targetPest.isAlive() && !this.bat.isResting();
    }

    @Override
    public void stop() {
        if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
            LOGGER.info("[BetterBats:BatDiveBombGoal] [Bat#{}] Stopped dive-bomb goal", this.bat.getId());
        }
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
                float attackDamageTrait = net.dasik.social.api.genetics.DasikAnimalGeneticsAPI.getTrait(this.bat, "attack_damage", 2.0f);
                float damage = 10.0f * attackDamageTrait;
                if (net.vanillaoutsider.betterbats.util.BatDebugHelper.isDebug(this.bat.level())) {
                    LOGGER.info("[BetterBats:BatDiveBombGoal] [Bat#{}] Strike executed against pest {} (Dmg: {})", this.bat.getId(), this.targetPest.getType().getDescription().getString(), damage);
                }
                this.targetPest.hurt(this.bat.damageSources().mobAttack(this.bat), damage);
                this.bat.playSound(net.minecraft.sounds.SoundEvents.BAT_AMBIENT, 1.0f, 0.5f); 
                this.targetPest = null;
            } else {
                this.bat.setDeltaMovement(dir.normalize().scale(0.3));
            }
        }
    }
}
