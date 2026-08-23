// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Dedicated suggestion and normalization provider helper for Better Bats Brigadier commands.
 */
public class CommandSuggestionsHelper {

    private static final List<String> RULE_NAMES = Arrays.asList(
            "bat_swarm_size", "swarm_size", "better-bats:bat_swarm_size",
            "bat_guano_threshold", "guano_threshold", "better-bats:bat_guano_threshold",
            "bat_pest_control", "pest_control", "better-bats:bat_pest_control",
            "bat_alignment", "alignment", "better-bats:bat_alignment",
            "bat_cohesion", "cohesion", "better-bats:bat_cohesion",
            "bat_separation", "separation", "better-bats:bat_separation",
            "bat_spawn_weight", "spawn_weight", "better-bats:bat_spawn_weight",
            "bat_drop_guano_item", "drop_guano_item", "better-bats:bat_drop_guano_item"
    );

    public static String normalizeRuleName(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if (s.startsWith("better-bats:")) s = s.substring("better-bats:".length());
        else if (s.startsWith("betterbats:")) s = s.substring("betterbats:".length());
        
        if (!s.startsWith("bat_")) {
            s = "bat_" + s;
        }
        return s;
    }

    public static CompletableFuture<Suggestions> suggestRules(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(RULE_NAMES, builder);
    }

    public static CompletableFuture<Suggestions> suggestValues(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String ruleName = normalizeRuleName(context.getArgument("rule", String.class));
        
        if (ruleName.equals("bat_pest_control") || ruleName.equals("bat_drop_guano_item")) {
            return SharedSuggestionProvider.suggest(Arrays.asList("true", "false"), builder);
        }
        
        if (ruleName.equals("bat_swarm_size")) {
            return SharedSuggestionProvider.suggest(Arrays.asList("0", "5", "10", "15", "20"), builder);
        }
        if (ruleName.equals("bat_guano_threshold")) {
            return SharedSuggestionProvider.suggest(Arrays.asList("6000", "12000", "24000"), builder);
        }
        if (ruleName.equals("bat_alignment") || ruleName.equals("bat_cohesion") || ruleName.equals("bat_separation")) {
            return SharedSuggestionProvider.suggest(Arrays.asList("0", "5", "10", "20", "50", "100"), builder);
        }
        if (ruleName.equals("bat_spawn_weight")) {
            return SharedSuggestionProvider.suggest(Arrays.asList("0", "10", "30", "50", "100"), builder);
        }

        return builder.buildFuture();
    }
}
