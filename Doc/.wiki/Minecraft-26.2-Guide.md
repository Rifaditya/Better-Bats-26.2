# 📦 Minecraft 26.2 Setup & Technical Guide

| Parameter | Specification Details |
|---|---|
| **Target Version** | Minecraft 26.2 (Stable) |
| **Java JDK** | Java 25 (Adoptium HotSpot `jdk-25.0.3.9-hotspot`) |
| **Fabric Loader** | `>=0.19.1` |
| **Fabric API** | `0.150.1+26.2` |
| **DasikLibrary Dependency** | `>=1.8.2` (Compiled against `1.8.9`) |
| **Mod Version** | `1.1.23+26.2` |
| **Jar File Naming** | `better-bats-1.1.23+26.2.jar` |
| **Main Entrypoint** | `net.vanillaoutsider.betterbats.BetterBatsFabric` |
| **Client Entrypoint** | `net.vanillaoutsider.betterbats.BetterBatsFabricClient` |
| **ModMenu Entrypoint** | `net.vanillaoutsider.betterbats.config.ModMenuIntegration` |

---

## 📖 Overview

Better Bats for Minecraft 26.2 is engineered specifically to run natively under Java 25 and Minecraft 26.2's updated entity and world API structures. It replaces standard ambient bat flight routines with realistic 3D BOIDs flocking math, phototaxis light orbiting, daytime photophobia roosting, guano farmland fertilization, and Silverfish/Endermite pest control dive-bombing.

> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## ⚡ Zero-Dependency Version Safety (`ModVersionGuard`)

Better Bats implements zero-dependency runtime class checking to guarantee that execution halts immediately with a human-readable diagnostic error if loaded on an unsupported legacy or incompatible Minecraft release:

```java
net.vanillaoutsider.betterbats.util.ModVersionGuard.checkClass("Better Bats", "net.minecraft.world.entity.EntityTypes");
```

If `EntityTypes` cannot be resolved via `Thread.currentThread().getContextClassLoader()`, an `IncompatibleClassChangeError` is thrown before any mixin or entity classloader initialization occurs.

---

## ⚙️ Installation Instructions

1. Download and install **Fabric Loader** (`0.19.1` or newer) for Minecraft 26.2.
2. Place `dasik-library-1.8.9+26.2.jar` (or compatible version `>=1.8.2`) into your world server/client `mods/` directory.
3. Place `better-bats-1.1.23+26.2.jar` into the `mods/` directory.
4. Launch Minecraft. On first launch, baseline config defaults will automatically generate at `.minecraft/config/better-bats.json`.

---

## 🔗 Related Pages
- [[Version & Modloader Compatibility Matrix|Version-Compatibility]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Developer Setup & Building from Source|Developer-Setup-and-Building]]
