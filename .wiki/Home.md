# 🦇 Better Bats Wiki

Welcome to the official documentation for **Better Bats**, a comprehensive Minecraft Fabric mod that overhauls Chiroptera (bat) behavior with realistic 3D BOIDs flocking, phototropism light orbiting, daytime photophobia cave-returning, guano crop fertilization, pest control dive-bombing, vibration panic mechanics, and entity genetics integration.

> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 🧭 Navigating the Wiki

### 📦 Minecraft Version Guides
- [[Minecraft 26.2 Setup & Technical Guide|Minecraft-26.2-Guide]] — Primary target platform documentation (JDK 25, Fabric Loader >=0.19.1).
- [[Version & Modloader Compatibility Matrix|Version-Compatibility]] — Supported versions, `ModVersionGuard` security checks, and library dependency bounds.

---

### 🎮 Player & Mechanics Guides
- [[Bat Ecology, Photophobia & Daytime Roosting|Bat-Ecology-and-Photophobia]] — Daytime behavior, photophobia light thresholds, and valid roosting surfaces.
- [[3D BOIDs Flocking, Altitude Caps & Steering|3D-BOIDs-Flocking-and-Steering]] — Cohesion, alignment, separation math, twilight streaming, and surface altitude caps.
- [[Guano Production & Farmland Crop Fertilization|Guano-Production-and-Crop-Fertilization]] — Roosting guano timers, bonemeal crop growth, and physical item drops.
- [[Phototaxis & Nighttime Light Orbiting|Phototaxis-and-Light-Orbiting]] — Light source detection, moth-effect light hopping, and curved banking vectors.
- [[Pest Control & Predatory Dive-Bomb Combat|Pest-Control-and-Predatory-Combat]] — Silverfish and Endermite hunting, dive-bomb mechanics, and damage scaling.
- [[Acoustic Echolocation, Vibration Sensing & Panic|Acoustic-Echolocation-and-Panic]] — Sculk soul particles, echolocation clicks, and vibration panic responses.
- [[Animal Genetics & Inherited Chiroptera Traits|Animal-Genetics-and-Trait-Inheritance]] — Scale/wingspan, movement speed, and attack damage genetics.
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]] — Comprehensive guide to all 8 namespaced GameRules in `better-bats:better_bats`.
- [[Ambient Spawning & Weight Modifiers|Ambient-Spawning-and-Weight-Modifiers]] — Dynamic spawn weight control and surface night spawn rules.
- [[HUD Diagnostics & Client Configuration Screens|HUD-Diagnostics-and-Config-Screens]] — Cloth Config GUI integration, ModMenu, and `better-bats.json` defaults.
- [[Commands & Advancements Guide|Commands-and-Advancements-Guide]] — Integration with Minecraft `/gamerule` infrastructure and vanilla advancement loops.
- [[Sound Effects & Visual Particles Reference|Sound-Effects-and-Visual-Particles]] — Sound event definitions and particle emission tables.
- [[Troubleshooting & Frequently Asked Questions|Troubleshooting-and-FAQ]] — Startup crash troubleshooting, dependency resolution, and world config sync.

---

### 💻 Developer & Technical Reference
- [[Developer Setup & Building from Source|Developer-Setup-and-Building]] — Building with Gradle 9.3+, JDK 25, and Loom execution.
- [[Architecture & Package Layout|Architecture-and-Package-Layout]] — Standard package structure, design patterns, and class responsibilities.
- [[Mixin Reference & Injection Hooks|Mixin-Reference-and-Hooks]] — Table of bytecode injections into vanilla `Bat`, `GameEventDispatcher`, and `NaturalSpawner`.
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]] — AI Goal priority matrix, state accessors, and flag management.
- [[Consumer Mods Integration Guide|Consumer-Mods-Integration-Guide]] — Interfacing via DasikLibrary APIs, reading bat genetics, and trigger mechanics.

---

## 📜 Copyright & License

Better Bats is open-source software authored by **Dasik (Rifaditya)** and licensed under the **GNU General Public License v3.0 (GPL-3.0-or-later)**.
