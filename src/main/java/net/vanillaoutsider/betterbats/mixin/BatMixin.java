// Verified against: Bat.java (26.1.2)
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
    private LivingEntity betterbats$leader;
    @Unique
    private FlockType betterbats$flockType = FlockType.AERIAL;
    @Unique
    private FlockState betterbats$flockState;
    @Unique
    private int betterbats$guanoTicks = 0;
    @Unique
    private int betterbats$panicTicks = 0;
    @Unique
    private Vec3 betterbats$panicSource;

    @Override
    public LivingEntity getLeader() { return this.betterbats$leader; }

    @Override
    public boolean hasLeader() { return this.betterbats$leader != null; }

    @Override
    public void setLeader(LivingEntity leader) { this.betterbats$leader = leader; }

    @Override
    public int getGroupSize() {
        return this.betterbats$flockState != null ? this.betterbats$flockState.getMemberCount() : 1;
    }

    public int getMaxSpawnClusterSize() {
        Bat self = (Bat)(Object)this;
        return self.level().isClientSide() ? 5 : net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(self.level(), BetterBatsFabric.BAT_SWARM_SIZE);
    }


    @Override
    public FlockType getFlockType() { return this.betterbats$flockType; }

    @Override
    public FlockState getFlockState() { return this.betterbats$flockState; }

    @Override
    public void setFlockState(FlockState state) { this.betterbats$flockState = state; }

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
            ((MobAccessor)self).getGoalSelector().addGoal(3, new net.vanillaoutsider.betterbats.ai.BatFollowLeaderGoal(self));
            ((MobAccessor)self).getGoalSelector().addGoal(4, new net.vanillaoutsider.betterbats.ai.BatHuntLightGoal(self));
            ((MobAccessor)self).getGoalSelector().addGoal(5, new net.vanillaoutsider.betterbats.ai.BatDiveBombGoal(self));
        }
    }

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void betterbats$wakeUpAtNight(ServerLevel level, CallbackInfo ci) {
        Bat self = (Bat)(Object)this;
        if (self.isResting() && !level.isBrightOutside() && self.getRandom().nextInt(200) == 0) {
            self.setResting(false);
        }
        if (!self.isResting() && !this.hasLeader()) {
            net.vanillaoutsider.betterbats.ai.BatFlightHelper.applyFlightForces(self);
            Vec3 newMovement = self.getDeltaMovement();
            if (newMovement.lengthSqr() > 0.001) {
                float yRotD = (float)(Mth.atan2(newMovement.z, newMovement.x) * 180.0F / (float)Math.PI) - 90.0F;
                float rotDiff = Mth.wrapDegrees(yRotD - self.getYRot());
                self.zza = 0.5F;
                self.setYRot(self.getYRot() + rotDiff);
            }
        }
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void betterbats$cancelVanillaAiWhenFlocking(ServerLevel level, CallbackInfo ci) {
        if (this.hasLeader()) {
            Bat self = (Bat)(Object)this;
            Vec3 newMovement = self.getDeltaMovement();
            if (newMovement.lengthSqr() > 0.001) {
                float yRotD = (float)(Mth.atan2(newMovement.z, newMovement.x) * 180.0F / (float)Math.PI) - 90.0F;
                float rotDiff = Mth.wrapDegrees(yRotD - self.getYRot());
                self.zza = 0.5F;
                self.setYRot(self.getYRot() + rotDiff);
            }
            ci.cancel();
        }
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
                    net.minecraft.world.level.Level level = self.level();
                    
                    for (int i = 1; i < 20; i++) {
                        BlockPos target = pos.below(i);
                        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(target);
                        if (!state.isAir()) {
                            if (state.getBlock() instanceof net.minecraft.world.level.block.FarmlandBlock) {
                                BlockPos cropPos = target.above();
                                net.minecraft.world.level.block.state.BlockState cropState = level.getBlockState(cropPos);
                                if (cropState.getBlock() instanceof BonemealableBlock crop) {
                                    if (crop.isValidBonemealTarget(level, cropPos, cropState)) {
                                        crop.performBonemeal((ServerLevel)level, level.getRandom(), cropPos, cropState);
                                        level.levelEvent(2005, cropPos, 0);
                                    }
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
