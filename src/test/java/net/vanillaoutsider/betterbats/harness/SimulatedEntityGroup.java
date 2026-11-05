// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.harness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * SimulatedEntityGroup: Helper to spawn and manage in-memory clusters of bats
 * (1 initiator + N followers) for headless multi-entity social handoff, roosting, and panic verification.
 */
public class SimulatedEntityGroup {

    private final SimulatedEntity initiator;
    private final List<SimulatedEntity> followers;
    private final List<SimulatedEntity> allEntities;

    public SimulatedEntityGroup(SimulatedEntity initiator, List<SimulatedEntity> followers) {
        this.initiator = Objects.requireNonNull(initiator, "initiator cannot be null");
        this.followers = Collections.unmodifiableList(new ArrayList<>(followers));
        List<SimulatedEntity> all = new ArrayList<>();
        all.add(initiator);
        all.addAll(followers);
        this.allEntities = Collections.unmodifiableList(all);
    }

    /**
     * Factory method creating a flying bat cluster with 1 initiator placed at origin (0, 64, 0)
     * and N followers positioned radially at the specified spacing radius.
     */
    public static SimulatedEntityGroup create(int followerCount, double followerSpacing) {
        SimulatedEntity init = new SimulatedEntity(UUID.randomUUID(), "initiator_bat", 0.0, 64.0, 0.0);
        List<SimulatedEntity> followers = new ArrayList<>();
        for (int i = 0; i < followerCount; i++) {
            double angle = (2.0 * Math.PI / Math.max(1, followerCount)) * i;
            double fx = Math.cos(angle) * followerSpacing;
            double fz = Math.sin(angle) * followerSpacing;
            followers.add(new SimulatedEntity(UUID.randomUUID(), "follower_bat_" + i, fx, 64.0, fz));
        }
        return new SimulatedEntityGroup(init, followers);
    }

    /**
     * Factory method creating a cave roosting cluster where all bats start resting on ceiling.
     */
    public static SimulatedEntityGroup createRoostCluster(int followerCount, double followerSpacing) {
        SimulatedEntityGroup group = create(followerCount, followerSpacing);
        for (SimulatedEntity bat : group.getAllEntities()) {
            bat.setHasSolidCeilingAbove(true);
            bat.triggerRoost(true);
        }
        return group;
    }

    public SimulatedEntity getInitiator() {
        return initiator;
    }

    public List<SimulatedEntity> getFollowers() {
        return followers;
    }

    public List<SimulatedEntity> getAllEntities() {
        return allEntities;
    }

    /**
     * Broadcasts a flight social signal (e.g. "flock_stream") with linear delay stagger.
     */
    public int broadcastSignal(SimulatedEntity source, String signal, double radius, int baseDelay, int perEntityStagger) {
        return broadcastSignal(source, signal, radius, idx -> baseDelay + (idx * perEntityStagger), idx -> 1.0f, 60, 100);
    }

    /**
     * Broadcasts a flight social signal with dynamic delay calculator.
     */
    public int broadcastSignal(SimulatedEntity source, String signal, double radius, ToIntFunction<Integer> delayCalculator, int activeDuration, int cooldownAfter) {
        return broadcastSignal(source, signal, radius, delayCalculator, idx -> 1.0f, activeDuration, cooldownAfter);
    }

    /**
     * Broadcasts a social signal with dynamic delay and pitch calculators.
     */
    public int broadcastSignal(
            SimulatedEntity source,
            String signal,
            double radius,
            ToIntFunction<Integer> delayCalculator,
            java.util.function.IntFunction<Float> pitchCalculator,
            int activeDuration,
            int cooldownAfter
    ) {
        int acceptedCount = 0;
        int followerIdx = 0;
        for (SimulatedEntity entity : allEntities) {
            if (entity.equals(source)) {
                continue;
            }
            if (source.distanceTo(entity) <= radius) {
                int staggeredDelay = delayCalculator.applyAsInt(followerIdx);
                float pitch = pitchCalculator != null ? pitchCalculator.apply(followerIdx) : 1.0f;
                if (entity.receiveSignal(signal, staggeredDelay, activeDuration, cooldownAfter, pitch)) {
                    acceptedCount++;
                    followerIdx++;
                }
            }
        }
        return acceptedCount;
    }

    /**
     * Broadcasts a panic disturbance from the specified world location.
     * All entities within radius receive panic, wake from roost if resting, and scatter.
     */
    public int broadcastPanic(double sourceX, double sourceY, double sourceZ, double radius) {
        int count = 0;
        for (SimulatedEntity entity : allEntities) {
            double dx = entity.getX() - sourceX;
            double dy = entity.getY() - sourceY;
            double dz = entity.getZ() - sourceZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist <= radius && !entity.isDead()) {
                entity.panic(sourceX, sourceY, sourceZ);
                count++;
            }
        }
        return count;
    }

    /**
     * Ticks every entity in the group by 1 game tick.
     */
    public void tickAll() {
        for (SimulatedEntity entity : allEntities) {
            entity.tick();
        }
    }

    public void forEach(Consumer<SimulatedEntity> action) {
        allEntities.forEach(action);
    }

    public List<SimulatedEntity> findByState(SimulatedEntity.State state) {
        return allEntities.stream()
                .filter(e -> e.getState() == state)
                .collect(Collectors.toList());
    }

    public long countInState(SimulatedEntity.State state) {
        return allEntities.stream()
                .filter(e -> e.getState() == state)
                .count();
    }

    public long countResting() {
        return allEntities.stream()
                .filter(SimulatedEntity::isResting)
                .count();
    }

    public long countPanicked() {
        return allEntities.stream()
                .filter(SimulatedEntity::isPanicked)
                .count();
    }

    public long countDead() {
        return allEntities.stream()
                .filter(SimulatedEntity::isDead)
                .count();
    }
}
