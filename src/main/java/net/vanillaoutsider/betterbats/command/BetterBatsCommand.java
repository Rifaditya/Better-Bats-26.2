// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRule;
import net.vanillaoutsider.betterbats.BetterBatsFabric;
import net.vanillaoutsider.betterbats.config.BetterBatsConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dedicated single-purpose Brigadier command tree builder and executor for /betterbats and /bb.
 */
public class BetterBatsCommand {

    private static final Map<String, GameRule<Boolean>> BOOL_MAP = new LinkedHashMap<>();
    private static final Map<String, GameRule<Integer>> INT_MAP = new LinkedHashMap<>();
    private static final Map<String, Boolean> BOOL_DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, Integer> INT_DEFAULTS = new LinkedHashMap<>();

    static {
        registerBool("bat_pest_control", BetterBatsFabric.BAT_PEST_CONTROL, true);
        registerBool("bat_drop_guano_item", BetterBatsFabric.BAT_DROP_GUANO_ITEM, false);

        registerInt("bat_swarm_size", BetterBatsFabric.BAT_SWARM_SIZE, 5);
        registerInt("bat_guano_threshold", BetterBatsFabric.BAT_GUANO_THRESHOLD, 12000);
        registerInt("bat_alignment", BetterBatsFabric.BAT_ALIGNMENT, 5);
        registerInt("bat_cohesion", BetterBatsFabric.BAT_COHESION, 5);
        registerInt("bat_separation", BetterBatsFabric.BAT_SEPARATION, 10);
        registerInt("bat_spawn_weight", BetterBatsFabric.BAT_SPAWN_WEIGHT, 30);
    }

    private static void registerBool(String name, GameRule<Boolean> rule, boolean def) {
        BOOL_MAP.put(name.toLowerCase(), rule);
        BOOL_DEFAULTS.put(name.toLowerCase(), def);
    }

    private static void registerInt(String name, GameRule<Integer> rule, int def) {
        INT_MAP.put(name.toLowerCase(), rule);
        INT_DEFAULTS.put(name.toLowerCase(), def);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> betterBats = buildCommandTree("betterbats");
        LiteralArgumentBuilder<CommandSourceStack> bb = buildCommandTree("bb");

        dispatcher.register(betterBats);
        dispatcher.register(bb);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCommandTree(String root) {
        return Commands.literal(root)
                .executes(context -> {
                    context.getSource().sendSuccess(
                            () -> Component.literal("§6[Better Bats]§r Use §a/" + root + " help§r for command guide or §a/" + root + " status§r for overview."),
                            false);
                    return 1;
                })
                .then(Commands.literal("help")
                        .executes(context -> executeHelp(context.getSource(), root)))
                .then(Commands.literal("status")
                        .executes(context -> executeStatus(context.getSource())))
                .then(Commands.literal("get")
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests(CommandSuggestionsHelper::suggestRules)
                                .executes(context -> executeGet(context.getSource(), StringArgumentType.getString(context, "rule")))))
                .then(Commands.literal("set")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .then(Commands.argument("rule", StringArgumentType.word())
                                .suggests(CommandSuggestionsHelper::suggestRules)
                                .then(Commands.argument("value", StringArgumentType.word())
                                        .suggests(CommandSuggestionsHelper::suggestValues)
                                        .executes(context -> executeSet(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "rule"),
                                                StringArgumentType.getString(context, "value")
                                        )))))
                .then(Commands.literal("reset")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .executes(context -> executeReset(context.getSource())))
                .then(Commands.literal("reload")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .executes(context -> executeReload(context.getSource())))
                .then(Commands.literal("debug")
                        .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.permissions()))
                        .then(Commands.literal("inspect")
                                .executes(context -> BatCommandHelper.inspectNearestBat(context.getSource())))
                        .then(Commands.literal("spawn_swarm")
                                .executes(context -> BatCommandHelper.spawnSwarm(context.getSource(), 5))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1, 30))
                                        .executes(context -> BatCommandHelper.spawnSwarm(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "count")
                                        )))));
    }

    private static int executeHelp(CommandSourceStack source, String literalName) {
        source.sendSuccess(() -> Component.literal(
                "§6--- Vanilla Outsider: Better Bats Commands ---§r\n" +
                "§a/" + literalName + " status§r - Display categorized summary of active GameRules\n" +
                "§a/" + literalName + " get <rule>§r - Query current value of a GameRule\n" +
                "§a/" + literalName + " set <rule> <val>§r - Modify a GameRule value & sync config (Gamemasters)\n" +
                "§a/" + literalName + " reset§r - Reset all GameRules to defaults & sync config (Gamemasters)\n" +
                "§a/" + literalName + " reload§r - Reload JSON config and sync active GameRules (Gamemasters)\n" +
                "§a/" + literalName + " debug inspect§r - Inspect nearest bat traits & physics (Gamemasters)\n" +
                "§a/" + literalName + " debug spawn_swarm [count]§r - Spawn test murmuration flock (Gamemasters)"
        ), false);
        return 1;
    }

    private static int executeStatus(CommandSourceStack source) {
        ServerLevel level = source.getLevel();

        StringBuilder sb = new StringBuilder();
        sb.append("§6=== Better Bats Status Overview ===§r\n");

        sb.append("§e[Swarm & Murmuration]§r\n");
        sb.append(" §7Swarm Size: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SWARM_SIZE)).append("§r")
                .append(" §7| Alignment: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_ALIGNMENT)).append("§r\n")
                .append(" §7Cohesion: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_COHESION)).append("§r")
                .append(" §7| Separation: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SEPARATION)).append("§r\n");

        sb.append("§e[Ecology & Fertilizer]§r\n");
        sb.append(" §7Guano Speed: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_GUANO_THRESHOLD)).append(" ticks§r\n")
                .append(" §7Drop Guano Items: ").append(formatBool(DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_DROP_GUANO_ITEM)))
                .append(" §7| Pest Control: ").append(formatBool(DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_PEST_CONTROL))).append("\n");

        sb.append("§e[World Spawning]§r\n");
        sb.append(" §7Bat Spawn Weight: §a").append(DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SPAWN_WEIGHT)).append("§r §7(Vanilla is 10)§r");

        source.sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static int executeGet(CommandSourceStack source, String ruleName) {
        ServerLevel level = source.getLevel();
        String key = CommandSuggestionsHelper.normalizeRuleName(ruleName);

        if (BOOL_MAP.containsKey(key)) {
            GameRule<Boolean> rule = BOOL_MAP.get(key);
            boolean val = DynamicGameRuleManager.getBoolean(level, rule);
            boolean def = BOOL_DEFAULTS.get(key);
            source.sendSuccess(() -> Component.literal(
                    "§6[Better Bats]§r §e" + key + "§r = " + formatBool(val) + " §7(Default: " + formatBool(def) + ")§r"
            ), false);
            return 1;
        }

        if (INT_MAP.containsKey(key)) {
            GameRule<Integer> rule = INT_MAP.get(key);
            int val = DynamicGameRuleManager.getInt(level, rule);
            int def = INT_DEFAULTS.get(key);
            source.sendSuccess(() -> Component.literal(
                    "§6[Better Bats]§r §e" + key + "§r = §a" + val + "§r §7(Default: " + def + ")§r"
            ), false);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Bats] Unknown GameRule: '" + ruleName + "'. Use tab completion for valid rules."));
        return 0;
    }

    private static int executeSet(CommandSourceStack source, String ruleName, String valueStr) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Bats] Server is unavailable."));
            return 0;
        }

        String key = CommandSuggestionsHelper.normalizeRuleName(ruleName);
        BetterBatsConfig config = BetterBatsConfig.get();

        if (BOOL_MAP.containsKey(key)) {
            if (!valueStr.equalsIgnoreCase("true") && !valueStr.equalsIgnoreCase("false")) {
                source.sendFailure(Component.literal("§c[Better Bats] Value for " + key + " must be true or false."));
                return 0;
            }
            boolean boolVal = Boolean.parseBoolean(valueStr);
            GameRule<Boolean> rule = BOOL_MAP.get(key);
            server.getGameRules().set(rule, boolVal, server);

            // Sync to config
            if (key.equals("bat_pest_control")) config.batPestControl = boolVal;
            if (key.equals("bat_drop_guano_item")) config.batDropGuanoItem = boolVal;
            BetterBatsConfig.save();

            source.sendSuccess(() -> Component.literal(
                    "§a[Better Bats] Successfully updated §e" + key + "§a to " + formatBool(boolVal) + "§a and saved config."
            ), true);
            return 1;
        }

        if (INT_MAP.containsKey(key)) {
            int intVal;
            try {
                intVal = Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("§c[Better Bats] Invalid integer value: '" + valueStr + "'"));
                return 0;
            }
            GameRule<Integer> rule = INT_MAP.get(key);
            server.getGameRules().set(rule, intVal, server);

            // Sync to config
            if (key.equals("bat_swarm_size")) config.batSwarmSize = intVal;
            if (key.equals("bat_guano_threshold")) config.batGuanoThreshold = intVal;
            if (key.equals("bat_alignment")) config.batAlignment = intVal;
            if (key.equals("bat_cohesion")) config.batCohesion = intVal;
            if (key.equals("bat_separation")) config.batSeparation = intVal;
            if (key.equals("bat_spawn_weight")) config.batSpawnWeight = intVal;
            BetterBatsConfig.save();

            source.sendSuccess(() -> Component.literal(
                    "§a[Better Bats] Successfully updated §e" + key + "§a to §f" + intVal + "§a and saved config."
            ), true);
            return 1;
        }

        source.sendFailure(Component.literal("§c[Better Bats] Unknown GameRule: '" + ruleName + "'"));
        return 0;
    }

    private static int executeReset(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Bats] Server is unavailable."));
            return 0;
        }

        BetterBatsConfig config = BetterBatsConfig.get();

        server.getGameRules().set(BetterBatsFabric.BAT_SWARM_SIZE, 5, server);
        server.getGameRules().set(BetterBatsFabric.BAT_GUANO_THRESHOLD, 12000, server);
        server.getGameRules().set(BetterBatsFabric.BAT_PEST_CONTROL, true, server);
        server.getGameRules().set(BetterBatsFabric.BAT_ALIGNMENT, 5, server);
        server.getGameRules().set(BetterBatsFabric.BAT_COHESION, 5, server);
        server.getGameRules().set(BetterBatsFabric.BAT_SEPARATION, 10, server);
        server.getGameRules().set(BetterBatsFabric.BAT_SPAWN_WEIGHT, 30, server);
        server.getGameRules().set(BetterBatsFabric.BAT_DROP_GUANO_ITEM, false, server);

        config.batSwarmSize = 5;
        config.batGuanoThreshold = 12000;
        config.batPestControl = true;
        config.batAlignment = 5;
        config.batCohesion = 5;
        config.batSeparation = 10;
        config.batSpawnWeight = 30;
        config.batDropGuanoItem = false;
        BetterBatsConfig.save();

        source.sendSuccess(() -> Component.literal("§a[Better Bats] Reset all GameRules to factory defaults and saved config."), true);
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("§c[Better Bats] Server is unavailable."));
            return 0;
        }

        BetterBatsConfig.load(FabricLoader.getInstance().getConfigDir());
        BetterBatsConfig config = BetterBatsConfig.get();

        server.getGameRules().set(BetterBatsFabric.BAT_SWARM_SIZE, config.batSwarmSize, server);
        server.getGameRules().set(BetterBatsFabric.BAT_GUANO_THRESHOLD, config.batGuanoThreshold, server);
        server.getGameRules().set(BetterBatsFabric.BAT_PEST_CONTROL, config.batPestControl, server);
        server.getGameRules().set(BetterBatsFabric.BAT_ALIGNMENT, config.batAlignment, server);
        server.getGameRules().set(BetterBatsFabric.BAT_COHESION, config.batCohesion, server);
        server.getGameRules().set(BetterBatsFabric.BAT_SEPARATION, config.batSeparation, server);
        server.getGameRules().set(BetterBatsFabric.BAT_SPAWN_WEIGHT, config.batSpawnWeight, server);
        server.getGameRules().set(BetterBatsFabric.BAT_DROP_GUANO_ITEM, config.batDropGuanoItem, server);

        source.sendSuccess(() -> Component.literal("§a[Better Bats] Reloaded configuration from disk and synchronized active GameRules."), true);
        return 1;
    }

    private static String formatBool(boolean val) {
        return val ? "§aEnabled§r" : "§cDisabled§r";
    }
}
