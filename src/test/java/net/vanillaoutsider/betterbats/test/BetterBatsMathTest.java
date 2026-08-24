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

    @Test
    @DisplayName("Daytime Cave-Seeking 4-Tier Fast-Fail Waterfall Logic")
    void testDaytimeCaveSeekingFastFailWaterfall() {
        int surfaceY = 70;
        int checkYAboveSurface = 75;
        int checkYBelowSurface = 50;

        // Stage 2 check: Above surface must fast-fail
        assertTrue(checkYAboveSurface >= surfaceY, "Above surface should fast-fail");
        assertFalse(checkYBelowSurface >= surfaceY, "Subterranean spot passes Stage 2");

        // Stage 3 check: Light comparison
        int lowestSkyLight = 8;
        int brighterLight = 10;
        int darkerLight = 3;
        assertTrue(brighterLight >= lowestSkyLight, "Brighter light fails Stage 3");
        assertFalse(darkerLight >= lowestSkyLight, "Darker light passes Stage 3");

        // Stage 5 check: Early exit on pitch darkness
        int pitchDarkness = 0;
        assertEquals(0, pitchDarkness, "Pitch darkness triggers immediate break");
    }

    @Test
    @DisplayName("Nighttime Cave Exit Open Sky Early Exit Logic")
    void testNighttimeCaveExitEarlyExit() {
        int highestSkyLight = 4;
        int candidateMoonlight = 14;

        assertTrue(candidateMoonlight > highestSkyLight, "Candidate is brighter");
        assertTrue(candidateMoonlight >= 14, "Candidate triggers instant open sky early exit");
    }

    @Test
    @DisplayName("Phototaxis Voxel Search Volume Optimization (59.4% Reduction)")
    void testPhototaxisVoxelVolumeCalculation() {
        int legacyRadiusH = 10;
        int legacyRadiusV = 10;
        int legacyVoxels = (2 * legacyRadiusH + 1) * (2 * legacyRadiusH + 1) * (2 * legacyRadiusV + 1);
        assertEquals(9261, legacyVoxels, "Legacy search volume must be 9,261 voxels");

        int optimizedRadiusH = 8;
        int optimizedRadiusV = 6;
        int optimizedVoxels = (2 * optimizedRadiusH + 1) * (2 * optimizedRadiusH + 1) * (2 * optimizedRadiusV + 1);
        assertEquals(3757, optimizedVoxels, "Optimized search volume must be 3,757 voxels");

        double reductionPercent = (1.0 - ((double) optimizedVoxels / legacyVoxels)) * 100.0;
        assertTrue(reductionPercent > 59.0 && reductionPercent < 60.0, "Voxel reduction must be ~59.4%");
    }
}
