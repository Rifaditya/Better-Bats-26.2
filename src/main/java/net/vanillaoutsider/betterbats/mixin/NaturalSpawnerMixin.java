// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterbats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterbats.BetterBatsFabric;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @Inject(method = "mobsAt", at = @At("RETURN"), cancellable = true)
    private static void betterbats$onMobsAt(
            ServerLevel level, StructureManager structureManager, ChunkGenerator generator, 
            MobCategory mobCategory, BlockPos pos, @Nullable Holder<Biome> biome, 
            CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> cir) {
        
        if (mobCategory == MobCategory.AMBIENT) {
            WeightedList<MobSpawnSettings.SpawnerData> original = cir.getReturnValue();
            if (original != null && !original.isEmpty()) {
                boolean hasBat = false;
                for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
                    if (item.value().type() == EntityTypes.BAT) {
                        hasBat = true;
                        break;
                    }
                }
                if (hasBat) {
                    int customWeight = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SPAWN_WEIGHT);
                    List<Weighted<MobSpawnSettings.SpawnerData>> newList = new ArrayList<>();
                    for (Weighted<MobSpawnSettings.SpawnerData> item : original.unwrap()) {
                        if (item.value().type() == EntityTypes.BAT) {
                            if (customWeight > 0) {
                                newList.add(new Weighted<>(item.value(), customWeight));
                            }
                        } else {
                            newList.add(item);
                        }
                    }
                    cir.setReturnValue(WeightedList.of(newList));
                }
            }
        }
    }
}
