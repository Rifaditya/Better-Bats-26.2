package net.vanillaoutsider.betterbats.ai;

// Verified against: Level.java (26.2+), Bat.java (26.2+)
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

import java.util.List;

/**
 * Helper class to calculate and apply environmental forces and individual BOIDs flocking to bats.
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

        // 3. True BOIDs Math (Cohesion, Alignment, Separation from local neighbors)
        List<Bat> neighbors = level.getEntitiesOfClass(
            Bat.class, 
            bat.getBoundingBox().inflate(12.0), 
            b -> b != bat && b.isAlive() && !b.isResting()
        );

        if (!neighbors.isEmpty()) {
            double alignmentWeight = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_ALIGNMENT) * 0.01;
            double cohesionWeight = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_COHESION) * 0.01;
            double separationWeight = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SEPARATION) * 0.01;

            Vec3 avgPos = Vec3.ZERO;
            Vec3 avgVel = Vec3.ZERO;
            Vec3 separationVec = Vec3.ZERO;
            int separationCount = 0;

            for (Bat neighbor : neighbors) {
                avgPos = avgPos.add(neighbor.position());
                avgVel = avgVel.add(neighbor.getDeltaMovement());

                double distSqr = bat.distanceToSqr(neighbor);
                if (distSqr < 4.0 && distSqr > 0.0001) {
                    Vec3 diff = bat.position().subtract(neighbor.position());
                    separationVec = separationVec.add(diff.normalize().scale(1.0 / Math.sqrt(distSqr)));
                    separationCount++;
                }
            }

            // Cohesion: pull towards center of mass of neighbors
            avgPos = avgPos.scale(1.0 / neighbors.size());
            Vec3 cohesionDir = avgPos.subtract(bat.position());
            if (cohesionDir.lengthSqr() > 0.001) {
                newVelocity = newVelocity.add(cohesionDir.normalize().scale(cohesionWeight));
            }

            // Alignment: align velocity with neighbors' average velocity
            avgVel = avgVel.scale(1.0 / neighbors.size());
            if (avgVel.lengthSqr() > 0.001) {
                newVelocity = newVelocity.add(avgVel.normalize().scale(alignmentWeight));
            }

            // Separation: steer away from neighbors that are too close
            if (separationCount > 0) {
                separationVec = separationVec.scale(1.0 / separationCount);
                if (separationVec.lengthSqr() > 0.001) {
                    newVelocity = newVelocity.add(separationVec.normalize().scale(separationWeight));
                }
            }
        }

        // 4. Light Preference (Day time seeks lower sky light, Night time seeks higher sky light)
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

        // 5. Clamp velocity to maximum speed
        double maxSpeed = 0.4;
        if (newVelocity.lengthSqr() > maxSpeed * maxSpeed) {
            newVelocity = newVelocity.normalize().scale(maxSpeed);
        }

        bat.setDeltaMovement(newVelocity);
    }
}
