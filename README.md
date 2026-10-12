# Better Bats

<p align="center">
    <a href="https://modrinth.com/mod/better-bats"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-8A2BE2?style=for-the-badge" alt="Requires Dasik Library"></a>
    <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Java">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.3-brightgreen?style=for-the-badge" alt="Minecraft 26.3">
</p>

> **"From useless cave annoyance to essential ecological companions."**

**1 Jar 1 Version Policy:** I build **1 dedicated JAR for each Minecraft version** (e.g. MC 26.1, MC 26.2, MC 26.3). Please download the exact build that matches your Minecraft installation.

**The Vanilla Problem:** In vanilla Minecraft, bats are purely cosmetic, chaotic ambient entities that offer no interactive value to the player's world. They fly aimlessly into lava pits, spawn in pitch darkness, and drop absolutely nothing, rendering them essentially useless.

**Better Bats** changes this foundation. It integrates bats into the ecosystem as active, social, and symbiotic cave fauna. They group into coordinated flocks, roost upside down on diverse fixtures, fertilize crops, hunt insect pests, circle lanterns at night, and panic dynamically when loud events disrupt their sleep.

Part of the **Vanilla Outsider Collection** — mods that refine vanilla mechanics with modern engineering standards.

---

## Key Features

### Murmuration: Boids Aerial Flocking
Bats no longer fly in erratic, individual patterns. They organize into cohesive swarms using a 3D Boids flocking strategy (Cohesion, Alignment, and Separation).
- **Flocking Dynamics**: Bats dynamically align movement vectors with nearby flock mates within 16 blocks.
- **Configurable Swarm Size**: Flocks cleanly cap out at `bat_swarm_size` (default: 5 members).

### Guano Fertility & Renewable Growth
Forget wasting bone meal. While roosting upside down in the dark, bats slowly accumulate guano.
- **Farmland Enrichment**: When roosting over crops, the bat scans up to 20 blocks down and applies a growth spurt every ~10 minutes (`bat_guano_threshold`).
- **Physical Bone Meal Drops**: Enable `bat_drop_guano_item true` so roosting bats drop physical Bone Meal items over hoppers.

### Phototaxis: Attraction to Light
During the night, bats are naturally attracted to bright artificial light sources.
- **Lantern Circling**: Bats break from their swarms to investigate light sources with brightness > 8 (torches, lanterns, campfires), circling with smooth banking flight.
- **Ambient Feeding**: After circling and feeding on ambient insects around the light, bats disperse and resume normal flocking.

### Pest Control: Symbiotic Defense
Bats are natural allies in pest management.
- **Aggressive Dive-Bombing**: Bats actively detect **Silverfish** and **Endermites** within a 10-block radius and dive-bomb to exterminate them.
- **Genetic Damage**: Pest damage scales organically from 1.0 to 4.0 based on genetic traits.

### Acoustic Panic & Predator Avoidance
Caves are quiet for a reason. Sensitive ears keep bats constantly vigilant:
- **Noise Disruption**: Loud disturbances (sprinting players, mining blocks, explosions) within 16 blocks wake sleeping bat swarms instantly, triggering frantic scatter flight.
- **Predator Avoidance**: Bats detect cats, ocelots, and phantoms within 10 blocks, prompting an immediate directional flee response.

### Universal Roosting Anchors
Bats can now hang upside down from authentic environmental perches:
- Pointed Dripstone stalactites
- Iron Chains and hanging lanterns
- Tree foliage, fences, stone walls, slabs, and stairs

### Multiplayer & Server-Side Compatibility
Better Bats runs completely server-side. Vanilla clients can connect to modded servers without needing to install anything locally.

---

## Configuration & GameRules

The mod works out of the box with zero setup.

* **Global Template**: `config/better-bats.json` (Sets defaults for newly created worlds)
* **In-Game**: Use `/gamerule better-bats:[rule_name]` or `/betterbats set <rule> <val>`:
  * `bat_swarm_size`: Maximum bats allowed in a single flock (default: 5).
  * `bat_guano_threshold`: Ticks required for a roosting bat to drop guano (default: 12000).
  * `bat_pest_control`: Toggles dive-bomb hunting of silverfish and endermites (default: true).
  * `bat_alignment`: Strength of matching velocity direction with the swarm (default: 5).
  * `bat_cohesion`: Strength of pull towards the swarm's center (default: 5).
  * `bat_separation`: Strength of collision avoidance from neighboring bats (default: 10).
  * `bat_spawn_weight`: Spawn weight of bats in dark caverns (default: 30).
  * `bat_drop_guano_item`: When true, roosting bats drop physical Bone Meal items (default: false).

---

## Recommended Mods

* **[Collapsible Game Rules](https://modrinth.com/mod/collapsible-gamerules)**: Groups custom GameRules into clean collapsible categories in the Game Rules UI.

---

## Installation

1. Install **[Fabric API](https://modrinth.com/mod/fabric-api)**.
2. Install **[Dasik Library](https://modrinth.com/mod/dasik-library)**.
3. Download the matching `better-bats` JAR for your Minecraft version and place it in your `mods` folder.

---

## Compatibility

| Environment / Feature | Support Status |
| :--- | :---: |
| Singleplayer | Supported |
| Dedicated Server (Vanilla Clients) | Supported |
| LAN Multiplayer | Supported |
| Empty / Custom Dimensions | Supported |

---

## Support

If you enjoy **Better Bats** and the **Vanilla Outsider Collection**, consider supporting future development:

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)
[![Saweria](https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge)](https://saweria.co/DasikIgaijinn)

> [!NOTE]
> **Indonesian Users:** SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## Credits & Modpack Permissions

| Property | Information |
| :--- | :--- |
| **Creator / Author** | **Dasik** (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | [GNU General Public License v3.0 (GPLv3)](https://www.gnu.org/licenses/gpl-3.0.html) |
| **Source Code** | [GitHub - Rifaditya/Better-Bats-26.3](https://github.com/Rifaditya/Better-Bats-26.3) |
| **Issue Tracker** | [GitHub Issues](https://github.com/Rifaditya/Better-Bats-26.3/issues) |

You are welcome to include this mod in any modpack on Modrinth or CurseForge. Re-uploading bare JAR files to third-party mirror sites without attribution is prohibited.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
