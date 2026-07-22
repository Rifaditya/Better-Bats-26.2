// Verified against: ClothConfigScreenHelper.java (26.1.2+)
package net.vanillaoutsider.betterbats.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreenHelper {
    public static ConfigScreenFactory<?> createFactory() {
        return ClothConfigScreenHelper::createScreen;
    }

    public static Screen createScreen(Screen parent) {
        BetterBatsConfig config = BetterBatsConfig.get();
        
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.better-bats.title"));

        builder.setSavingRunnable(BetterBatsConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.better-bats.category.general"));
        general.addEntry(entryBuilder.startTextDescription(Component.translatable("config.better-bats.warning")).build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batSwarmSize"), config.batSwarmSize)
                .setDefaultValue(5)
                .setSaveConsumer(val -> config.batSwarmSize = val)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batGuanoThreshold"), config.batGuanoThreshold)
                .setDefaultValue(12000)
                .setSaveConsumer(val -> config.batGuanoThreshold = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.better-bats.batPestControl"), config.batPestControl)
                .setDefaultValue(true)
                .setSaveConsumer(val -> config.batPestControl = val)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batAlignment"), config.batAlignment)
                .setDefaultValue(5)
                .setSaveConsumer(val -> config.batAlignment = val)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batCohesion"), config.batCohesion)
                .setDefaultValue(5)
                .setSaveConsumer(val -> config.batCohesion = val)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batSeparation"), config.batSeparation)
                .setDefaultValue(10)
                .setSaveConsumer(val -> config.batSeparation = val)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.better-bats.batSpawnWeight"), config.batSpawnWeight)
                .setDefaultValue(30)
                .setSaveConsumer(val -> config.batSpawnWeight = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.better-bats.batDropGuanoItem"), config.batDropGuanoItem)
                .setDefaultValue(false)
                .setSaveConsumer(val -> config.batDropGuanoItem = val)
                .build());

        return builder.build();
    }
}
