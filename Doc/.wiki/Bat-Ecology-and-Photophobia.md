# 🦇 Bat Ecology, Photophobia & Daytime Roosting

| Feature Parameter | Specification Details |
|---|---|
| **Primary AI Goal Class** | `net.vanillaoutsider.betterbats.ai.BatSleepGoal` |
| **Roost Validator Class** | `net.vanillaoutsider.betterbats.ai.BatRoostHelper` |
| **Daytime Sky Light Limit** | `Sky Light = 0` |
| **Daytime Block Light Limit** | `Block Light <= 7` |
| **Roost Search Radius** | `16 blocks` (Horizontal) / `10 blocks` (Vertical) |
| **Cluster Preference** | Prefers roosting within `5x3x5` blocks of existing resting bats |
| **Supported Roost Surfaces** | Solid ceilings, stalactites, lanterns, chains, fences, walls, leaves |

---

## ☀️ Daytime Photophobia Mechanics

Unlike vanilla Minecraft where bats randomly fly regardless of time or daylight, Better Bats introduces **Photophobia**. During daylight hours (`level.isBrightOutside()`) or when raining while exposed to sky (`level.isRaining() && level.canSeeSky(pos)`), bats actively seek out dark cave overhangs or covered roosting spots.

```
                  ┌────────────────────────┐
                  │   Daylight / Storm     │
                  └───────────┬────────────┘
                              │
                    Can see sky / Bright?
                     ┌────────┴────────┐
                    YES                NO
                     │                 │
           ┌─────────▼────────┐  ┌─────▼────────┐
           │ Seek Dark Cover  │  │ Check Roost  │
           │  (BatSleepGoal)  │  │ Surface Type │
           └─────────┬────────┘  └─────┬────────┘
                     │                 │
             Sky Light == 0 &&         │
             Block Light <= 7?         │
                     │                 │
           ┌─────────▼─────────────────▼────────┐
           │     Attach Ceiling & Sleep         │
           │        (setResting(true))          │
           └────────────────────────────────────┘
```

---

## 🪨 Valid Roosting Surfaces (`BatRoostHelper`)

Roosting is evaluated via `BatRoostHelper.isSuitableRoost(Level level, BlockPos pos, BlockPos above)`:

1. **Solid Ceiling Blocks**: Any block with a sturdy bottom face (`aboveState.isFaceSturdy(level, above, Direction.DOWN)`) or redstone conductor.
2. **Pointed Dripstone / Speleothem**: Stalactite tips pointing downwards (`SpeleothemBlock.TIP_DIRECTION == Direction.DOWN`).
3. **Hanging Lanterns**: Lanterns suspended from ceilings (`LanternBlock.HANGING == true`).
4. **Hanging Chains & Supports**: Blocks tagged under `#minecraft:chains`, `#minecraft:fences`, `#minecraft:walls`, and `#minecraft:leaves`.

```java
public static boolean isSuitableRoost(Level level, BlockPos pos, BlockPos above) {
    if (!level.isEmptyBlock(pos)) return false;
    BlockState aboveState = level.getBlockState(above);
    if (aboveState.isAir()) return false;

    if (aboveState.isFaceSturdy(level, above, Direction.DOWN) || aboveState.isRedstoneConductor(level, pos)) return true;
    if (aboveState.getBlock() instanceof SpeleothemBlock || aboveState.getBlock() instanceof PointedDripstoneBlock) {
        if (aboveState.hasProperty(SpeleothemBlock.TIP_DIRECTION) && aboveState.getValue(SpeleothemBlock.TIP_DIRECTION) == Direction.DOWN) return true;
    }
    if (aboveState.getBlock() instanceof LanternBlock && aboveState.getValue(LanternBlock.HANGING)) return true;
    return aboveState.is(BlockTags.CHAINS) || aboveState.is(BlockTags.FENCES) || aboveState.is(BlockTags.WALLS) || aboveState.is(BlockTags.LEAVES);
}
```

---

## 🦇 Roost Clustering Behavior

When a bat decides to sleep, it scans a 16-block bounding box for already resting bats. If resting bats are present, it prioritizes selecting a candidate roost block offset within a `5x3x5` neighborhood of its fellow bats, creating natural colony clusters on cave ceilings and under dark structures.

---

## 🔗 Related Pages
- [[3D BOIDs Flocking, Altitude Caps & Steering|3D-BOIDs-Flocking-and-Steering]]
- [[Guano Production & Farmland Crop Fertilization|Guano-Production-and-Crop-Fertilization]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
