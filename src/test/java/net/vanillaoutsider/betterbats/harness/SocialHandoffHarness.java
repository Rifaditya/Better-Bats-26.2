// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.harness;

/**
 * SocialHandoffHarness: Standardized test harness for 3-Tier Deep Behavioral Probing in Better Bats.
 *
 * Tier 1: Multi-Entity Social Handoff Verification (Flock streaming, cave roosting clustering, disturbance panic cascade)
 * Tier 2: Full State-Machine Lifecycle Probing (Idle -> Alerted -> Active -> Cooldown -> Reset)
 * Tier 3: Negative Parity & Anti-Goal Invariance (dead, resting vs flight, panicked vs resting, guano zeroing, threat wakes)
 */
public class SocialHandoffHarness {

    /**
     * Creates a clean test cluster with standard follower spacing.
     */
    public static BehavioralStepRunner setupCluster(int followerCount, double spacing) {
        SimulatedEntityGroup group = SimulatedEntityGroup.create(followerCount, spacing);
        return BehavioralStepRunner.of(group);
    }

    /**
     * Creates a test cluster equipped with continuous negative invariant watchdogs.
     * Guarantees that invalid entity states can NEVER violate behavioral contracts at any tick.
     */
    public static BehavioralStepRunner setupClusterWithNegativeInvariants(int followerCount, double spacing) {
        SimulatedEntityGroup group = SimulatedEntityGroup.create(followerCount, spacing);
        return attachNegativeInvariants(BehavioralStepRunner.of(group));
    }

    /**
     * Creates a roosting cluster where bats start resting on ceiling with continuous negative invariant watchdogs.
     */
    public static BehavioralStepRunner setupRoostCluster(int followerCount, double spacing) {
        SimulatedEntityGroup group = SimulatedEntityGroup.createRoostCluster(followerCount, spacing);
        return attachNegativeInvariants(BehavioralStepRunner.of(group));
    }

    private static BehavioralStepRunner attachNegativeInvariants(BehavioralStepRunner runner) {
        return runner
                .addInvariant("Dead bats must never enter ACTIVE or ALERTED state", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isDead)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Dead bats must never be resting or panicked", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isDead)
                                .noneMatch(e -> e.isResting() || e.isPanicked()))
                .addInvariant("Resting bats must never participate in flight flocking (ACTIVE or ALERTED)", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isResting)
                                .noneMatch(e -> e.getState() == SimulatedEntity.State.ACTIVE || e.getState() == SimulatedEntity.State.ALERTED))
                .addInvariant("Panicked bats must never remain resting", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isPanicked)
                                .noneMatch(SimulatedEntity::isResting))
                .addInvariant("Panicked bats must never accumulate guano (guanoTicks == 0)", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isPanicked)
                                .allMatch(e -> e.getGuanoTicks() == 0))
                .addInvariant("Panicked bats must never participate in flight flocking ('flock_stream')", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isPanicked)
                                .noneMatch(e -> "flock_stream".equals(e.getActiveSignal())))
                .addInvariant("Predator-threatened bats must never remain resting", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isPredatorNearby)
                                .noneMatch(SimulatedEntity::isResting))
                .addInvariant("Awake player nearby must wake resting bats", g ->
                        g.getAllEntities().stream().filter(SimulatedEntity::isPlayerAwakeNearby)
                                .noneMatch(SimulatedEntity::isResting));
    }
}
