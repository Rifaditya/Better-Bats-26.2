# 🦇 Ambient Spawning & Weight Modifiers

| Parameter | Specification Details |
|---|---|
| **Spawn Injector Mixin** | `net.vanillaoutsider.betterbats.mixin.NaturalSpawnerMixin` |
| **Surface Spawn Mixin** | `net.vanillaoutsider.betterbats.mixin.BatMixin.betterbats$onCheckBatSpawnRules` |
| **Mob Category** | `MobCategory.AMBIENT` |
| **Vanilla Base Spawn Weight** | `10` |
| **Mod Default Weight** | `30` (`better-bats:bat_spawn_weight`) |
| **Valid Surface Spawn Block Tag** | `#minecraft:bats_spawnable_on` |
| **Surface Spawn Sky Light** | `Sky Light <= 7` (Nighttime or dark forest cover) |

---

## 🌲 Dynamic Ambient Spawn Weight Replacement

In vanilla Minecraft, ambient mob spawning lists are statically fixed per biome. Better Bats utilizes `NaturalSpawnerMixin` to dynamically intercept `NaturalSpawner.mobsAt()` whenever the game checks for ambient spawns:

```java
@Inject(method = "mobsAt", at = @At("RETURN"), cancellable = true)
private static void betterbats$onMobsAt(
        ServerLevel level, StructureManager structureManager, ChunkGenerator generator, 
        MobCategory mobCategory, BlockPos pos, @Nullable Holder<Biome> biome, 
        CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> cir) {
    
    if (mobCategory == MobCategory.AMBIENT) {
        WeightedList<MobSpawnSettings.SpawnerData> original = cir.getReturnValue();
        if (original != null && !original.isEmpty()) {
            // Check if Bat exists in original spawn list
            boolean hasBat = original.unwrap().stream().anyMatch(item -> item.value().type() == EntityTypes.BAT);
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
```

- **Disabling Spawning**: Setting `/gamerule better-bats:bat_spawn_weight 0` completely strips bats from the `AMBIENT` spawn weight list, preventing any new natural bat spawning.

---

## 🌌 Surface Night Spawning Rules

Vanilla bats only spawn deep underground below specific Y-levels. Better Bats updates `Bat.checkBatSpawnRules()` via mixin to allow bats to naturally spawn outdoors on the surface at night:

```java
@Inject(method = "checkBatSpawnRules", at = @At("HEAD"), cancellable = true)
private static void betterbats$onCheckBatSpawnRules(
        EntityType<Bat> type, LevelAccessor level, EntitySpawnReason spawnReason, 
        BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
    
    boolean isSurface = pos.getY() >= level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos).getY();
    if (isSurface) {
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        if (skyLight <= 7 && level.getBlockState(pos.below()).is(BlockTags.BATS_SPAWNABLE_ON)) {
            cir.setReturnValue(Mob.checkMobSpawnRules(type, level, spawnReason, pos, random));
        } else {
            cir.setReturnValue(false);
        }
    }
}
```

---

## 🔗 Related Pages
- [[Bat Ecology, Photophobia & Daytime Roosting|Bat-Ecology-and-Photophobia]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Mixin Reference & Injection Hooks|Mixin-Reference-and-Hooks]]
