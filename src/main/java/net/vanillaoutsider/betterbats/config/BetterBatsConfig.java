// Verified against: BetterDogsConfig.java (26.1.2+)
package net.vanillaoutsider.betterbats.config;

import java.nio.file.Path;

public class BetterBatsConfig {
    private static BetterBatsConfig INSTANCE = new BetterBatsConfig();
    private static Path CONFIG_PATH;

    public static final int VERSION = 1;
    public int configVersion = VERSION;

    // Default template values for GameRules
    public int batSwarmSize = 5;
    public int batGuanoThreshold = 12000;
    public boolean batPestControl = true;
    public int batAlignment = 5;
    public int batCohesion = 5;
    public int batSeparation = 10;
    public int batSpawnWeight = 30;
    public boolean batDropGuanoItem = false;

    public static synchronized void load(Path configDir) {
        CONFIG_PATH = configDir.resolve("better-bats.json");
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("Better Bats");
        INSTANCE = net.dasik.social.api.config.ConfigHelper.load(
                CONFIG_PATH,
                INSTANCE,
                BetterBatsConfig.class,
                VERSION,
                config -> config.configVersion,
                (config, ver) -> config.configVersion = ver,
                "/better-bats.json",
                logger
        );
    }

    public static synchronized void save() {
        if (CONFIG_PATH == null) return;
        net.dasik.social.api.config.ConfigHelper.save(
                CONFIG_PATH,
                INSTANCE,
                org.slf4j.LoggerFactory.getLogger("Better Bats")
        );
    }

    public static BetterBatsConfig get() {
        return INSTANCE;
    }
}
