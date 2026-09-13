// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
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
    public static GameRule<Boolean> BAT_DROP_GUANO_ITEM;
    public static GameRule<Boolean> BAT_DEBUG_MODE;

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
                        .range(Integer.MIN_VALUE, Integer.MAX_VALUE)
                        .register();
                        
        BAT_GUANO_THRESHOLD = 
                DynamicGameRuleManager.integerRule("better-bats:bat_guano_threshold", BETTER_BATS, config.batGuanoThreshold)
                        .name("Guano Production Speed")
                        .description("Ticks required for a resting bat to produce guano (Lower is faster).")
                        .range(1, Integer.MAX_VALUE)
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

        BAT_DROP_GUANO_ITEM = 
                DynamicGameRuleManager.booleanRule("better-bats:bat_drop_guano_item", BETTER_BATS, config.batDropGuanoItem)
                        .name("Drop Guano Items")
                        .description("If true, roosting bats drop physical Bone Meal items over non-farmland blocks or when crops below are fully grown. Default: false.")
                        .register();

        BAT_DEBUG_MODE = 
                DynamicGameRuleManager.booleanRule("better-bats:debug_mode", BETTER_BATS, false)
                        .name("Debug Mode")
                        .description("Enable detailed diagnostic logging for Better Bats AI and behaviors. Resets to false on restart.")
                        .register();

        // Ensure Debug Mode resets to false on world/server startup (Default-OFF & Session-Transient Lifecycle Law)
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            server.getGameRules().set(BAT_DEBUG_MODE, false, server);
        });

        // Register Animal Genetics for Bats (Scale/Wingspan, Flight Speed, Pest Attack Damage)
        net.dasik.social.api.genetics.EntityGeneticsRegistry.register(
                net.minecraft.world.entity.EntityTypes.BAT,
                new net.dasik.social.api.genetics.GeneticsConfig(
                        java.util.Map.of(
                                "scale", new net.dasik.social.api.genetics.TraitConfig(
                                        "scale", "minecraft:generic.scale", "ADD_VALUE", 0.0f, 1.0f, 0.75f, 1.30f
                                ),
                                "movement_speed", new net.dasik.social.api.genetics.TraitConfig(
                                        "movement_speed", "minecraft:generic.movement_speed", "ADD_MULTIPLIED_BASE", 0.0f, 1.0f, -0.04f, 0.08f
                                ),
                                "attack_damage", new net.dasik.social.api.genetics.TraitConfig(
                                        "attack_damage", "minecraft:generic.attack_damage", "ADD_VALUE", 0.0f, 1.0f, 1.0f, 4.0f
                                )
                        ),
                        java.util.Map.of(
                                "default", java.util.Map.of(
                                        "scale", new net.dasik.social.api.genetics.MutationRule("uniform", 0.75f, 1.30f),
                                        "movement_speed", new net.dasik.social.api.genetics.MutationRule("uniform", -0.04f, 0.08f),
                                        "attack_damage", new net.dasik.social.api.genetics.MutationRule("uniform", 1.0f, 4.0f)
                                )
                        )
                )
        );

        // Register in-game Brigadier Command Suite (/betterbats and /bb)
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            net.vanillaoutsider.betterbats.command.BetterBatsCommand.register(dispatcher);
        });

        String version = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("better-bats")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("1.1.18+26.2");
        LOGGER.info("Better Bats: Chioptera Enhancements Initialized (v" + version + ")");
    }
}
