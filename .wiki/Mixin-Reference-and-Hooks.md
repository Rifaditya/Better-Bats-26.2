# 🔌 Mixin Reference & Injection Hooks

Better Bats uses Bytecode Mixins to modify vanilla entity and world spawning behavior while keeping world saves 100% vanilla-safe.

---

## 📋 Comprehensive Mixin Injection Table

| Mixin Class | Target Vanilla Class | Target Method | `@At` Injection Point | Cancellation / Behavior |
|---|---|---|---|---|
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `<init>` | `@At("TAIL")` | Registers custom AI goals (Panic, Sleep, HuntLight, DiveBomb) into `goalSelector`. |
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `customServerAiStep` | `@At("HEAD")` | `cancellable = true`. Completely replaces vanilla flight calculation with `BatFlightHelper` forces and resting logic. |
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `checkBatSpawnRules` | `@At("HEAD")` | `cancellable = true`. Validates surface nighttime spawning rules. |
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `addAdditionalSaveData` | `@At("TAIL")` | Writes `betterbats:guano_ticks` integer tag to `ValueOutput`. |
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `readAdditionalSaveData` | `@At("TAIL")` | Reads `betterbats:guano_ticks` integer tag from `ValueInput`. |
| `BatMixin` | `net.minecraft.world.entity.ambient.Bat` | `tick` | `@At("TAIL")` | Ticks guano production, farmland fertilization, and gravity state (`setNoGravity(!isResting())`). |
| `GameEventDispatcherMixin` | `net.minecraft.world.level.gameevent.GameEventDispatcher` | `post` | `@At("HEAD")` | Listens for `EXPLODE`, `BLOCK_DESTROY`, or sprinting `STEP` within 16 blocks to wake up resting bats and trigger 100-tick panic. |
| `NaturalSpawnerMixin` | `net.minecraft.world.level.NaturalSpawner` | `mobsAt` | `@At("RETURN")` | `cancellable = true`. Dynamically replaces bat ambient spawn weight with `better-bats:bat_spawn_weight` GameRule. |
| `MobAccessor` | `net.minecraft.world.entity.Mob` | `getGoalSelector` | `@Accessor` | Exposes `Mob.goalSelector` field to `BatMixin`. |

---

## 🔗 Related Pages
- [[Architecture & Package Layout|Architecture-and-Package-Layout]]
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]]
- [[Ambient Spawning & Weight Modifiers|Ambient-Spawning-and-Weight-Modifiers]]
