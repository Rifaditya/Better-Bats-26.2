<p align="center">
  <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
  <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-8A2BE2?style=for-the-badge" alt="Requires Dasik Library"></a>
  <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&logo=java" alt="Java 25">
  <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License GPLv3">
  <img src="https://img.shields.io/badge/Minecraft-26.2+-brightgreen?style=for-the-badge" alt="Minecraft 26.2+">
</p>

# 🦇 Better Bats

> **"From useless cave annoyance to essential ecological companions."**

> [!NOTE]
> **1 Jar 1 Version Policy:** I build **1 dedicated JAR for each Minecraft version** (e.g. MC 26.2, MC 26.3). Please download the exact build that matches your Minecraft installation.
> <br><br>
> **Dependency Requirement:** For modern Minecraft 26.x releases (26.1.2, 26.2, 26.3+), this mod requires both **Fabric API** and **Dasik Library** (`v1.8.15+`). Legacy builds (1.20.1, 1.21.x) are self-contained and only require Fabric API.

In vanilla Minecraft, bats are purely cosmetic, erratic, ambient entities that offer no mechanical purpose or interactive value. They fly aimlessly into lava pits, spawn in pitch darkness, drop zero items or XP, and chirp in annoying loops before vanishing into thin air.

**Better Bats** transforms bats into living, intelligent, and symbiotic cave fauna. Using a true Boids murmuration algorithm, bats organize into fluid, leaderless aerial flocks. They roost upside down on dripstone, chains, and lanterns, drop natural guano fertilizer onto crops below, hunt annoying insect pests (silverfish, endermites) with lethal dive-bomb attacks, navigate pitch-dark caverns using sonic echolocation pulses, swarm toward lanterns at night like moths, and panic dynamically from loud explosions or stalking feline predators.

Part of the **Vanilla Outsider Collection** — mods that refine vanilla mechanics with modern engineering standards.

---

## ✨ Features

### 🐝 Murmuration: Boids Aerial Flocking
Bats no longer fly in erratic, chaotic solo paths:
- **Leaderless Swarms**: Organized via a true 3D Boids flocking algorithm (Cohesion, Alignment, and Separation). Each bat adjusts its flight dynamically based on nearby flock mates.
- **Configurable Swarm Caps**: Swarm recruitment caps out cleanly at `bat_swarm_size` (default: 5 members), preventing overcrowded clusters while maintaining organic murmurations.
- **Twilight Funneling**: At dusk (12,000-14,000 ticks) and dawn (22,000-24,000 ticks), cohesion and alignment scale up by 2.5x while separation tightens, causing swarms to funnel together dramatically as they exit or enter cavern mouths.

### 💩 Guano Fertility & Renewable Bone Meal
Bats actively enrich subterranean farms and surface crops:
- **Roosting Guano Accumulation**: Resting bats quietly accumulate guano while roosting upside down. Once the threshold is met (`bat_guano_threshold`, default 12,000 ticks / ~10 minutes), the bat discharges a guano fertilization cycle.
- **Deep Crop Enrichment**: The bat scans up to **20 blocks straight down**. If farmland with crops is detected, it triggers a bone meal growth spurt accompanied by bright green **Happy Villager** particles and falling mycelium cues.
- **Physical Guano Harvest**: Toggle `better-bats:bat_drop_guano_item true` so roosting bats drop physical **Bone Meal item entities** whenever resting over non-farmland blocks or when crops below are already fully matured.

### 🦟 Symbiotic Pest Control (Silverfish & Endermite Defense)
Bats become valuable allies against underground infestations:
- **Aggressive Dive-Bombing**: When bats detect **Silverfish** or **Endermites** within a **10-block radius**, they break flock formation and perform high-speed dive-bomb attacks.
- **Lethal Strikes**: Bats deal scalable attack damage (1.0 to 4.0 damage) inherited through genetic traits, quickly exterminating creeping stone pests before they swarm the player.

### 🦇 Pitch-Dark Cavern Echolocation
Deep underground where light levels drop to absolute zero, bats utilize biological sonar:
- **Acoustic Pulses**: When flying in pitch darkness (Sky Light 0, Block Light < 4), bats emit periodic high-pitched echolocation clicks.
- **Sonic Ring Particles**: Each click emits subtle **Sculk Soul** particles, giving explorers a visible hint of cavern ceilings and bat roosts far above.

### 🪔 Phototaxis: The Moth Effect
During nighttime, bats exhibit natural attraction to bright artificial illumination:
- **Lantern Circling**: Bats break away from swarms to investigate light sources with brightness > 8 (torches, lanterns, campfires).
- **Smooth Banking Flight**: Approaches lanterns with a smooth curved banking maneuver and circles with a gentle vertical sine-wave bobbing motion.
- **Cooldown Dissipation**: After 10-30 seconds of feeding on ambient insects around the light, bats disperse and resume normal flocking.

### 😱 Acoustic Panic & Predator Avoidance
Living creatures with sensitive hearing and natural survival instincts:
- **Acoustic Disruption**: Loud acoustic disturbances (sprinting players, mining blocks, explosions) within **16 blocks** instantly wake roosting bats, triggering frantic high-speed scatter flight and resetting guano timers.
- **Feline & Phantom Avoidance**: Bats continuously scan for natural predators (**Cats**, **Ocelots**, **Phantoms**) within a 10-block radius. Resting bats wake immediately, and flying bats receive powerful directional flee vectors pushing them away from danger.

### 🏠 Universal Roosting Anchors
Bats can now hang upside down from authentic environmental perches:
- **Pointed Dripstone**: Stalactites hanging from cavern ceilings.
- **Iron Chains & Hanging Lanterns**: Industrial and dungeon fixtures.
- **Fences, Stone Walls & Tree Foliage**: Surface forest and village roosts.
- **Slabs & Stairs**: Any solid underside surface.

### 🧬 Dasik Animal Genetics Integration
Powered by the DasikLibrary Animal Genetics API:
- **Wingspan / Scale**: Natural physical variation from **0.75x to 1.30x** scale.
- **Movement Speed**: Flight velocities vary from **-4% to +8%** based on genetic roll.
- **Attack Power**: Dive-bomb pest damage scales organically from **1.0 to 4.0**.

### 🧩 Compatibility & HUD Integration
- **100% Server-Side Compatible**: Runs entirely on the server. Vanilla clients can join modded servers without downloading anything locally.
- **ModMenu + YACL Screen**: Comprehensive client config screen in singleplayer to customize swarm sizes and guano rates visually.
- **Zero NBT Pollution**: State accessors operate safely in memory with seamless world save upgrades.

---

## 📊 Quick Reference & Mechanics Matrix

| Gameplay Aspect | Vanilla Minecraft | Better Bats (Modern 26.2+) |
| :--- | :---: | :---: |
| **Flight Pattern** | Erratic, chaotic, solitary random drift | **Organized Boids Murmuration** (Cohesion + Alignment) |
| **Swarm Mechanics** | ❌ None (Solo entities only) | ✅ **5-member flocks** with twilight dawn/dusk funneling |
| **Resting / Roosting** | Solid ceiling blocks only | ✅ **Dripstone, chains, lanterns, leaves, walls, slabs** |
| **Farmland Interaction** | ❌ None | ✅ **Guano fertilization** scans 20 blocks down onto crops |
| **Physical Bone Meal Drops** | ❌ None | ✅ **Configurable item drops** (`bat_drop_guano_item`) |
| **Pest Control** | Passive / ignores mobs | ✅ **Dive-bombs Silverfish & Endermites** (1-4 damage) |
| **Cavern Navigation** | Silent random drift | ✅ **Echolocation clicks & Sculk Soul particles** |
| **Reaction to Lanterns** | Ignores light | ✅ **Phototaxis moth effect** (circles lights at night) |
| **Disturbance & Noise** | Only wakes on direct bump | ✅ **16-block acoustic panic** from mining, sprints, blasts |
| **Predator Reaction** | Ignores cats and ocelots | ✅ **10-block directional flee response** from felines |

---

## 🚀 In-Game Commands & Quick Start

Better Bats features a dedicated, full-featured Brigadier command suite accessible via `/betterbats` or the compact alias `/bb`:

| Command Syntax | Permission Level | Description |
| :--- | :---: | :--- |
| `/betterbats help` *(or `/bb help`)* | All Players | Displays interactive command syntax and subcommands. |
| `/betterbats status` *(or `/bb status`)* | All Players | Summarizes active swarm sizes, guano timers, and pest control states. |
| `/betterbats get <rule>` | All Players | Queries the current value of a specific Better Bats rule. |
| `/betterbats set <rule> <val>` | Gamemasters (Level 2) | Modifies a GameRule live in-game and synchronizes to config. |
| `/betterbats reset` | Gamemasters (Level 2) | Resets all Better Bats GameRules to factory defaults. |
| `/betterbats reload` | Gamemasters (Level 2) | Reloads JSON config from disk and synchronizes active world state. |
| `/betterbats debug inspect` | Gamemasters (Level 2) | Inspects genetic traits, scale, and guano ticks of the nearest bat. |
| `/betterbats debug spawn_swarm [count]` | Gamemasters (Level 2) | Spawns a coordinated test flock of bats (default: 5, up to 30). |

---

## ⚙️ Configuration (Native GameRules)

> [!IMPORTANT]
> **💡 Config vs. In-Game GameRules:** The global configuration file (`config/better-bats.json`) only defines default values for newly created worlds. In existing worlds, change settings in-game via the **Edit Game Rules** UI screen, the `/betterbats set` command, or the `/gamerule` command.

| GameRule Name | Type | Default | Valid Range | Description |
| :--- | :---: | :---: | :---: | :--- |
| `better-bats:bat_swarm_size` | `Integer` | `5` | `0` to `50` | Maximum size of bat flocks. Set to 0 to disable flocking entirely. |
| `better-bats:bat_guano_threshold` | `Integer` | `12000` | `100` to `72000` | Ticks required for a resting bat to produce guano (~10 minutes). Lower is faster. |
| `better-bats:bat_pest_control` | `Boolean` | `true` | `true / false` | Toggles dive-bomb hunting of silverfish and endermites. |
| `better-bats:bat_alignment` | `Integer` | `5` | `0` to `100` | How strongly bats align their flight direction with the swarm. |
| `better-bats:bat_cohesion` | `Integer` | `5` | `0` to `100` | How strongly bats are pulled towards the center of the swarm. |
| `better-bats:bat_separation` | `Integer` | `10` | `0` to `100` | How strongly bats avoid colliding with neighboring flock mates. |
| `better-bats:bat_spawn_weight` | `Integer` | `30` | `0` to `100` | Spawn weight of bats in dark caverns and surface at night (Vanilla is 10). |
| `better-bats:bat_drop_guano_item` | `Boolean` | `false` | `true / false` | When true, roosting bats drop physical Bone Meal item entities. |
| `better-bats:debug_mode` | `Boolean` | `false` | `true / false` | Enables detailed diagnostic logging for bat AI. Resets to false on restart. |

---

## 📖 In-Depth How-To & Operational Playbook

### 1. Designing Natural Bat Roosts in Crop Greenhouses
- Build a subterranean greenhouse with high ceilings (8 to 15 blocks tall).
- Hang **Pointed Dripstone**, **Iron Chains**, or **Lanterns** from the ceiling above your farmland rows.
- Bats wandering into the greenhouse will naturally choose these perches to sleep. While resting, they fertilize crops up to 20 blocks below every ~10 minutes.

### 2. Building an Automated Renewable Bone Meal Tower
1. Enable physical guano item drops via `/bb set bat_drop_guano_item true`.
2. Construct an enclosed dark chamber with chains hanging from the ceiling.
3. Place a floor of hoppers directly beneath the roosting points (within 20 blocks down).
4. When bats rest over the hoppers, their guano drops as physical Bone Meal items, funneled directly into storage chests without killing any mobs!

### 3. Silverfish & Stronghold Infestation Defense
- When raiding Mountain biomes or exploring underground Strongholds, bats act as an early-warning defense system.
- If stone monster eggs break, nearby bats will immediately dive-bomb the silverfish, absorbing aggro and taking them out before they multiply.

### 4. Directing Bat Flocks Away with Felines
- If bat swarms congregate in an area where you want quiet, place a tamed **Cat** or **Ocelot** in the room.
- Bats will detect the feline predator within 10 blocks, break their roosts, and steer away toward darker, predator-free corridors.

### 5. Nighttime Porch Lighting & Phototaxis
- Place hanging lanterns along pathways or exterior porches. At night, surface bats will fly in curved arcs toward the lantern, circling gracefully for 10-30 seconds before peeling away back into the night sky.

---

## 🧩 Recommended Sister Mods

If you enjoy **Better Bats**, these companion mods from the **Vanilla Outsider Collection** plug in seamlessly:

* 🌾 [**Agrarian Reform**](https://modrinth.com/mod/vanilla-outsider-agrarian-reform): Offline crop growth simulation, smart trample protection, and deep irrigation that reward bat guano fertilization.
* 🛏️ [**True Sleep**](https://modrinth.com/mod/vanilla-outsider-true-sleep): Real-time night acceleration where bat swarms awaken and hunt while you rest.
* 🐕 [**Better Dogs**](https://modrinth.com/mod/better-dogs): Deep canine companion behaviors, guard post patrol modes, genetic traits, and vehicle riding.

> 🌟 *Explore the full [**Vanilla Outsider Collection**](https://modrinth.com/collection/vanilla-outsider) for more vanilla enhancements.*

---

## ☕ Support

If you enjoy the **Vanilla Outsider Collection**, consider fueling future development!

<p align="center">
  <a href="https://ko-fi.com/dasikigaijin/tip"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
  <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
  <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

> [!NOTE]
> **🇮🇩 Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

> [!TIP]
> **Dedicated Server Hosting Partner:**
> Looking for a reliable server to play with friends? Check out **BisectHosting** for 1-click modpack installations, automated backups, and 24/7 dedicated customer support.

---

## 📜 Credits & Modpack Permissions

| Property | Information |
| :--- | :--- |
| **Creator / Author** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider Collection |
| **License** | [GNU General Public License v3.0 (GPLv3)](https://www.gnu.org/licenses/gpl-3.0.html) |
| **Source Code** | [GitHub - Rifaditya/Better-Bats-26.2](https://github.com/Rifaditya/Better-Bats-26.2) |
| **Issue Tracker** | [GitHub Issues](https://github.com/Rifaditya/Better-Bats-26.2/issues) |
| **Documentation / Wiki** | [GitHub Wiki](https://github.com/Rifaditya/Better-Bats-26.2/wiki) |

> [!IMPORTANT]
> **📦 Modpack Permissions & Distribution:**<br>
> You are fully welcome to include this mod in any modpack on any platform! However, the mod file must be downloaded directly through official distribution channels (**Modrinth** or **CurseForge**). Re-uploading, mirroring, or redistributing the original mod JAR to third-party mirror sites, scraper portals, or unauthorized launchers is strictly prohibited.
> <br><br>
> **⚖️ License & Fork Guidelines (No Zero-Change Re-uploads):**<br>
> This project is open-source under the **GNU GPLv3**. You are fully encouraged to inspect the code, learn from it, and fork the repository to create genuine modifications, substantial feature expansions, or community ports—provided your project remains open-source under GPLv3 with proper attribution.<br>
> **However, straight 1:1 re-uploads, clone forks with no meaningful functional changes, or re-publishing identical builds under different project names (e.g. to farm downloads or rewards) are strictly forbidden.**

---

<p align="center">
  <strong>Made with ❤️ for the Minecraft community</strong><br>
  <em>Part of the Vanilla Outsider Collection</em>
</p>
