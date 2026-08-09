# 🏛️ Architecture & Package Layout

| Design Principle | Architectural Policy |
|---|---|
| **Core Philosophy** | Vanilla Outsider (VO) — Diegetic, non-destructive, input-consistent |
| **Design Rule** | 1 File, 1 Function Law (Single-responsibility Cohesion) |
| **Main Package Namespace** | `net.vanillaoutsider.betterbats` |
| **Thread Safety Model** | Server-Thread Main Looper Sync (No async world mutation) |

---

## 🌳 ASCII Package Hierarchy Tree

```
net.vanillaoutsider.betterbats
├── BetterBatsFabric.java             [ModInitializer: GameRules, Genetics setup]
├── BetterBatsFabricClient.java       [ClientModInitializer]
├── BatStateAccessor.java             [State Accessor Interface: Panic & Goals]
│
├── ai
│   ├── BatDiveBombGoal.java          [Predatory Attack Goal on Silverfish & Endermites]
│   ├── BatFlightHelper.java          [3D BOIDs Flocking & Environmental Flight Steering]
│   ├── BatHuntLightGoal.java         [Phototaxis Light Orbiting & Banking AI Goal]
│   ├── BatPanicGoal.java             [Vibration Scattering Panic Flight AI Goal]
│   ├── BatRoostHelper.java           [Roost Surface Surface Validator Helper]
│   └── BatSleepGoal.java             [Photophobia Daytime Cave Roosting AI Goal]
│
├── config
│   ├── BetterBatsConfig.java         [Config Data Record & JSON Helper]
│   ├── ClothConfigScreenHelper.java  [Cloth Config GUI Integration Helper]
│   └── ModMenuIntegration.java       [ModMenu API Screen Provider]
│
├── mixin
│   ├── BatMixin.java                 [Core Bat Overhaul: Tick, AI, Guano, Echolocation]
│   ├── GameEventDispatcherMixin.java [Vibration Listener: Explosion, Step, Destroy]
│   ├── MobAccessor.java              [Exposes Mob.getGoalSelector()]
│   └── NaturalSpawnerMixin.java      [Dynamic Ambient Spawn Weight Interceptor]
│
└── util
    └── ModVersionGuard.java          [Zero-Dependency Version Safety Checker]
```

---

## 🔒 1 File, 1 Function Architectural Law

Every class in Better Bats follows strict single-function responsibility:
- `BatFlightHelper`: Purely handles vector math calculations for BOIDs flocking, ground/ceiling avoidance, and day/night flight steering.
- `BatRoostHelper`: Purely evaluates block state suitability for bat hanging/roosting.
- `GameEventDispatcherMixin`: Purely listens for world vibration events and triggers panic accessors.

---

## 🔗 Related Pages
- [[Developer Setup & Building from Source|Developer-Setup-and-Building]]
- [[Mixin Reference & Injection Hooks|Mixin-Reference-and-Hooks]]
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]]
