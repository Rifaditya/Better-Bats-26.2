// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

/**
 * Single-purpose zero-allocation debug mode gating helper.
 */
public class BatDebugHelper {

    public static boolean isDebug(Level level) {
        if (level == null) return false;
        return DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_DEBUG_MODE);
    }
}
