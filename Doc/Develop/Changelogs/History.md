# Better Bats Changelog

All notable changes to this project will be documented in this file.

## [1.1.9-26.2] - 2026-06-19

### Changed
- **AI/Movement**: Replaced the leader-follower hierarchy with a true leaderless BOIDs murmuration model. All bats now run independent local cohesion, alignment, and separation calculations from their neighbors.
- **AI/Wandering**: Restored standard vanilla wandering AI as the base individual intent when not influenced by neighbors.

## [1.1.8-26.2] - 2026-06-19

### Fixed
- **AI/Movement**: Fixed follower bats losing AI movement, falling, drowning, and teleporting by overriding distance checks to keep follow leader goal active continuously.
- **Flight Mechanics**: Integrated ground/water avoidance, ceiling avoidance, and sky-seeking (night) / cave-seeking (day) light preference steering forces.

## [1.1.7-26.2] - 2026-06-19

### Fixed
- **AI/Movement**: Fixed bats constantly teleporting to the leader instead of flying smoothly. Follower bats now execute boids flocking steering vectors every tick, and the default teleportation threshold has been increased to 32 blocks (1024.0f distance squared).

## [1.1.6-26.1.2] - 2026-06-14

### Fixed
- **Performance**: Optimized `BatPanicGoal` to eliminate all intermediate heap allocations inside its active tick loop by switching vector math to double primitives.

## [1.1.5-26.1.2] - 2026-06-14

### Added
- **Configuration Screen**: Added optional ModMenu and Cloth Config configuration GUI integration. The configuration settings define baseline defaults used to register/initialize GameRules for new worlds.

## [1.1.4-26.1.2] - 2026-06-14

### Added
- **Configurable Spawn Rates**: Added a new dynamic GameRule `better-bats:bat_spawn_weight` to dynamically scale or disable bat spawning. Increased the default weight to 30 (from 10) to make bats spawn more commonly by default.

## [1.1.3-26.1.2] - 2026-06-14

### Fixed
- **Performance**: Optimized `BatHuntLightGoal` to avoid intermediate `Vec3` allocations by caching target coordinates and using double-based math.

## [1.1.2+A-26.1.2] - 2026-06-14

### Changed
- **Versioning**: Migrated the versioning scheme to the standard Zenith release format (`Major.Minor.Patch+[Stage]-[MC_Version]`).

### Fixed
- **Performance**: Optimized `BatFollowLeaderGoal` to cache parameters and eliminate heap allocations in the active tick loop.

### Concept Coverage
- Features implemented: 6/6 (100%)
- Missing: None

## [1.1.1+build.4] - 2026-05-20

### Changed
- **Target Citations**: Aligned all Mojang source verification citations to target Minecraft 26.1.2 Release instead of the developer snapshots.

## [1.1.1+build.3] - 2026-05-20

### Added
- **Acoustic Panic (Sleep Interrupt)**: Implemented full Acoustic Panic goal mechanics. Resting bats within a 16-block radius of loud sounds (explosions, mining, sprinting players) will wake up, panic with high-speed flight, disperse from their flock, play mixed bat takeoff and low-pitch phantom flap sounds, and reset their guano accumulation progress.
- **Dependency Guard**: Added entrypoint runtime verification for the presence of `dasik-library`.
- **Refmap and Compile Integrity**: Added missing refmap configuration to mixins file and resolved all source verification class citations targeting Minecraft 26.1 Snapshot 11.

## [1.1.1+build.2] - 2026-05-12

### Added
- **Diurnal Roosting (The Sleep)**: Bats now actively seek dark spots (Sky Light 0) during the day to roost.
- **Light Avoidance**: Bats will no longer be attracted to artificial light sources during the day.
- **Nocturnal Awakening**: Bats will automatically wake up and disperse at nightfall.

## [1.1.1+build.1] - 2026-05-12

### Changed
- **Standard Core Migration**: Fully refactored GameRule management to use `DynamicGameRuleManager` from DasikLibrary 1.7.0.
- **Boids Murmuration**: Implemented `BatFollowLeaderGoal` with dynamic `Alignment`, `Cohesion`, and `Separation` weights tunable at runtime via GameRules.
- **Thin Architecture**: Removed `GameRuleHelper` and offloaded AI logic from Mixins to dedicated goal classes.

## [1.1.0+build.1] - 2026-04-20

### Added
- **Custom GameRule Category**: Added a dedicated `Better Bats` category in the world settings menu for better organization.
- **Dynamic Config**: Implemented real-time, server-side configuration via GameRules:
    - `batSwarmSize`: Control the maximum size of bat flocks.
    - `batGuanoThreshold`: Adjust the speed of guano production (fertility).
    - `batPestControl`: Toggle predatory behavior against Silverfish and Endermites.
- **Translations**: Added full localization and descriptive tooltips for all new GameRules in `en_us.json`.

### Changed
- **Standard Alignment**: Re-engineered the GameRule registry system to match the high-quality pattern used in **Better Dogs**. 
- **Registry Overhaul**: Replaced the deprecated/removed `GameRuleRegistry` with direct `Registry.register` using the official `new GameRule<>()` constructor.

### Fixed
- **Snapshot 26.1 Compatibility**: Fixed issues where the mod would fail to compile or run due to private method access in the latest Minecraft snapshots.
- **Mixin Overrides**: Resolved a compilation error in `BatMixin.java` regarding the `getMaxSpawnClusterSize` method signature.
- **Server-Side Safety**: Fixed potential client-side crashes by ensuring all GameRule lookups are strictly gated to the `ServerLevel`.

## [1.0.0+build.2] - 2026-04-18
### Added
- Official mod icon (v2 - Roosting Bat).
- Basic localization (en_us.json).
- Project LICENSE (GPLv3).
- Root README.md.

## [1.0.0+build.1] - 2026-04-18
### Added
- Initial implementation for Minecraft 26.1.
- **Hive Mind**: Bats now form swarms using DasikLibrary.
- **Guano Roosts**: Resting bats passively fertilize farmland below.
- **Phototaxis**: Bats circle bright light sources (>12 brightness).
- **Pest Control**: Bats attack Silverfish and Endermites.
- **Acoustic Panic**: Loud sounds wake resting bats in a 16-block radius.
- **Surface Spawning**: Bats can now spawn on the surface at night (sky light <= 7).
