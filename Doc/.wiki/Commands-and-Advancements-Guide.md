# 📜 Commands & Advancements Guide

| Feature Domain | Implementation Status | Technical Mechanism |
|---|---|---|
| **Custom Brigadier Subtrees** | Not Implemented (By Design) | Integrated directly with vanilla `/gamerule` system |
| **Custom Advancement JSONs** | Not Implemented (By Design) | Leverages vanilla mob triggers and Advancement criteria |
| **GameRule Registration** | Active | `DynamicGameRuleManager.integerRule()` & `booleanRule()` |

---

## 📌 Anti-Phantom & Absence Disclaimer

> 📌 **Architectural Design Policy**: Better Bats deliberately refrains from adding redundant custom root commands (such as `/betterbats`) or custom advancement JSON files. In keeping with the **Vanilla Outsider** design philosophy, all mod configuration is wired directly into Minecraft's native `/gamerule` engine, ensuring total compatibility with existing admin tools, datapacks, and command blocks.

---

## 🖥️ Vanilla Command Integration

All configuration settings are managed via the standard `/gamerule` command:

```mcfunction
# Query active bat swarm size
/gamerule better-bats:bat_swarm_size

# Set guano timer threshold to 6000 ticks (5 minutes)
/gamerule better-bats:bat_guano_threshold 6000

# Toggle pest control dive-bombing
/gamerule better-bats:bat_pest_control true
```

---

## 🏆 Advancement Triggers & Vanilla Loops

While Better Bats does not add custom advancement entries to the Advancement screen, all entity interactions fully trigger vanilla Minecraft advancement criteria:
- Slaying pests (Silverfish/Endermites) via bat dive-bombing triggers mob kill predicates for the bat's damage source (`damageSources().mobAttack(this.bat)`).
- Guano farmland fertilization triggers crop growth and bonemeal particle events (`levelEvent 2005`).

---

## 🔗 Related Pages
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Guano Production & Farmland Crop Fertilization|Guano-Production-and-Crop-Fertilization]]
