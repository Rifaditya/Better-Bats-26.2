// Verified against: GameRules.java (26.1.2)
package net.vanillaoutsider.betterbats;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterbats.config.BetterBatsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterBatsFabric implements ModInitializer {
    public static final String MOD_ID = "better-bats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameRuleCategory BETTER_BATS = DynamicGameRuleManager.registerCategory(Identifier.fromNamespaceAndPath("better-bats", "better_bats"));

    public static GameRule<Integer> BAT_SWARM_SIZE;
    public static GameRule<Integer> BAT_GUANO_THRESHOLD;
    public static GameRule<Boolean> BAT_PEST_CONTROL;
    public static GameRule<Integer> BAT_ALIGNMENT;
    public static GameRule<Integer> BAT_COHESION;
    public static GameRule<Integer> BAT_SEPARATION;
    public static GameRule<Integer> BAT_SPAWN_WEIGHT;

    @Override
    public void onInitialize() {
        net.vanillaoutsider.betterbats.util.ModVersionGuard.checkClass("Better Bats", "net.minecraft.world.entity.EntityTypes");
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("dasik-library")) {
            throw new RuntimeException("Better Bats requires 'dasik-library' to be loaded!");
        }

        // Load configuration defaults
        BetterBatsConfig.load(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir());
        BetterBatsConfig config = BetterBatsConfig.get();

        // Initialize GameRules with configuration defaults
        BAT_SWARM_SIZE = 
                DynamicGameRuleManager.integerRule("better-bats:bat_swarm_size", BETTER_BATS, config.batSwarmSize)
                        .name("Bat Swarm Size")
                        .description("Controls the maximum size of bat flocks. When this threshold is met or exceeded, bats will no longer recruit new members into their murmuration. Set to 0 to disable flocking entirely. Default: 5.")
                        .register();
                        
        BAT_GUANO_THRESHOLD = 
                DynamicGameRuleManager.integerRule("better-bats:bat_guano_threshold", BETTER_BATS, config.batGuanoThreshold)
                        .name("Guano Production Speed")
                        .description("Ticks required for a resting bat to produce guano (Lower is faster).")
                        .register();
                        
        BAT_PEST_CONTROL = 
                DynamicGameRuleManager.booleanRule("better-bats:bat_pest_control", BETTER_BATS, config.batPestControl)
                        .name("Enable Pest Control")
                        .description("If true, bats will hunt silverfish and endermites.")
                        .register();

        BAT_ALIGNMENT = 
                DynamicGameRuleManager.integerRule("better-bats:bat_alignment", BETTER_BATS, config.batAlignment)
                        .name("Bat Alignment Weight")
                        .description("How strongly bats align their flight direction with the swarm (0-100). Default: 5")
                        .range(0, 100)
                        .register();

        BAT_COHESION = 
                DynamicGameRuleManager.integerRule("better-bats:bat_cohesion", BETTER_BATS, config.batCohesion)
                        .name("Bat Cohesion Weight")
                        .description("How strongly bats are pulled towards the center of the swarm (0-100). Default: 5")
                        .range(0, 100)
                        .register();

        BAT_SEPARATION = 
                DynamicGameRuleManager.integerRule("better-bats:bat_separation", BETTER_BATS, config.batSeparation)
                        .name("Bat Separation Weight")
                        .description("How strongly bats avoid colliding with each other (0-100). Default: 10")
                        .range(0, 100)
                        .register();

        BAT_SPAWN_WEIGHT = 
                DynamicGameRuleManager.integerRule("better-bats:bat_spawn_weight", BETTER_BATS, config.batSpawnWeight)
                        .name("Bat Spawn Weight")
                        .description("The spawn weight of bats (Vanilla is 10). Set to 0 to disable spawning. Range: 0 to 100.")
                        .range(0, 100)
                        .register();

        String version = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("better-bats")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("1.1.5-26.1.2");
        LOGGER.info("Better Bats: Chioptera Enhancements Initialized (v" + version + ")");
    }
}
