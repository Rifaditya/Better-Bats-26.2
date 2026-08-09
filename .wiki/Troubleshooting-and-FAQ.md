# ❓ Troubleshooting & Frequently Asked Questions

---

## ❓ Frequently Asked Questions

### Q1: Why are bats not spawning in my world?
**Answer**: 
1. Check the active GameRule value using `/gamerule better-bats:bat_spawn_weight`. If set to `0`, spawning is completely disabled.
2. Remember that surface spawning requires nighttime or dark forest overhangs (`Sky Light <= 7`) over valid spawn blocks (`#minecraft:bats_spawnable_on`).

---

### Q2: Why are changes in `better-bats.json` not taking effect in my existing world?
**Answer**: 
Values in `.minecraft/config/better-bats.json` set baseline defaults for **NEW WORLDS ONLY**. Existing active worlds store their configuration inside the world's `level.dat` GameRules. To update an existing world, use `/gamerule` commands in-game.

---

### Q3: Why are resting bats dropping Bone Meal items on my floor?
**Answer**: 
The GameRule `better-bats:bat_drop_guano_item` is set to `true`. By default, this is `false` (bats fertilize crops directly without dropping physical item entities). Set `/gamerule better-bats:bat_drop_guano_item false` to stop item drops.

---

## 🛠️ Common Startup Errors & Solutions

### Error 1: `RuntimeException: Better Bats requires 'dasik-library' to be loaded!`
- **Cause**: DasikLibrary is missing from your `mods/` directory.
- **Solution**: Download and install `dasik-library` (version `>=1.8.2`) matching your Minecraft version.

---

### Error 2: `IncompatibleClassChangeError: [Better Bats] This mod build is incompatible...`
- **Cause**: You are attempting to run a Minecraft 26.2 build (`better-bats-1.1.23+26.2.jar`) on an incompatible or legacy Minecraft release.
- **Solution**: Ensure your game version is Minecraft 26.2 and Fabric Loader is `0.19.1` or newer.

---

## 🔗 Related Pages
- [[Minecraft 26.2 Setup & Technical Guide|Minecraft-26.2-Guide]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Version & Modloader Compatibility Matrix|Version-Compatibility]]
