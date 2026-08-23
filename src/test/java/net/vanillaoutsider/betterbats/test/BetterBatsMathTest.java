// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BetterBatsMathTest {

    private static final double MIN_SPEED = 0.15;
    private static final double MAX_SPEED = 0.35;
    private static final int MAX_ALTITUDE_ABOVE_SURFACE = 30;

    @Test
    @DisplayName("Enforce Minimum Flight Speed Assertion")
    void testMinimumSpeedEnforcement() {
        double currentX = 0.02;
        double currentY = 0.01;
        double currentZ = 0.02;

        double speedSqr = currentX * currentX + currentY * currentY + currentZ * currentZ;
        assertTrue(speedSqr < MIN_SPEED * MIN_SPEED, "Speed squared should be below min threshold before enforcement");

        double invLength = 1.0 / Math.sqrt(speedSqr);
        double clampedX = currentX * invLength * MIN_SPEED;
        double clampedY = currentY * invLength * MIN_SPEED;
        double clampedZ = currentZ * invLength * MIN_SPEED;

        double finalSpeed = Math.sqrt(clampedX * clampedX + clampedY * clampedY + clampedZ * clampedZ);
        assertEquals(MIN_SPEED, finalSpeed, 0.0001, "Clamped speed should equal MIN_SPEED");
    }

    @Test
    @DisplayName("Enforce Maximum Flight Speed Clamping")
    void testMaximumSpeedClamping() {
        double fastX = 0.8;
        double fastY = 0.6;
        double fastZ = 0.0;

        double speed = Math.sqrt(fastX * fastX + fastY * fastY + fastZ * fastZ);
        assertTrue(speed > MAX_SPEED, "Speed should exceed MAX_SPEED before clamping");

        double scale = MAX_SPEED / speed;
        double clampedX = fastX * scale;
        double clampedY = fastY * scale;
        double clampedZ = fastZ * scale;

        double finalSpeed = Math.sqrt(clampedX * clampedX + clampedY * clampedY + clampedZ * clampedZ);
        assertEquals(MAX_SPEED, finalSpeed, 0.0001, "Clamped speed should equal MAX_SPEED");
    }

    @Test
    @DisplayName("Hard Altitude Cap Calculation")
    void testHardAltitudeCap() {
        int surfaceY = 64;
        int maxAltitude = surfaceY + MAX_ALTITUDE_ABOVE_SURFACE;
        assertEquals(94, maxAltitude, "Max altitude should be Y=94");

        double currentYAboveCap = 100.0;
        double excess = currentYAboveCap - maxAltitude;
        assertTrue(excess > 0, "Excess altitude should be positive when above cap");

        double capForce = -Math.min(0.25, excess * 0.05);
        assertTrue(capForce < 0, "Downward force must be negative");
        assertEquals(-0.25, capForce, 0.0001, "Cap force should cap at -0.25");
    }
}
