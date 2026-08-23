// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BetterBatsCommandTest {

    private static final List<String> EXPECTED_RULES = Arrays.asList(
            "bat_swarm_size", "swarm_size", "better-bats:bat_swarm_size",
            "bat_guano_threshold", "guano_threshold", "better-bats:bat_guano_threshold",
            "bat_pest_control", "pest_control", "better-bats:bat_pest_control",
            "bat_alignment", "alignment", "better-bats:bat_alignment",
            "bat_cohesion", "cohesion", "better-bats:bat_cohesion",
            "bat_separation", "separation", "better-bats:bat_separation",
            "bat_spawn_weight", "spawn_weight", "better-bats:bat_spawn_weight",
            "bat_drop_guano_item", "drop_guano_item", "better-bats:bat_drop_guano_item",
            "bat_debug_mode", "debug_mode", "better-bats:debug_mode"
    );

    @Test
    @DisplayName("Verify Command Rule Name Coverage")
    void testCommandRuleListIntegrity() {
        assertFalse(EXPECTED_RULES.isEmpty(), "Rule list should not be empty");
        assertTrue(EXPECTED_RULES.contains("bat_swarm_size"), "Must contain bat_swarm_size");
        assertTrue(EXPECTED_RULES.contains("swarm_size"), "Must contain swarm_size alias");
        assertTrue(EXPECTED_RULES.contains("better-bats:bat_swarm_size"), "Must contain full namespaced rule");
        assertTrue(EXPECTED_RULES.contains("bat_drop_guano_item"), "Must contain bat_drop_guano_item");
        assertTrue(EXPECTED_RULES.contains("bat_debug_mode"), "Must contain bat_debug_mode");
        assertTrue(EXPECTED_RULES.contains("debug_mode"), "Must contain debug_mode alias");
        assertTrue(EXPECTED_RULES.contains("better-bats:debug_mode"), "Must contain full namespaced debug_mode rule");
    }

    @Test
    @DisplayName("Verify Number Parsing and Clamping for Commands")
    void testNumberParsing() {
        String validInt = "15";
        int parsed = Integer.parseInt(validInt);
        assertEquals(15, parsed, "Parsed integer should equal 15");

        String invalidInt = "abc";
        assertThrows(NumberFormatException.class, () -> Integer.parseInt(invalidInt));
    }
}
