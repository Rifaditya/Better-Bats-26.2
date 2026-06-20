package net.vanillaoutsider.betterbats.ai;

// Verified against: Level.java (26.2+), Bat.java (26.2+), Heightmap.java (26.2+)
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

import java.util.List;

/**
 * Helper class to calculate and apply environmental forces and individual BOIDs flocking to bats.
 * Includes altitude cap, night comfort zone, two-phase daytime cave-return logic,
 * and 1.1.16 organic twilight funneling.
 */
public class BatFlightHelper {

    /** Maximum blocks above terrain surface a bat may fly (hard cap). */
    private static final int MAX_ALTITUDE_ABOVE_SURFACE = 30;

    /** Night comfort zone: minimum blocks above terrain surface. */
    private static final int NIGHT_COMFORT_MIN = 5;

    /** Night comfort zone: maximum blocks above terrain surface. */
    private static final int NIGHT_COMFORT_MAX = 20;

    /** Below this height above surface, daytime bats switch from descending to cave-probing. */
    private static final int DAY_PROBE_THRESHOLD = 15;

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

        // 2.5. Hard Altitude Cap - ALWAYS APPLIED (even when a goal is active)
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, myPos.getX(), myPos.getZ());
        double currentY = bat.getY();
        int maxAltitude = surfaceY + MAX_ALTITUDE_ABOVE_SURFACE;
        if (currentY > maxAltitude) {
            double excess = currentY - maxAltitude;
            // Strong proportional downward force — scales with distance above cap
            double capForce = -Math.min(0.25, excess * 0.05);
            newVelocity = newVelocity.add(0.0, capForce, 0.0);
        }

        // Check if the bat has an active custom goal or is panicked.
        boolean hasActiveGoal = false;
        if (bat instanceof net.vanillaoutsider.betterbats.BatStateAccessor accessor) {
            if (accessor.betterbats$isGoalActive() || accessor.betterbats$isPanicked()) {
                hasActiveGoal = true;
            }
        }

        if (!hasActiveGoal) {
            // Check for Twilight (Dusk 12000-14000, Dawn 22000-24000)
            long dayTime = level.getDefaultClockTime() % 24000;
            boolean isTwilight = (dayTime >= 12000 && dayTime <= 14000) || (dayTime >= 22000 && dayTime <= 24000);

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

                // 1.1.16: Twilight Funneling/Streaming
                if (isTwilight && level.canSeeSky(myPos)) {
                    // Bats stream together closely when entering/leaving caves
                    alignmentWeight *= 2.5;
                    cohesionWeight *= 2.5;
                    separationWeight *= 0.4;
                }

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

                // Cohesion
                avgPos = avgPos.scale(1.0 / neighbors.size());
                Vec3 cohesionDir = avgPos.subtract(bat.position());
                if (cohesionDir.lengthSqr() > 0.001) {
                    newVelocity = newVelocity.add(cohesionDir.normalize().scale(cohesionWeight));
                }

                // Alignment
                avgVel = avgVel.scale(1.0 / neighbors.size());
                if (avgVel.lengthSqr() > 0.001) {
                    newVelocity = newVelocity.add(avgVel.normalize().scale(alignmentWeight));
                }

                // Separation
                if (separationCount > 0) {
                    separationVec = separationVec.scale(1.0 / separationCount);
                    if (separationVec.lengthSqr() > 0.001) {
                        newVelocity = newVelocity.add(separationVec.normalize().scale(separationWeight));
                    }
                }
            }

            // 4. Random Organic Wandering
            double wanderStrength = 0.05;
            double rx = (level.getRandom().nextDouble() - 0.5) * wanderStrength;
            double rz = (level.getRandom().nextDouble() - 0.5) * wanderStrength;
            newVelocity = newVelocity.add(rx, 0.0, rz);

            // 5. Day/Night Environment Steering (Cave / Surface seek)
            Vec3 envSteer = Vec3.ZERO;
            boolean isDay = level.isBrightOutside();
            double heightAboveSurface = currentY - surfaceY;

            if (isDay) {
                // ── DAYTIME: Two-phase descent-then-seek ──
                if (level.canSeeSky(myPos)) {
                    if (heightAboveSurface > DAY_PROBE_THRESHOLD) {
                        // Phase 1: Far above surface — smooth parabolic descent
                        double descentForce = -Mth.clamp(heightAboveSurface * 0.006, 0.05, 0.18);
                        double wrx = (level.getRandom().nextDouble() - 0.5) * 0.08;
                        double wrz = (level.getRandom().nextDouble() - 0.5) * 0.08;
                        envSteer = new Vec3(wrx, descentForce, wrz);
                    } else {
                        // Phase 2: Near surface — focused probe for dark/covered spots
                        BlockPos darkPos = null;
                        int lowestSkyLight = level.getBrightness(LightLayer.SKY, myPos);
                        int searchRangeH = 16;
                        for (int i = 0; i < 40; i++) {
                            int dx = level.getRandom().nextInt(searchRangeH * 2 + 1) - searchRangeH;
                            int dy = level.getRandom().nextInt(20) - 14; // Heavy downward bias (-14 to +5)
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
                            // No cover found nearby: smooth downward steer
                            double wrx = (level.getRandom().nextDouble() - 0.5) * 0.1;
                            double wrz = (level.getRandom().nextDouble() - 0.5) * 0.1;
                            envSteer = new Vec3(wrx, -0.10, wrz);
                        }
                    }
                }
            } else {
                // ── NIGHTTIME: Exit caves, then roam within comfort zone ──
                boolean canSeeSky = level.canSeeSky(myPos);

                if (!canSeeSky) {
                    // Underground / in cave — seek exit
                    int mySkyLight = level.getBrightness(LightLayer.SKY, myPos);
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
                            // Smooth climb force
                            steerY = Math.signum(toTarget.y) * Mth.clamp(Math.abs(toTarget.y) * 0.01, 0.04, 0.12);
                        }
                        envSteer = new Vec3(steerX, steerY, steerZ);
                    } else {
                        // Smooth escape upwards
                        double wrx = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        double wrz = (level.getRandom().nextDouble() - 0.5) * 0.1;
                        envSteer = new Vec3(wrx, 0.06, wrz);
                    }
                } else {
                    // Already outside in open sky — smooth comfort zone bounding (Parabolic)
                    double steerY = 0.0;
                    if (heightAboveSurface > NIGHT_COMFORT_MAX) {
                        double excess = heightAboveSurface - NIGHT_COMFORT_MAX;
                        steerY = -Mth.clamp(excess * 0.01, 0.01, 0.08);
                    } else if (heightAboveSurface < NIGHT_COMFORT_MIN) {
                        double deficit = NIGHT_COMFORT_MIN - heightAboveSurface;
                        steerY = Mth.clamp(deficit * 0.01, 0.01, 0.08);
                    }
                    if (steerY != 0.0) {
                        envSteer = new Vec3(0.0, steerY, 0.0);
                    }
                }
            }
            newVelocity = newVelocity.add(envSteer);

            // 6. Enforce Minimum Speed
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
