// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.test;

import net.vanillaoutsider.betterbats.harness.BehavioralStepRunner;
import net.vanillaoutsider.betterbats.harness.SimulatedEntity;
import net.vanillaoutsider.betterbats.harness.SimulatedEntityGroup;
import net.vanillaoutsider.betterbats.harness.SocialHandoffHarness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BatDeepBehavioralTest: Pillar 4 Headless Deep Behavioral Probing Test Suite for Better Bats.
 *
 * Implements the studio standard 3-Tier Behavioral Probing:
 * - Tier 1: Multi-Entity Social Handoff Verification (Twilight flock streaming, cave roost clustering, disturbance panic cascade)
 * - Tier 2: Full State-Machine Lifecycle Probing (IDLE -> ALERTED -> ACTIVE -> COOLDOWN -> IDLE)
 * - Tier 3: Negative Parity & Anti-Goal Invariance (invalid states rejected, mid-action & mid-countdown aborts, guano zeroing)
 */
@DisplayName("Pillar 4: Deep Behavioral Probing & Social Handoff Harness (Better Bats)")
public class BatDeepBehavioralTest {

    @Test
    @DisplayName("Tier 1: Multi-Entity Twilight Flock Streaming Propagation & Staggered Convergence")
    void testFlockTwilightStreamingPropagationAndStaggeredConvergence() {
        // Setup cluster: 1 lead bat + 4 followers at 6m spacing with continuous negative invariant watchdogs
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(4, 6.0);
        SimulatedEntityGroup group = runner.getGroup();
        SimulatedEntity leadBat = group.getInitiator();

        // 1. Initial State: All bats start IDLE and flying
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.IDLE, "Lead bat must start IDLE")
                .assertAllFollowers(e -> e.getState() == SimulatedEntity.State.IDLE, "Followers must start IDLE")
                .assertRestingCount(0, "All bats must start in flight (resting == false)");

        // 2. Lead bat initiates twilight streaming flight (60 ticks duration, 100 ticks cooldown)
        boolean initiated = leadBat.initiateBehavior("flock_stream", 60, 100);
        assertTrue(initiated, "Lead bat must initiate flock streaming flight");
        assertEquals(SimulatedEntity.State.ACTIVE, leadBat.getState());
        assertEquals(60, leadBat.getStateTicks());

        // 3. Broadcast twilight stream signal with staggered delay to followers within 16m
        // Follower 0: delay 8 ticks
        // Follower 1: delay 14 ticks
        // Follower 2: delay 20 ticks
        // Follower 3: delay 26 ticks
        int accepted = group.broadcastSignal(
                leadBat,
                "flock_stream",
                16.0,
                idx -> 8 + idx * 6,
                60,
                100
        );
        assertEquals(4, accepted, "All 4 eligible flying followers must accept flock stream signal");

        // 4. Followers start in ALERTED state with staggered delays
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 8, "Follower 0 delay 8")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 14, "Follower 1 delay 14")
                .assertFollower(2, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 20, "Follower 2 delay 20")
                .assertFollower(3, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 26, "Follower 3 delay 26");

        // 5. Step 8 ticks: Follower 0 activates
        runner.step(8);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60, "Follower 0 active at tick 8")
                .assertFollower(1, e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 6, "Follower 1 6 ticks remaining")
                .assertInitiator(e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 52, "Lead bat still active");

        // 6. Step 6 more ticks (total tick 14): Follower 1 activates
        runner.step(6);
        runner.assertFollower(1, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60, "Follower 1 active at tick 14");

        // 7. Step 6 more ticks (total tick 20): Follower 2 activates
        runner.step(6);
        runner.assertFollower(2, e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 60, "Follower 2 active at tick 20");

        // 8. Step 6 more ticks (total tick 26): Follower 3 activates
        // FULL FLOCK CONVERGENCE: All 5 bats streaming concurrently!
        runner.step(6);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ACTIVE, "All 4 followers active at tick 26")
                .assertInitiator(e -> e.getState() == SimulatedEntity.State.ACTIVE && e.getStateTicks() == 34, "Lead bat active at tick 26")
                .assertTotalCountInState(SimulatedEntity.State.ACTIVE, 5, "All 5 bats streaming together at convergence");

        // 9. Step 34 more ticks (total tick 60): Lead bat finishes and transitions to COOLDOWN
        runner.step(34);
        runner.assertInitiator(e -> e.getState() == SimulatedEntity.State.COOLDOWN, "Lead bat in COOLDOWN at tick 60");
        runner.assertFollowersCountInState(SimulatedEntity.State.ACTIVE, 4, "All 4 followers still actively streaming");

        // 10. Step 8 more ticks (total tick 68): Follower 0 finishes and transitions to COOLDOWN
        runner.step(8);
        runner.assertFollower(0, e -> e.getState() == SimulatedEntity.State.COOLDOWN, "Follower 0 in COOLDOWN at tick 68");

        // 11. Step 18 more ticks (total tick 86): All remaining followers complete streaming
        runner.step(18);
        runner.assertTotalCountInState(SimulatedEntity.State.ACTIVE, 0, "Zero bats actively streaming after tick 86")
                .assertTotalCountInState(SimulatedEntity.State.COOLDOWN, 5, "All 5 bats cleanly in COOLDOWN");
    }

    @Test
    @DisplayName("Tier 1: Multi-Entity Cave Roost Clustering Choreography & Guano Accumulation")
    void testCaveRoostClusteringChoreography() {
        // Setup flying cluster of 5 bats (1 pioneer + 4 followers)
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(4, 5.0);
        SimulatedEntityGroup group = runner.getGroup();
        SimulatedEntity pioneer = group.getInitiator();

        // 1. Pioneer bat discovers dark cave ceiling and settles into roost
        boolean roosted = pioneer.triggerRoost(true);
        assertTrue(roosted, "Pioneer bat must successfully roost on solid ceiling");
        assertTrue(pioneer.isResting(), "Pioneer is resting");
        assertEquals("roosting", pioneer.getActiveSignal());
        assertEquals(0.0, pioneer.getVx());

        // 2. Flying followers detect resting peer and receive roost clustering signal
        int accepted = group.broadcastSignal(
                pioneer,
                "roost_cluster",
                16.0,
                idx -> 5 + idx * 5, // Staggered arrival delays: 5, 10, 15, 20 ticks
                10,
                0
        );
        assertEquals(4, accepted, "All 4 flying bats within 16m accept roost cluster signal");

        // 3. Step 5 ticks: Follower 0 arrives at ceiling and settles
        runner.step(5);
        SimulatedEntity f0 = group.getFollowers().get(0);
        assertEquals(SimulatedEntity.State.ACTIVE, f0.getState());
        f0.triggerRoost(true);
        assertTrue(f0.isResting());

        // 4. Step 5 ticks (tick 10): Follower 1 arrives and settles
        runner.step(5);
        SimulatedEntity f1 = group.getFollowers().get(1);
        f1.triggerRoost(true);
        assertTrue(f1.isResting());

        // 5. Step 5 ticks (tick 15): Follower 2 arrives and settles
        runner.step(5);
        SimulatedEntity f2 = group.getFollowers().get(2);
        f2.triggerRoost(true);
        assertTrue(f2.isResting());

        // 6. Step 5 ticks (tick 20): Follower 3 arrives and settles
        // FULL ROOST CONVERGENCE: All 5 bats settled into resting cluster
        runner.step(5);
        SimulatedEntity f3 = group.getFollowers().get(3);
        f3.triggerRoost(true);
        runner.assertRestingCount(5, "All 5 bats must be settled in resting roost cluster at tick 20");

        // 7. Advance simulation 50 ticks while roosting: Guano accumulation ticks upward
        runner.step(50);
        runner.assertRestingCount(5, "All 5 bats remain resting peacefully");
        // Pioneer has been resting for 70 ticks (20 + 50)
        assertEquals(70, pioneer.getGuanoTicks(), "Pioneer accumulated 70 guano ticks");
        // Follower 3 has been resting for 50 ticks
        assertEquals(50, f3.getGuanoTicks(), "Follower 3 accumulated 50 guano ticks");
        runner.assertGuanoTicksAllRestingAtLeast(50, "All cluster members have accumulated at least 50 guano ticks");
    }

    @Test
    @DisplayName("Tier 1: Multi-Entity Disturbance Panic Cascade & Guano Timer Wipe")
    void testHerdPanicDisturbanceCascade() {
        // Setup cluster: 3 resting bats on ceiling + 2 flying bats nearby
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(4, 5.0);
        SimulatedEntityGroup group = runner.getGroup();

        // Put initiator and first two followers to roost with pre-accumulated guano
        SimulatedEntity b0 = group.getInitiator();
        SimulatedEntity b1 = group.getFollowers().get(0);
        SimulatedEntity b2 = group.getFollowers().get(1);
        SimulatedEntity b3 = group.getFollowers().get(2); // flying
        SimulatedEntity b4 = group.getFollowers().get(3); // flying

        b0.triggerRoost(true);
        b1.triggerRoost(true);
        b2.triggerRoost(true);

        b0.setGuanoTicks(300);
        b1.setGuanoTicks(250);
        b2.setGuanoTicks(180);

        runner.assertRestingCount(3, "3 bats resting on ceiling before disturbance");

        // Disturbance event: Explosion / loud sprint vibration occurs at (0, 60, 0) within 16m
        int panickedCount = group.broadcastPanic(0.0, 60.0, 0.0, 16.0);
        assertEquals(5, panickedCount, "All 5 bats within 16m radius must receive panic signal");

        // Assert Handoff & Negative Parity:
        // 1. ALL resting bats immediately wake up
        runner.assertRestingCount(0, "ALL resting bats must immediately wake up upon disturbance");
        // 2. ALL 5 bats are panicked
        runner.assertPanickedCount(5, "ALL 5 bats must be in panicked state");
        // 3. Guano accumulation timers are strictly wiped to 0
        assertEquals(0, b0.getGuanoTicks(), "Guano ticks wiped to 0 on b0");
        assertEquals(0, b1.getGuanoTicks(), "Guano ticks wiped to 0 on b1");
        assertEquals(0, b2.getGuanoTicks(), "Guano ticks wiped to 0 on b2");

        // 4. Directional scatter: Bats fly away from disturbance source (Y=60 vs bats at Y=64 -> positive vy)
        assertTrue(b0.getVy() > 0, "Bat flies upward/away from disturbance");

        // 5. Advance 100 ticks: Panic state decays and clears across all bats
        runner.step(100);
        runner.assertPanickedCount(0, "Zero bats panicked after 100 ticks of panic flight");
        runner.assertTotalCountInState(SimulatedEntity.State.IDLE, 5, "All bats returned cleanly to IDLE");
    }

    @Test
    @DisplayName("Tier 2: Full State-Machine Lifecycle Probing (Idle -> Alerted -> Active -> Cooldown -> Reset)")
    void testFlockStreamingFullLifecycle() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 6.0);
        SimulatedEntity leadBat = runner.getGroup().getInitiator();
        SimulatedEntity follower = runner.getGroup().getFollowers().get(0);

        leadBat.initiateBehavior("flock_stream", 60, 100);
        int accepted = runner.getGroup().broadcastSignal(leadBat, "flock_stream", 16.0, 10, 0);
        assertEquals(1, accepted);

        // State 1: ALERTED (delay = 10 ticks)
        assertEquals(SimulatedEntity.State.ALERTED, follower.getState());
        assertEquals(10, follower.getDelayTicks());

        // State 2: Transition to ACTIVE at tick 10
        runner.step(10);
        assertEquals(SimulatedEntity.State.ACTIVE, follower.getState());
        assertEquals(60, follower.getStateTicks());

        // State 3: Transition to COOLDOWN at tick 70 (10 + 60)
        runner.step(60);
        assertEquals(SimulatedEntity.State.COOLDOWN, follower.getState());
        assertEquals(100, follower.getCooldownTicks());

        // State 4: Transition to IDLE at tick 170 (70 + 100)
        runner.step(100);
        assertEquals(SimulatedEntity.State.IDLE, follower.getState());
        assertNull(follower.getActiveSignal(), "Active signal must be null after clean lifecycle reset");
    }

    @Test
    @DisplayName("Tier 2: Full Panic State Lifecycle & Decay Probing")
    void testPanicLifecycleAndDecay() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity bat = runner.getGroup().getInitiator();

        assertEquals(SimulatedEntity.State.IDLE, bat.getState());
        assertFalse(bat.isPanicked());

        // Trigger panic at (10, 64, 10)
        bat.panic(10.0, 64.0, 10.0);
        assertTrue(bat.isPanicked());
        assertEquals(100, bat.getPanicTicks());
        assertEquals("panic", bat.getActiveSignal());
        assertTrue(bat.getVx() < 0, "X velocity directed away from positive X source");

        // Advance 50 ticks: Still panicked with 50 ticks remaining
        runner.step(50);
        assertTrue(bat.isPanicked());
        assertEquals(50, bat.getPanicTicks());

        // Advance 50 more ticks: Panic clears completely
        runner.step(50);
        assertFalse(bat.isPanicked());
        assertEquals(0, bat.getPanicTicks());
        assertNull(bat.getActiveSignal());
        assertEquals(SimulatedEntity.State.IDLE, bat.getState());
    }

    @Test
    @DisplayName("Tier 2: Roosting & Guano Accumulation Full Lifecycle")
    void testRoostAndGuanoLifecycle() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(1, 5.0);
        SimulatedEntity bat = runner.getGroup().getInitiator();

        // 1. In flight: guanoTicks stays 0
        runner.step(20);
        assertEquals(0, bat.getGuanoTicks());

        // 2. Roost: guanoTicks increments on every tick
        bat.triggerRoost(true);
        runner.step(100);
        assertEquals(100, bat.getGuanoTicks());

        // 3. Wake up: guanoTicks stops incrementing
        bat.wakeUp();
        assertFalse(bat.isResting());
        runner.step(20);
        assertEquals(100, bat.getGuanoTicks(), "Guano does not increment while flying");

        // 4. Reset: guano ticks clear
        bat.resetGuanoTicks();
        assertEquals(0, bat.getGuanoTicks());
    }

    @Test
    @DisplayName("Tier 3: Negative Parity & Anti-Goal Invariance (All Invalid States Strictly Reject Flight Signals)")
    void testNegativeParityInvalidStateRejections() {
        // Setup 5 followers with continuous negative invariant watchdogs
        BehavioralStepRunner runner = SocialHandoffHarness.setupClusterWithNegativeInvariants(5, 5.0);
        SimulatedEntityGroup group = runner.getGroup();

        SimulatedEntity eligibleFollower = group.getFollowers().get(0);
        SimulatedEntity deadFollower = group.getFollowers().get(1);
        SimulatedEntity restingFollower = group.getFollowers().get(2);
        SimulatedEntity panickedFollower = group.getFollowers().get(3);
        SimulatedEntity predatorThreatFollower = group.getFollowers().get(4);

        // Apply invalid negative states
        deadFollower.setDead(true);
        restingFollower.triggerRoost(true);
        panickedFollower.panic(0, 0, 0);
        predatorThreatFollower.setPredatorNearby(true);

        SimulatedEntity leadBat = group.getInitiator();
        leadBat.initiateBehavior("flock_stream", 60, 100);

        int accepted = group.broadcastSignal(leadBat, "flock_stream", 16.0, 10, 0);

        // Anti-Goal: Exactly 1 entity (eligibleFollower) accepted the flight stream signal
        assertEquals(1, accepted, "Only eligible, non-disqualified bats must accept flight signals");
        assertEquals(SimulatedEntity.State.ALERTED, eligibleFollower.getState());

        // All disqualified bats remain strictly IDLE or PANIC
        assertEquals(SimulatedEntity.State.IDLE, deadFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, restingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, predatorThreatFollower.getState());

        // Advance simulation: Continuous invariant watchdogs enforce 0 violations on every tick
        runner.step(25);
        assertEquals(SimulatedEntity.State.ACTIVE, eligibleFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, deadFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, restingFollower.getState());
        assertEquals(SimulatedEntity.State.IDLE, predatorThreatFollower.getState());
    }

    @Test
    @DisplayName("Tier 3: Mid-Action Interruption Aborts Flocking Immediately (Death, Roosting, Panic)")
    void testMidActionInterruptionAbortsFlockingImmediately() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(3, 5.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        initiator.initiateBehavior("flock_stream", 60, 100);
        runner.getGroup().broadcastSignal(initiator, "flock_stream", 16.0, 5, 0);

        runner.step(5);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ACTIVE, "All followers active");

        SimulatedEntity dyingBat = runner.getGroup().getFollowers().get(0);
        SimulatedEntity roostingBat = runner.getGroup().getFollowers().get(1);
        SimulatedEntity panickedBat = runner.getGroup().getFollowers().get(2);

        // Apply distinct interruptions mid-action
        dyingBat.setDead(true);
        roostingBat.triggerRoost(true);
        panickedBat.panic(0, 60, 0);

        // Advance 1 tick: dying and roosting bats instantly abort flight state to IDLE
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, dyingBat.getState(), "Dead bat aborted to IDLE");
        assertEquals(SimulatedEntity.State.IDLE, roostingBat.getState(), "Roosting bat aborted to IDLE");
        assertEquals(0, dyingBat.getStateTicks(), "Dead bat state ticks zeroed");
        assertEquals(0, roostingBat.getStateTicks(), "Roosting bat state ticks zeroed");
        assertNotEquals("flock_stream", panickedBat.getActiveSignal(), "Panicked bat replaced flock signal with panic");
    }

    @Test
    @DisplayName("Tier 3: Mid-Countdown Interruption Aborts ALERTED State Instantly")
    void testMidCountdownInterruptionAbortsAlertedStateInstantly() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(2, 5.0);
        SimulatedEntity initiator = runner.getGroup().getInitiator();
        initiator.initiateBehavior("flock_stream", 60, 100);

        runner.getGroup().broadcastSignal(initiator, "flock_stream", 16.0, 20, 0);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 20,
                "All followers must start ALERTED with 20 ticks delay");

        // Advance 10 ticks into countdown
        runner.step(10);
        runner.assertAllFollowers(e -> e.getState() == SimulatedEntity.State.ALERTED && e.getDelayTicks() == 10,
                "Followers counting down delay");

        // Interrupt follower 0 with predator threat
        SimulatedEntity f0 = runner.getGroup().getFollowers().get(0);
        f0.setPredatorNearby(true);

        // Next tick: follower 0 immediately aborts ALERTED state
        runner.step(1);
        assertEquals(SimulatedEntity.State.IDLE, f0.getState(), "Threatened bat aborted ALERTED state to IDLE");
        assertNull(f0.getActiveSignal(), "Signal cleared");
    }

    @Test
    @DisplayName("Tier 3: Roosting Requires Solid Ceiling (Rejects Air Ceilings)")
    void testRoostingRequiresSolidCeiling() {
        SimulatedEntity bat = new SimulatedEntity(java.util.UUID.randomUUID(), "test_bat", 0, 64, 0);

        // Attempting to roost with no solid ceiling fails
        boolean successNoCeiling = bat.triggerRoost(false);
        assertFalse(successNoCeiling, "Roosting must fail without solid ceiling");
        assertFalse(bat.isResting());

        // With solid ceiling succeeds
        boolean successCeiling = bat.triggerRoost(true);
        assertTrue(successCeiling, "Roosting must succeed with solid ceiling");
        assertTrue(bat.isResting());
    }

    @Test
    @DisplayName("Tier 3: Threat Proximity Wakes Resting Bats (Predators and Awake Players)")
    void testThreatProximityWakesRestingBats() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(2, 5.0);
        SimulatedEntity f0 = runner.getGroup().getFollowers().get(0);
        SimulatedEntity f1 = runner.getGroup().getFollowers().get(1);

        f0.triggerRoost(true);
        f1.triggerRoost(true);
        runner.assertRestingCount(2, "Both bats resting");

        // Predator approaches f0
        f0.setPredatorNearby(true);
        assertFalse(f0.isResting(), "Predator proximity instantly wakes f0");

        // Awake player approaches f1
        f1.setPlayerAwakeNearby(true);
        assertFalse(f1.isResting(), "Awake player proximity instantly wakes f1");
    }

    @Test
    @DisplayName("Tier 3: Distance Threshold Rejections (Out-of-Range Rejection)")
    void testDistanceThresholdRejections() {
        BehavioralStepRunner runner = SocialHandoffHarness.setupCluster(2, 10.0);
        SimulatedEntity leadBat = runner.getGroup().getInitiator();

        // Place follower 0 at 8m, follower 1 at 25m
        runner.getGroup().getFollowers().get(0).setPosition(8.0, 64.0, 0.0);
        runner.getGroup().getFollowers().get(1).setPosition(25.0, 64.0, 0.0);

        leadBat.initiateBehavior("flock_stream", 60, 100);

        // Broadcast with 16m radius
        int accepted = runner.getGroup().broadcastSignal(leadBat, "flock_stream", 16.0, 10, 0);
        assertEquals(1, accepted, "Only follower within 16m must accept signal");
        assertEquals(SimulatedEntity.State.ALERTED, runner.getGroup().getFollowers().get(0).getState());
        assertEquals(SimulatedEntity.State.IDLE, runner.getGroup().getFollowers().get(1).getState());
    }
}
