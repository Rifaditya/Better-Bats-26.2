# 🖥️ HUD Diagnostics & Client Configuration Screens

| Parameter | Specification Details |
|---|---|
| **Config File Location** | `.minecraft/config/better-bats.json` |
| **Config Version** | `1` (`public int configVersion = VERSION`) |
| **Config Helper API** | `net.dasik.social.api.config.ConfigHelper` |
| **ModMenu Entrypoint Class** | `net.vanillaoutsider.betterbats.config.ModMenuIntegration` |
| **Optional Screen Builder** | `net.vanillaoutsider.betterbats.config.ClothConfigScreenHelper` |

---

## ⚙️ Baseline Config JSON (`better-bats.json`)

On initial startup, `BetterBatsConfig.load()` generates a template JSON file inside the user's config folder backing default GameRule values for new world generation:

```json
{
  "configVersion": 1,
  "batSwarmSize": 5,
  "batGuanoThreshold": 12000,
  "batPestControl": true,
  "batAlignment": 5,
  "batCohesion": 5,
  "batSeparation": 10,
  "batSpawnWeight": 30,
  "batDropGuanoItem": false
}
```

---

## 🛠️ ModMenu & Cloth Config Integration

Better Bats integrates cleanly with **ModMenu** and **Cloth Config** (if loaded in the client environment). Accessing the mod config screen through ModMenu allows players to visually edit default baseline options:

```java
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
                return ClothConfigScreenHelper.create(parent);
            }
            return null;
        };
    }
}
```

---

## ⚠️ 2-Way Sync Warning Display

The configuration screen renders a warning notice in the UI header explaining world scope:

> ⚠️ **WARNING**: Changes made in this client screen only affect baseline defaults for **NEW worlds**. To configure your active/existing world, use the in-game Collapsible Game Rule Screen or `/gamerule` commands.

---

## 🔗 Related Pages
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Minecraft 26.2 Setup & Technical Guide|Minecraft-26.2-Guide]]
