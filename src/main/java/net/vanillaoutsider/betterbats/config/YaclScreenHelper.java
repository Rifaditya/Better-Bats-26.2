// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import net.dasik.social.api.config.DasikSupportHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YaclScreenHelper {
    public static ConfigScreenFactory<?> createScreen() {
        return YaclScreenHelper::buildScreen;
    }

    private static Screen buildScreen(Screen parent) {
        BetterBatsConfig config = BetterBatsConfig.get();

        var generalGroup = OptionGroup.createBuilder()
            .name(Component.translatable("config.better-bats.category.general"));

        Option<?> supportButton = (Option<?>) DasikSupportHelper.createYaclButton();
        if (supportButton != null) {
            generalGroup.option(supportButton);
        }

        generalGroup
            // Bat Swarm Size
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batSwarmSize"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_swarm_size.description")))
                .binding(
                    5,
                    () -> config.batSwarmSize,
                    val -> config.batSwarmSize = val
                )
                .customController(opt -> new IntegerSliderController(opt, 0, 50, 1))
                .build())

            // Guano Production Speed (Threshold)
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batGuanoThreshold"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_guano_threshold.description")))
                .binding(
                    12000,
                    () -> config.batGuanoThreshold,
                    val -> config.batGuanoThreshold = val
                )
                .customController(opt -> new IntegerSliderController(opt, 100, 72000, 500))
                .build())

            // Pest Control
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("config.better-bats.batPestControl"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_pest_control.description")))
                .binding(
                    true,
                    () -> config.batPestControl,
                    val -> config.batPestControl = val
                )
                .controller(BooleanControllerBuilder::create)
                .build())

            // Bat Alignment Weight
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batAlignment"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_alignment.description")))
                .binding(
                    5,
                    () -> config.batAlignment,
                    val -> config.batAlignment = val
                )
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build())

            // Bat Cohesion Weight
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batCohesion"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_cohesion.description")))
                .binding(
                    5,
                    () -> config.batCohesion,
                    val -> config.batCohesion = val
                )
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build())

            // Bat Separation Weight
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batSeparation"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_separation.description")))
                .binding(
                    10,
                    () -> config.batSeparation,
                    val -> config.batSeparation = val
                )
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build())

            // Bat Spawn Weight
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("config.better-bats.batSpawnWeight"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_spawn_weight.description")))
                .binding(
                    30,
                    () -> config.batSpawnWeight,
                    val -> config.batSpawnWeight = val
                )
                .customController(opt -> new IntegerSliderController(opt, 0, 100, 1))
                .build())

            // Drop Guano Items
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("config.better-bats.batDropGuanoItem"))
                .description(OptionDescription.of(Component.translatable("gamerule.better-bats.bat_drop_guano_item.description")))
                .binding(
                    false,
                    () -> config.batDropGuanoItem,
                    val -> config.batDropGuanoItem = val
                )
                .controller(BooleanControllerBuilder::create)
                .build());

        return YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("config.better-bats.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("config.better-bats.category.general"))
                .group(generalGroup.build())
                .build())
            .save(BetterBatsConfig::save)
            .build()
            .generateScreen(parent);
    }
}
