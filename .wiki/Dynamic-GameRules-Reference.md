# ⚙️ Dynamic GameRules Reference Table

Category Identifier: **`better-bats:better_bats`** (§lBetter Bats)

Better Bats utilizes `DynamicGameRuleManager` from DasikLibrary to register dynamic, per-world GameRules that sync seamlessly with client configuration menus while backing live world gameplay logic.

---

## 📋 Complete GameRules Matrix

| GameRule Namespace Key | Type | Default Value | Valid Range | Localization Display Title | Description & Behavior |
|---|---|---|---|---|---|
| `better-bats:bat_swarm_size` | Integer | `5` | Unbounded | Bat Swarm Size | Maximum size of bat flocks. When met, bats stop recruiting new members. Set to `0` to disable flocking. |
| `better-bats:bat_guano_threshold` | Integer | `12000` | Unbounded | Guano Production Speed | Time interval in game ticks required for a resting bat to produce guano/fertilizer ($12000\text{t} = 10\text{min}$). Lower is faster. |
| `better-bats:bat_pest_control` | Boolean | `true` | `true`/`false` | Enable Pest Control | If `true`, bats aggressively dive-bomb, target, and defeat Silverfish and Endermites. |
| `better-bats:bat_alignment` | Integer | `5` | `0` to `100` | Bat Alignment Weight | Scale factor dictating how strongly bats match velocity and flight heading of nearby flock members. |
| `better-bats:bat_cohesion` | Integer | `5` | `0` to `100` | Bat Cohesion Weight | Scale factor dictating how strongly bats are drawn toward the center of mass of their flock. |
| `better-bats:bat_separation` | Integer | `10` | `0` to `100` | Bat Separation Weight | Scale factor controlling how aggressively bats avoid collision and push away from neighbors. |
| `better-bats:bat_spawn_weight` | Integer | `30` | `0` to `100` | Bat Spawn Weight | Ambient spawn weight in dark surface/cave settings (Vanilla is `10`). Set to `0` to disable bat spawning entirely. |
| `better-bats:bat_drop_guano_item` | Boolean | `false` | `true`/`false` | Drop Guano Items | If `true`, resting bats drop physical Bone Meal item entities over non-farmland blocks or fully grown crops. |

---

## 💻 In-Game Command Execution Examples

Server operators and singleplayer players can modify any GameRule live in-game using standard vanilla Brigadier `/gamerule` commands:

```mcfunction
# Set bat flock size limit to 10
/gamerule better-bats:bat_swarm_size 10

# Accelerate guano production to every 2 minutes (2400 ticks)
/gamerule better-bats:bat_guano_threshold 2400

# Enable physical bone meal item drops
/gamerule better-bats:bat_drop_guano_item true

# Query active bat spawn weight
/gamerule better-bats:bat_spawn_weight
```

---

## ⚠️ Configuration Sync Warning

> ⚠️ **GameRule vs Config Sync Note**: Values modified in `.minecraft/config/better-bats.json` set baseline defaults for **NEW WORLDS ONLY**. Existing active worlds read and store their values directly inside the world's `level.dat` GameRule container. To reconfigure an existing world, use `/gamerule` or the in-game Collapsible Game Rule Screen.

---

## 🔗 Related Pages
- [[3D BOIDs Flocking, Altitude Caps & Steering|3D-BOIDs-Flocking-and-Steering]]
- [[Guano Production & Farmland Crop Fertilization|Guano-Production-and-Crop-Fertilization]]
- [[HUD Diagnostics & Client Configuration Screens|HUD-Diagnostics-and-Config-Screens]]
