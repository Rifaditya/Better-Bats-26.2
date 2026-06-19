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

        // 1. Ground and Water Avoidance (Scan down 5 blocks) - ALWAYS APPLIED
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

        // 2. Ceiling Avoidance (Scan up 3 blocks) - ALWAYS APPLIED
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

        // Check if the bat has an active custom goal or is panicked.
        // If so, we skip Boids, Wandering, and Day/Night Environment Steering, but keep ground/ceiling avoidance and basic clamp.
        boolean hasActiveGoal = false;
        if (bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            if (accessor.betterbats$isGoalActive() || accessor.betterbats$isPanicked()) {
                hasActiveGoal = true;
            }
        }

        if (!hasActiveGoal) {
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

            // 4. Random Organic Wandering (steers the flock dynamically day/night)
            double wanderStrength = 0.05;
            double rx = (level.getRandom().nextDouble() - 0.5) * wanderStrength;
            double rz = (level.getRandom().nextDouble() - 0.5) * wanderStrength;
            newVelocity = newVelocity.add(rx, 0.0, rz);

            // 5. Day/Night Environment Steering (Cave / Surface seek)
            Vec3 envSteer = Vec3.ZERO;
            boolean isDay = level.isBrightOutside();
            int mySkyLight = level.getBrightness(LightLayer.SKY, myPos);

            if (isDay) {
                if (mySkyLight > 0) {
                    // Search for a dark cover block (canSeeSky is false) in a wide 24-block range
                    BlockPos darkPos = null;
                    int lowestSkyLight = mySkyLight;
                    int searchRangeH = 24;
                    for (int i = 0; i < 60; i++) {
                        int dx = level.getRandom().nextInt(searchRangeH * 2 + 1) - searchRangeH;
                        int dy = level.getRandom().nextInt(25) - 16; // Bias search downwards (-16 to +8)
                        int dz = level.getRandom().nextInt(searchRangeH * 2 + 1) - searchRangeH;
                        BlockPos check = myPos.offset(dx, dy, dz);
                        if (level.isEmptyBlock(check) && !level.canSeeSky(check)) {
                            int light = level.getBrightness(LightLayer.SKY, check);
                            if (light < lowestSkyLight) {
                                lowestSkyLight = light;
                                darkPos = check;
                            }
                        }
                    }
                    if (darkPos != null) {
                        Vec3 toTarget = Vec3.atCenterOf(darkPos).subtract(bat.position());
                        double dx = toTarget.x;
                        double dz = toTarget.z;
                        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                        double steerX = 0.0;
                        double steerZ = 0.0;
                        if (horizontalDist > 0.01) {
                            steerX = (dx / horizontalDist) * 0.15;
                            steerZ = (dz / horizontalDist) * 0.15;
                        }
                        double steerY = 0.0;
                        if (Math.abs(toTarget.y) > 0.01) {
                            steerY = Math.signum(toTarget.y) * 0.12;
                        }
                        envSteer = new Vec3(steerX, steerY, steerZ);
                    } else {
                        // No cover found: steer downward and randomly horizontally to descend to the ground
                        double wrx = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        double wrz = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        envSteer = new Vec3(wrx, -0.12, wrz);
                    }
                }
            } else {
                if (mySkyLight < 15) {
                    // Search for a brighter spot/exit in a wide 24-block range
                    BlockPos brightPos = null;
                    int highestSkyLight = mySkyLight;
                    int searchRangeH = 24;
                    for (int i = 0; i < 60; i++) {
                        int dx = level.getRandom().nextInt(searchRangeH * 2 + 1) - searchRangeH;
                        int dy = level.getRandom().nextInt(25) - 8; // Bias search upwards (-8 to +16)
                        int dz = level.getRandom().nextInt(searchRangeH * 2 + 1) - searchRangeH;
                        BlockPos check = myPos.offset(dx, dy, dz);
                        if (level.isEmptyBlock(check)) {
                            int light = level.getBrightness(LightLayer.SKY, check);
                            if (light > highestSkyLight) {
                                highestSkyLight = light;
                                brightPos = check;
                            }
                        }
                    }
                    if (brightPos != null) {
                        Vec3 toTarget = Vec3.atCenterOf(brightPos).subtract(bat.position());
                        double dx = toTarget.x;
                        double dz = toTarget.z;
                        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                        double steerX = 0.0;
                        double steerZ = 0.0;
                        if (horizontalDist > 0.01) {
                            steerX = (dx / horizontalDist) * 0.15;
                            steerZ = (dz / horizontalDist) * 0.15;
                        }
                        double steerY = 0.0;
                        if (Math.abs(toTarget.y) > 0.01) {
                            steerY = Math.signum(toTarget.y) * 0.08;
                        }
                        envSteer = new Vec3(steerX, steerY, steerZ);
                    } else {
                        // No brighter spot found: steer upward and randomly horizontally to escape pockets
                        double wrx = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        double wrz = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        envSteer = new Vec3(wrx, 0.08, wrz);
                    }
                }
            }
            newVelocity = newVelocity.add(envSteer);

            // 6. Enforce Minimum Speed ("never stop" flying/moving)
            double minSpeed = 0.15;
            if (newVelocity.lengthSqr() < minSpeed * minSpeed) {
                if (newVelocity.lengthSqr() > 0.001) {
                    newVelocity = newVelocity.normalize().scale(minSpeed);
                } else {
                    double angle = level.getRandom().nextDouble() * Math.PI * 2.0;
                    newVelocity = new Vec3(Math.cos(angle) * minSpeed, minSpeed, Math.sin(angle) * minSpeed);
                }
            }
        }

        // 7. Clamp velocity to maximum speed - ALWAYS APPLIED
        double maxSpeed = 0.4;
        if (newVelocity.lengthSqr() > maxSpeed * maxSpeed) {
            newVelocity = newVelocity.normalize().scale(maxSpeed);
        }

        bat.setDeltaMovement(newVelocity);
    }
}
