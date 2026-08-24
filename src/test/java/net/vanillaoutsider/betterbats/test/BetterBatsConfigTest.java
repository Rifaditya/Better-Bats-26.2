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

    @Test
    @DisplayName("Verify fabric.mod.json Manifest Completeness & Metadata")
    void testFabricModJsonManifestIntegrity() throws Exception {
        java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream("fabric.mod.json");
        assertNotNull(stream, "fabric.mod.json must exist in classpath");

        String content = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(content.contains("\"id\": \"better-bats\""), "Must declare correct mod id");
        assertTrue(content.contains("\"modrinth\""), "Must declare modrinth custom block");
        assertTrue(content.contains("\"projectId\": \"better-bats\""), "Must declare modrinth projectId");
        assertTrue(content.contains("\"GPL-3.0-or-later\""), "Must declare GPL-3.0-or-later license");
        assertTrue(content.contains("\"yet-another-config-lib\""), "Must suggest YACL");
        assertTrue(content.contains("\"yet_another_config_lib_v3\""), "Must suggest YACL v3");
        assertTrue(content.contains("https://github.com/Rifaditya/"), "Must declare official GitHub repository URLs");
    }

    @Test
    @DisplayName("Verify en_us.json Localization Keys Presence")
    void testLocalizationKeysCompleteness() throws Exception {
        java.io.InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/better-bats/lang/en_us.json");
        assertNotNull(stream, "en_us.json must exist in classpath");

        String content = new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(content.contains("gamerule.better-bats.debug_mode"), "Must contain debug_mode title");
        assertTrue(content.contains("gamerule.better-bats.debug_mode.description"), "Must contain debug_mode description");
        assertTrue(content.contains("gamerule.better-bats.bat_swarm_size"), "Must contain swarm size title");
        assertTrue(content.contains("gamerule.better-bats.bat_pest_control"), "Must contain pest control title");
    }
}
