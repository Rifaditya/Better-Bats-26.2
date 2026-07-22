// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.util;

public final class ModVersionGuard {
    private ModVersionGuard() {}

    public static void checkClass(String modName, String requiredClassName) {
        try {
            Class.forName(requiredClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("\n" +
                "=====================================================================\n" +
                " [" + modName + "] Minecraft API Mismatch!\n" +
                " A required Minecraft class or API was not found in your game version.\n" +
                " Try updating your Minecraft version one drop at a time until it works, or\n" +
                " download a matching JAR for your Minecraft release from Modrinth or CurseForge.\n" +
                "=====================================================================");
        }
    }
}
