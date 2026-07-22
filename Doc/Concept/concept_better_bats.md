# Better Bats (Chiroptera Enhancements)

## Philosophy Fit
**Vanilla Outsider (VO)**: "One Click, One Action." This mod completely overhauls the ambient, useless vanilla Bat into a dynamic, ecosystem-driven entity. It draws heavily from DasikLibrary's Leader/Follower system to make caves and nights feel alive, immersive, and responsive to light sources without making them a farmable pet or magic mechanics.

## Mechanics
1. **Cave Opening & Surface Spawning**
   - **Description**: Alters the vanilla spawning logic for Bats. Instead of only spawning deep underground in darkness, Bats can now spawn near the surface explicitly out of cave openings or dark overhangs during twilight/night.
   - **Implementation**: Mixin into the `SpawnRestriction` or `Bat` entity's `canSpawn()` logic. Allow spawning at higher Y-levels if the sky light is low (nighttime) and there are solid stone/cave blocks nearby.

2. **The Flocking System (Leader/Follower)**
   - **Description**: Bats no longer fly erratically alone. They organize into cohesive swarms where a designated "Leader" bat dictates the general flight path, and "Follower" bats mirror the trajectory with slight offsets.
   - **Implementation**: Strictly use `DasikLibrary` v1.6.9+ `GroupMember` and `FollowLeaderGoal`. Assign the `PulseGuard` (AtomicLong) for master pulse ticking to drastically save TPS when simulating swarm paths.

3. **Guano Roosts (Ecosystem Fertility)**
   - **Description**: Replaces magical crop pollination. Bats roosting upside down slowly accumulate and drop "Guano" on the exact block beneath them. Over time, this passive guano drop accelerates crop growth by applying a bonemeal effect.
   - **Implementation**: Track roost ticks in `NBT`. Once an accumulation threshold (defined by `batGuanoThreshold`) is reached, check `pos.below(1..20)`. If Farmland is present and a crop is above it, apply `performBonemeal`. 

4. **Phototaxis & Predation (Lantern Hunting)**
   - **Description**: Similar to moths, Bats are attracted to artificial light sources. If a lantern or dynamic light is nearby, the swarm breaks normal pathing to hunt bugs around the light. Visual "crit" particles occasionally spawn to simulate eating insects.
   - **Implementation**: Create a custom `HuntLightGoal` using vanilla pathfinding targeting blocks with luminance > 12. Trigger client-side particle packets intermittently during circling.

5. **Pest Control (Dive-Bomb)**
   - **Description**: Symbiotic combat. Bats will aggressively swoop down and one-shot tiny pests (`Silverfish` and `Endermites`).
   - **Implementation**: Add `MeleeAttackGoal` targeting `Silverfish`/`Endermites`. Increase speed modifier heavily during the swoop.

6. **Acoustic Panic (Diurnal Roost Interruption)**
   - **Description**: At dawn, bats seek a "Home" block to sleep upside down. However, they are sensitive to vibrations. Loud noises (sprinting, mining, explosions) nearby will wake the swarm instantly, forcing them to scatter in panic and resetting their Guano drop timers for the day.
   - **Implementation**: Integrate basic `GameEvent` listener or Warden vibration mechanics in a small radius. Trigger a panic goal if threshold is met.

## Configuration (GameRules)
Via DasikLibrary, allowing for server performance tuning:
- `batSwarmSize` (Integer, default `5` - Max bats per leader in a local cave chunk)
- `batGuanoThreshold` (Integer, default `12000` - Ticks required to drop guano payload)
- `batPestControl` (Boolean, default `true` - Enable/Disable Silverfish hunting)

## Project Metadata
- **Version Format**: `1.1.0+build.1`
- **Internal Dependency**: `"dasik-library": "*"` (Standalone)
- **Archive Strategy**: Store old build `.jar`s in `/Archive/builds/`
- **Hive Mind Alignment**: MUST use `GroupMember` API. AI timers must NOT be synced to NBT.

## Assets Needed (Vanilla Repurposed)
- **Particles**: `crit` particles for lantern hunting. `falling_dust` (with Dirt/Podzol BlockState) for guano drop.
- **Sounds**: Native `ENTITY_BAT_TAKEOFF` mixed with low-pitch `ENTITY_PHANTOM_FLAP` to simulate swarm scatter.

## Implementation Checklist
- [x] Feature 1: Surface / Cave Opening Spawning Conditions modified
- [x] Feature 2: Leader/Follower goal integration (DasikLibrary)
- [x] Feature 3: Guano Roost soil enrichment logic
- [x] Feature 4: Phototaxis (Lantern bug hunting & particles)
- [x] Feature 5: Pest Control (Silverfish Dive-Bomb)
- [x] Feature 6: Acoustic Panic (Vibration listener & sleep interrupt)
- [x] Platform Docs updated (CurseForge/Modrinth)

---

## 🔮 Future Backlog

### Feature 7: Expanded Roost Hanging (Dripstone, Chains, & Lanterns)
- **Description**: Bats can roost upside down beneath Pointed Dripstone (stalactites), Iron Chains, Fences, and Hanging Lanterns in addition to solid full ceiling blocks.
- **Implementation**: Expand `isSuitableRoost` check in `BatSleepGoal` and `BatMixin` to recognize `POINTED_DRIPSTONE`, `CHAIN`, `FENCE`, and `LANTERN` block states.

### Feature 8: Physical Guano Item Harvest (GameRule: `better-bats:bat_drop_guano_item`)
- **Description**: Adds an optional GameRule (default: `false`) enabling roosting bats to drop a physical Bone Meal item when resting over non-farmland blocks, allowing for survival guano collector towers.
- **Implementation**: Register `better-bats:bat_drop_guano_item` boolean GameRule. When guano production threshold is met over non-farmland, spawn `Items.BONE_MEAL` item entity.

### Feature 9: Predator Avoidance (Cats, Ocelots, & Phantoms)
- **Description**: Bats panic and scatter if natural predators (`Cat`, `Ocelot`, `Phantom`) enter a 10-block radius.
- **Implementation**: Scan for feline and phantom entities in `BatPanicGoal` / `BatFlightHelper` to trigger panic flight.

