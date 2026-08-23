// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.dasik.social.api.group.GroupMember;
import net.dasik.social.api.group.FlockType;
import net.dasik.social.core.group.FlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.BonemealableBlock;
import net.vanillaoutsider.betterbats.BetterBatsFabric;
import net.vanillaoutsider.betterbats.BatStateAccessor;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bat.class)
public abstract class BatMixin implements GroupMember, BatStateAccessor {

    @Unique
    private FlockType betterbats$flockType = FlockType.AERIAL;
    @Unique
    private int betterbats$guanoTicks = 0;
    @Unique
    private int betterbats$panicTicks = 0;
    @Unique
    private Vec3 betterbats$panicSource;
    @Unique
    private boolean betterbats$goalActive = false;

    @Override
    public int betterbats$getGuanoTicks() {
        return this.betterbats$guanoTicks;
    }

    @Override
    public boolean betterbats$isGoalActive() {
        return this.betterbats$goalActive;
    }

    @Override
    public void betterbats$setGoalActive(boolean active) {
        this.betterbats$goalActive = active;
    }

    @Override
    public LivingEntity getLeader() { return null; }

    @Override
    public boolean hasLeader() { return false; }

    @Override
    public void setLeader(LivingEntity leader) {}

    @Override
    public int getGroupSize() { return 1; }

    public int getMaxSpawnClusterSize() {
        Bat self = (Bat)(Object)this;
        return self.level().isClientSide() ? 5 : net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(self.level(), BetterBatsFabric.BAT_SWARM_SIZE);
    }

    @Override
    public FlockType getFlockType() { return this.betterbats$flockType; }

    @Override
    public FlockState getFlockState() { return null; }

    @Override
    public void setFlockState(FlockState state) {}

    @Override
    public void betterbats$resetGuanoTicks() {
        this.betterbats$guanoTicks = 0;
    }

    @Override
    public void betterbats$panic(Vec3 source) {
        this.betterbats$panicTicks = 100;
        this.betterbats$panicSource = source;
    }

    @Override
    public boolean betterbats$isPanicked() {
        return this.betterbats$panicTicks > 0;
    }

    @Override
    public int betterbats$getPanicTicks() {
        return this.betterbats$panicTicks;
    }

    @Override
    public void betterbats$setPanicTicks(int ticks) {
        this.betterbats$panicTicks = ticks;
    }

    @Override
    public Vec3 betterbats$getPanicSource() {
        return this.betterbats$panicSource;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void betterbats$onInit(EntityType<? extends Bat> type, Level level, CallbackInfo ci) {
        Bat self = (Bat)(Object)this;
        if (!level.isClientSide()) {
            ((MobAccessor)self).getGoalSelector().addGoal(1, new net.vanillaoutsider.betterbats.ai.BatPanicGoal(self));
            ((MobAccessor)self).getGoalSelector().addGoal(2, new net.vanillaoutsider.betterbats.ai.BatSleepGoal(self));
            ((MobAccessor)self).getGoalSelector().addGoal(4, new net.vanillaoutsider.betterbats.ai.BatHuntLightGoal(self));
            ((MobAccessor)self).getGoalSelector().addGoal(5, new net.vanillaoutsider.betterbats.ai.BatDiveBombGoal(self));
        }
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void betterbats$onCustomServerAiStep(ServerLevel level, CallbackInfo ci) {
        Bat self = (Bat)(Object)this;

        if (!net.dasik.social.api.genetics.DasikAnimalGeneticsAPI.hasGenetics(self)) {
            net.dasik.social.api.genetics.DasikAnimalGeneticsAPI.rollStats(self, "better-bats:bat");
            net.dasik.social.api.genetics.GeneticsEngine.applyGeneticsModifiers(self);
        }

        BlockPos pos = self.blockPosition();
        BlockPos above = pos.above();

        if (self.isResting()) {
            boolean isSilent = self.isSilent();
            if (net.vanillaoutsider.betterbats.ai.BatRoostHelper.isSuitableRoost(level, pos, above)) {
                if (self.getRandom().nextInt(200) == 0) {
                    self.yHeadRot = self.getRandom().nextInt(360);
                }
                if (level.getNearestPlayer(net.minecraft.world.entity.ai.targeting.TargetingConditions.forNonCombat().range(4.0), self) != null) {
                    self.setResting(false);
                    if (!isSilent) {
                        level.levelEvent(null, 1025, pos, 0);
                    }
                } else if (self.getRandom().nextInt(20) == 0) {
                    // Check for nearby predators (Cats, Ocelots, Phantoms) within 10 blocks (throttled to 1/sec)
                    java.util.List<net.minecraft.world.entity.LivingEntity> restingPredators = level.getEntitiesOfClass(
                        net.minecraft.world.entity.LivingEntity.class,
                        self.getBoundingBox().inflate(10.0),
                        e -> (e instanceof net.minecraft.world.entity.animal.feline.Cat ||
                              e instanceof net.minecraft.world.entity.animal.feline.Ocelot ||
                              e instanceof net.minecraft.world.entity.monster.Phantom) && e.isAlive()
                    );
                    if (!restingPredators.isEmpty()) {
                        self.setResting(false);
                        if (!isSilent) {
                            level.levelEvent(null, 1025, pos, 0);
                        }
                    }
                }
            } else {
                self.setResting(false);
                if (!isSilent) {
                    level.levelEvent(null, 1025, pos, 0);
                }
            }
            if (self.isResting() && !level.isBrightOutside() && self.getRandom().nextInt(200) == 0) {
                self.setResting(false);
            }
            ci.cancel();
            return;
        }

        // Flying mode: override vanilla random target calculation completely
        net.vanillaoutsider.betterbats.ai.BatFlightHelper.applyFlightForces(self);

        // Pitch-dark cave echolocation click & subtle sonic pulse
        if (level.getBrightness(LightLayer.SKY, pos) == 0 && level.getBrightness(LightLayer.BLOCK, pos) < 4) {
            if (self.getRandom().nextInt(90) == 0) {
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BAT_AMBIENT, net.minecraft.sounds.SoundSource.NEUTRAL, 0.35F, 1.8F + self.getRandom().nextFloat() * 0.3F);
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL, self.getX(), self.getY() + 0.1, self.getZ(), 2, 0.08, 0.08, 0.08, 0.02);
            }
        }

        Vec3 newMovement = self.getDeltaMovement();
        if (newMovement.lengthSqr() > 0.001) {
            float targetYaw = (float)(Mth.atan2(newMovement.z, newMovement.x) * (180.0 / Math.PI)) - 90.0F;
            float newYaw = Mth.approachDegrees(self.getYRot(), targetYaw, 12.0F);
            self.setYRot(newYaw);
            self.setYHeadRot(newYaw);
            self.setYBodyRot(newYaw);
            self.zza = 0.5F;
        }

        if (self.getRandom().nextInt(100) == 0 && net.vanillaoutsider.betterbats.ai.BatRoostHelper.isSuitableRoost(level, pos, above)) {
            self.setResting(true);
        }

        ci.cancel();
    }

    @Inject(method = "checkBatSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void betterbats$onCheckBatSpawnRules(
            EntityType<Bat> type, LevelAccessor level, EntitySpawnReason spawnReason, 
            BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        
        boolean isSurface = pos.getY() >= level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY();
        
        if (isSurface) {
            int skyLight = level.getBrightness(LightLayer.SKY, pos);
            if (skyLight <= 7 && level.getBlockState(pos.below()).is(BlockTags.BATS_SPAWNABLE_ON)) {
                cir.setReturnValue(Mob.checkMobSpawnRules(type, level, spawnReason, pos, random));
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterbats$onAddAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putInt("betterbats:guano_ticks", this.betterbats$guanoTicks);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterbats$onReadAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        this.betterbats$guanoTicks = input.getIntOr("betterbats:guano_ticks", 0);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterbats$onTick(CallbackInfo ci) {
        Bat self = (Bat)(Object)this;
        if (!self.level().isClientSide()) {
            self.setNoGravity(!self.isResting());
            if (self.isResting()) {
                this.betterbats$guanoTicks++;
                int threshold = net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(self.level(), BetterBatsFabric.BAT_GUANO_THRESHOLD);
                if (this.betterbats$guanoTicks >= threshold) {
                    this.betterbats$guanoTicks = 0;
                    BlockPos pos = self.blockPosition();
                    ServerLevel level = (ServerLevel) self.level();
                    
                    boolean fertilized = false;
                    for (int i = 1; i < 20; i++) {
                        BlockPos target = pos.below(i);
                        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(target);
                        if (!state.isAir()) {
                            if (state.getBlock() instanceof net.minecraft.world.level.block.FarmlandBlock) {
                                BlockPos cropPos = target.above();
                                net.minecraft.world.level.block.state.BlockState cropState = level.getBlockState(cropPos);
                                if (cropState.getBlock() instanceof BonemealableBlock crop) {
                                    if (crop.isValidBonemealTarget(level, cropPos, cropState)) {
                                        crop.performBonemeal(level, level.getRandom(), cropPos, cropState);
                                        level.levelEvent(2005, cropPos, 0);
                                        level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.05);
                                        fertilized = true;
                                    }
                                }
                            }
                            if (!fertilized) {
                                level.sendParticles(net.minecraft.core.particles.ParticleTypes.MYCELIUM, target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.01);
                                if (net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_DROP_GUANO_ITEM)) {
                                    net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                                        level,
                                        target.getX() + 0.5,
                                        target.getY() + 0.8,
                                        target.getZ() + 0.5,
                                        new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BONE_MEAL)
                                    );
                                    level.addFreshEntity(itemEntity);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
