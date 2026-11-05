// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.harness;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * SimulatedEntity: Lightweight in-memory simulated entity for headless behavioral state testing in Better Bats.
 * Operates with 0ms boot overhead and zero OpenGL/chunk dependencies.
 *
 * Simulates:
 * - 4-state behavioral machine: IDLE, ALERTED, ACTIVE, COOLDOWN
 * - Cave roosting & clustering choreography (hanging upside down, solid ceiling requirement)
 * - Guano accumulation cycle while resting (ticks up, fertilizes, resets)
 * - Disturbance & herd panic mechanics (explosion, loud sprinting, predator threat)
 * - Twilight funneling & BOIDs flocking coordination
 * - Negative parity & anti-goal invariants
 */
public class SimulatedEntity {

    public enum State {
        IDLE,
        ALERTED,
        ACTIVE,
        COOLDOWN
    }

    private final UUID uuid;
    private final String role;
    private double x;
    private double y;
    private double z;
    private double vx = 0.0;
    private double vy = 0.0;
    private double vz = 0.0;

    // Bat Behavioral & Environmental State Flags
    private boolean resting = false;
    private boolean panicked = false;
    private int panicTicks = 0;
    private double panicSourceX = 0.0;
    private double panicSourceY = 0.0;
    private double panicSourceZ = 0.0;
    private int guanoTicks = 0;
    private boolean dead = false;
    private boolean inCombat = false;
    private boolean predatorNearby = false;
    private boolean playerAwakeNearby = false;
    private boolean hasSolidCeilingAbove = true;
    private double altitudeAboveSurface = 10.0;
    private boolean canSeeSky = false;
    private boolean twilight = false;

    // Behavioral State Machine
    private State state = State.IDLE;
    private int stateTicks = 0;
    private int delayTicks = 0;
    private int cooldownTicks = 0;
    private String activeSignal = null;
    private float pitch = 1.0f;
    private Consumer<SimulatedEntity> customTickHook = null;

    public SimulatedEntity(UUID uuid, String role, double x, double y, double z) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.role = Objects.requireNonNull(role, "role cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getRole() {
        return role;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getVz() {
        return vz;
    }

    public void setVelocity(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    public boolean isResting() {
        return resting;
    }

    public void setResting(boolean resting) {
        this.resting = resting;
        if (resting) {
            this.vx = 0.0;
            this.vy = 0.0;
            this.vz = 0.0;
        }
    }

    public boolean isPanicked() {
        return panicked;
    }

    public void setPanicked(boolean panicked) {
        this.panicked = panicked;
    }

    public int getPanicTicks() {
        return panicTicks;
    }

    public void setPanicTicks(int panicTicks) {
        this.panicTicks = panicTicks;
        this.panicked = panicTicks > 0;
    }

    public double getPanicSourceX() {
        return panicSourceX;
    }

    public double getPanicSourceY() {
        return panicSourceY;
    }

    public double getPanicSourceZ() {
        return panicSourceZ;
    }

    public int getGuanoTicks() {
        return guanoTicks;
    }

    public void setGuanoTicks(int guanoTicks) {
        this.guanoTicks = guanoTicks;
    }

    public void resetGuanoTicks() {
        this.guanoTicks = 0;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        if (dead) {
            abortBehavior();
            this.resting = false;
            this.panicked = false;
            this.panicTicks = 0;
            this.guanoTicks = 0;
        }
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public boolean isPredatorNearby() {
        return predatorNearby;
    }

    public void setPredatorNearby(boolean predatorNearby) {
        this.predatorNearby = predatorNearby;
        if (predatorNearby && this.resting) {
            wakeUp();
        }
    }

    public boolean isPlayerAwakeNearby() {
        return playerAwakeNearby;
    }

    public void setPlayerAwakeNearby(boolean playerAwakeNearby) {
        this.playerAwakeNearby = playerAwakeNearby;
        if (playerAwakeNearby && this.resting) {
            wakeUp();
        }
    }

    public boolean hasSolidCeilingAbove() {
        return hasSolidCeilingAbove;
    }

    public void setHasSolidCeilingAbove(boolean hasSolidCeilingAbove) {
        this.hasSolidCeilingAbove = hasSolidCeilingAbove;
        if (!hasSolidCeilingAbove && this.resting) {
            wakeUp();
        }
    }

    public double getAltitudeAboveSurface() {
        return altitudeAboveSurface;
    }

    public void setAltitudeAboveSurface(double altitudeAboveSurface) {
        this.altitudeAboveSurface = altitudeAboveSurface;
    }

    public boolean canSeeSky() {
        return canSeeSky;
    }

    public void setCanSeeSky(boolean canSeeSky) {
        this.canSeeSky = canSeeSky;
    }

    public boolean isTwilight() {
        return twilight;
    }

    public void setTwilight(boolean twilight) {
        this.twilight = twilight;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getStateTicks() {
        return stateTicks;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public int getCooldownTicks() {
        return cooldownTicks;
    }

    public String getActiveSignal() {
        return activeSignal;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Consumer<SimulatedEntity> getCustomTickHook() {
        return customTickHook;
    }

    public void setCustomTickHook(Consumer<SimulatedEntity> customTickHook) {
        this.customTickHook = customTickHook;
    }

    /**
     * Checks if entity is eligible to participate in active flight social behaviors (flocking, choruses).
     * Disqualified if dead, resting (asleep on ceiling), panicked, in combat, or threatened by predator.
     */
    public boolean isAvailableForSocial() {
        return !dead && !resting && !panicked && !inCombat && !predatorNearby;
    }

    /**
     * Checks if entity is eligible to roost on a cave ceiling.
     */
    public boolean isAvailableForRoosting() {
        return !dead && !panicked && !resting && hasSolidCeilingAbove;
    }

    /**
     * Receives social signal with default duration (60 ticks) and cooldown (100 ticks).
     */
    public boolean receiveSignal(String signal, int responseDelay) {
        return receiveSignal(signal, responseDelay, 60, 100, 1.0f);
    }

    /**
     * Receives social signal with configurable duration and cooldown.
     */
    public boolean receiveSignal(String signal, int responseDelay, int activeDuration, int cooldownAfter) {
        return receiveSignal(signal, responseDelay, activeDuration, cooldownAfter, 1.0f);
    }

    /**
     * Receives social signal with configurable duration, cooldown, and pitch.
     * Rejects if entity is not available for social flight or not idle.
     */
    public boolean receiveSignal(String signal, int responseDelay, int activeDuration, int cooldownAfter, float pitch) {
        if (!isAvailableForSocial() || state != State.IDLE) {
            return false;
        }
        this.state = State.ALERTED;
        this.delayTicks = Math.max(0, responseDelay);
        this.stateTicks = activeDuration;
        this.cooldownTicks = cooldownAfter;
        this.activeSignal = signal;
        this.pitch = pitch;
        return true;
    }

    /**
     * Initiates active behavioral action (e.g. lead flight stream).
     */
    public boolean initiateBehavior(String signal, int durationTicks, int cooldownAfter) {
        return initiateBehavior(signal, durationTicks, cooldownAfter, 1.0f);
    }

    /**
     * Initiates active behavioral action with custom pitch.
     */
    public boolean initiateBehavior(String signal, int durationTicks, int cooldownAfter, float pitch) {
        if (!isAvailableForSocial() || state != State.IDLE) {
            return false;
        }
        this.state = State.ACTIVE;
        this.stateTicks = durationTicks;
        this.cooldownTicks = cooldownAfter;
        this.activeSignal = signal;
        this.pitch = pitch;
        return true;
    }

    /**
     * Triggers panic on this entity caused by a disturbance event at the specified source coordinate.
     * Enforces immediate waking if resting, wipes guano ticks, sets panic state,
     * and calculates directional flight velocity away from disturbance.
     */
    public void panic(double sourceX, double sourceY, double sourceZ) {
        if (this.dead) {
            return;
        }
        if (this.resting) {
            this.resting = false;
        }
        this.guanoTicks = 0;
        this.panicked = true;
        this.panicTicks = 100;
        this.panicSourceX = sourceX;
        this.panicSourceY = sourceY;
        this.panicSourceZ = sourceZ;
        this.state = State.ACTIVE;
        this.stateTicks = 0;
        this.delayTicks = 0;
        this.cooldownTicks = 0;
        this.activeSignal = "panic";

        // Calculate directional flight vector away from panic source
        double dx = this.x - sourceX;
        double dy = this.y - sourceY;
        double dz = this.z - sourceZ;
        double lenSqr = dx * dx + dy * dy + dz * dz;
        if (lenSqr < 0.01) {
            dy = 0.5;
            lenSqr = 0.25;
        }
        double invLen = 1.0 / Math.sqrt(lenSqr);
        this.vx = dx * invLen * 0.35;
        this.vy = dy * invLen * 0.35;
        this.vz = dz * invLen * 0.35;
    }

    /**
     * Attempts to transition into resting state on cave ceiling.
     * Requires sturdy ceiling above; rejected if dead or panicked.
     */
    public boolean triggerRoost(boolean hasCeiling) {
        if (this.dead || this.panicked || !hasCeiling) {
            return false;
        }
        abortBehavior();
        this.resting = true;
        this.hasSolidCeilingAbove = true;
        this.activeSignal = "roosting";
        this.vx = 0.0;
        this.vy = 0.0;
        this.vz = 0.0;
        return true;
    }

    /**
     * Wakes the resting bat into flying state.
     */
    public void wakeUp() {
        this.resting = false;
        if ("roosting".equals(this.activeSignal)) {
            this.activeSignal = null;
        }
    }

    /**
     * Simulates 1 game tick of entity behavior.
     * Enforces continuous negative parity watchdogs and state machine transitions.
     */
    public void tick() {
        if (customTickHook != null) {
            customTickHook.accept(this);
            return;
        }

        // Negative Invariant: Dead entities immediately abort all actions
        if (dead) {
            if (state != State.IDLE || resting || panicked) {
                abortBehavior();
                resting = false;
                panicked = false;
                panicTicks = 0;
                guanoTicks = 0;
            }
            return;
        }

        // Threat Check: Nearby predator or awake player wakes resting bats
        if (resting && (predatorNearby || playerAwakeNearby)) {
            wakeUp();
        }

        // Negative Invariant: Panicked bats can never rest and cannot accumulate guano
        if (panicked) {
            if (resting) {
                resting = false;
            }
            guanoTicks = 0;
            if (panicTicks > 0) {
                panicTicks--;
                x += vx;
                y += vy;
                z += vz;
            }
            if (panicTicks <= 0) {
                panicked = false;
                activeSignal = null;
                state = State.IDLE;
                vx = 0.0;
                vy = 0.0;
                vz = 0.0;
            }
            return;
        }

        // Resting State: Accumulate guano, zero motion, abort active flight goals
        if (resting) {
            vx = 0.0;
            vy = 0.0;
            vz = 0.0;
            if (state == State.ALERTED || state == State.ACTIVE) {
                abortBehavior();
            }
            guanoTicks++;
            return;
        }

        // Flight State Watchdog: If bat became ineligible mid-flight, abort immediately
        if (!isAvailableForSocial()) {
            if (state != State.IDLE) {
                abortBehavior();
            }
            return;
        }

        // Standard Behavioral State Machine
        switch (state) {
            case ALERTED -> {
                if (delayTicks > 0) {
                    delayTicks--;
                }
                if (delayTicks <= 0) {
                    state = State.ACTIVE;
                    if (stateTicks <= 0) {
                        stateTicks = 60;
                    }
                }
            }
            case ACTIVE -> {
                if (stateTicks > 0) {
                    stateTicks--;
                    x += vx;
                    y += vy;
                    z += vz;
                }
                if (stateTicks <= 0) {
                    if (cooldownTicks > 0) {
                        state = State.COOLDOWN;
                    } else {
                        state = State.IDLE;
                        activeSignal = null;
                    }
                }
            }
            case COOLDOWN -> {
                if (cooldownTicks > 0) {
                    cooldownTicks--;
                }
                if (cooldownTicks <= 0) {
                    state = State.IDLE;
                    activeSignal = null;
                }
            }
            case IDLE -> {
            }
        }
    }

    /**
     * Immediately aborts and resets active behavioral state.
     */
    public void abortBehavior() {
        this.state = State.IDLE;
        this.stateTicks = 0;
        this.delayTicks = 0;
        this.cooldownTicks = 0;
        this.activeSignal = null;
        this.pitch = 1.0f;
    }

    /**
     * Calculates 3D Euclidean distance to another entity.
     */
    public double distanceTo(SimulatedEntity other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
