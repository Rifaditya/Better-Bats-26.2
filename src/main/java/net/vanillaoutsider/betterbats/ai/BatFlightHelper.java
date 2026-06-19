package net.vanillaoutsider.betterbats.ai;

// Verified against: Level.java (26.2+), Bat.java (26.2+)
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

/**
 * Helper class to calculate and apply environmental forces to bats (ground/ceiling avoidance, light preferences).
 */
public class BatFlightHelper {
    public static void applyFlightForces(Bat bat) {
        if (bat.level().isClientSide() || bat.isResting()) {
            return;
        }

        Level level = bat.level();
        BlockPos myPos = bat.blockPosition();
        Vec3 newVelocity = bat.getDeltaMovement();

        // 1. Ground and Water Avoidance (Scan down 5 blocks)
        double groundForce = 0.0;
        for (int d = 1; d <= 5; d++) {
            BlockPos belowPos = myPos.below(d);
            if (level.getBlockState(belowPos).blocksMotion() || !level.getFluidState(belowPos).isEmpty()) {
                // Stronger upward force the closer the bat is to the surface/fluid
                groundForce = (6 - d) * 0.035;
                break;
            }
        }
        if (groundForce > 0.0) {
            newVelocity = newVelocity.add(0.0, groundForce, 0.0);
        }

        // 2. Ceiling Avoidance (Scan up 3 blocks)
        double ceilingForce = 0.0;
        for (int d = 1; d <= 3; d++) {
            BlockPos abovePos = myPos.above(d);
            if (level.getBlockState(abovePos).blocksMotion()) {
                // Stronger downward force the closer the bat is to the ceiling
                ceilingForce = (4 - d) * -0.035;
                break;
            }
        }
        if (ceilingForce < 0.0) {
            newVelocity = newVelocity.add(0.0, ceilingForce, 0.0);
        }

        // 3. Light Preference (Day time seeks lower sky light, Night time seeks higher sky light)
        BlockPos bestPos = null;
        int bestSkyLight = level.isBrightOutside() ? 16 : -1;

        for (int i = 0; i < 8; i++) {
            int dx = level.getRandom().nextInt(17) - 8;
            int dy = level.getRandom().nextInt(9) - 4;
            int dz = level.getRandom().nextInt(17) - 8;
            BlockPos targetPos = myPos.offset(dx, dy, dz);
            if (level.isEmptyBlock(targetPos)) {
                int skyLight = level.getBrightness(LightLayer.SKY, targetPos);
                if (level.isBrightOutside()) {
                    if (skyLight < bestSkyLight) {
                        bestSkyLight = skyLight;
                        bestPos = targetPos;
                    }
                } else {
                    if (skyLight > bestSkyLight) {
                        bestSkyLight = skyLight;
                        bestPos = targetPos;
                    }
                }
            }
        }

        if (bestPos != null) {
            Vec3 targetVec = Vec3.atCenterOf(bestPos);
            Vec3 steerDir = targetVec.subtract(bat.position());
            if (steerDir.lengthSqr() > 0.001) {
                newVelocity = newVelocity.add(steerDir.normalize().scale(0.025));
            }
        }

        // 4. Clamp velocity to maximum speed
        double maxSpeed = 0.4;
        if (newVelocity.lengthSqr() > maxSpeed * maxSpeed) {
            newVelocity = newVelocity.normalize().scale(maxSpeed);
        }

        bat.setDeltaMovement(newVelocity);
    }
}
