// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.test;

import net.vanillaoutsider.betterbats.config.BetterBatsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BetterBatsConfigTest {

    @Test
    @DisplayName("Verify BetterBatsConfig Default Values")
    void testConfigDefaults() {
        BetterBatsConfig config = new BetterBatsConfig();

        assertEquals(5, config.batSwarmSize, "Default bat swarm size should be 5");
        assertEquals(12000, config.batGuanoThreshold, "Default guano threshold should be 12000 ticks");
        assertTrue(config.batPestControl, "Default pest control should be true");
        assertEquals(5, config.batAlignment, "Default alignment should be 5");
        assertEquals(5, config.batCohesion, "Default cohesion should be 5");
        assertEquals(10, config.batSeparation, "Default separation should be 10");
        assertEquals(30, config.batSpawnWeight, "Default spawn weight should be 30");
        assertFalse(config.batDropGuanoItem, "Default drop guano item should be false");
    }

    @Test
    @DisplayName("Verify Bat Genetics Trait Boundaries")
    void testGeneticsTraitBoundaries() {
        float minScale = 0.75f;
        float maxScale = 1.30f;
        assertTrue(minScale < maxScale, "Min scale should be less than max scale");

        float minSpeed = -0.04f;
        float maxSpeed = 0.08f;
        assertTrue(minSpeed < maxSpeed, "Min speed modifier should be less than max speed modifier");

        float minDamage = 1.0f;
        float maxDamage = 4.0f;
        assertTrue(minDamage < maxDamage, "Min damage trait should be less than max damage trait");
    }
}
