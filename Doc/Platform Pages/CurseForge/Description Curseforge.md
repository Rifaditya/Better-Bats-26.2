<div align="center">

![Better Bats Banner](Doc/Assets/banner.png)

</div>
<p align="center">
    <a href="https://www.curseforge.com/minecraft/mc-mods/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Java">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.1+-brightgreen?style=for-the-badge" alt="Minecraft 26.1+">
</p>

# 🦇 Better Bats: The "Acoustic & Sleep" Update (Build 4)

**No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

**The Vanilla Problem:** In vanilla Minecraft, bats are purely cosmetic, chaotic, ambient entities that offer no interactive value to the player's world. They fly aimlessly, spawn in complete darkness, and drop absolutely nothing, rendering them essentially useless.

**Better Bats** changes this foundation. It integrates bats into the ecosystem as active, social, and symbiotic neighbors. They group into coordinated swarms, seek shelter from the sun, fertilize crops, hunt insect pests, circle lanterns, and panic dynamically when loud events disrupt their sleep.

---

## 🎥 Showcase Video

<!-- For CurseForge / GitHub (Markdown Image Link) -->
[![Mod Showcase Video](https://img.youtube.com/vi/VIDEO_ID/maxresdefault.jpg)](https://youtu.be/VIDEO_ID)

*Click the player above to watch the mod showcase in action!*

---

## ✨ Features

### 🌪️ Hive Mind (Social Swarms / Murmuration)

Bats no longer fly in erratic, individual patterns. They organize into coordinated, leaderless swarms (murmurations) using a dynamic Boids flocking strategy. Each bat behaves independently, adjusting its flight to stick close to, align with, and avoid colliding with its immediate neighbors. During dusk and dawn, the swarm funnels tightly together, creating breathtaking cinematic streams as they exit and return to their caves!

<blockquote>
<strong>Boids Steering</strong>: Swarm flight is powered by a dynamic Boids murmuration algorithm.<br>
Flocking Range: <strong>12 blocks</strong> — Cohesion, alignment, and separation steer bats naturally without any designated leader.
</blockquote>

### 💩 Guano Fertility (Natural Growth)

While roosting upside down in the dark, bats slowly accumulate guano. Every 10 minutes, they drop guano to fertilize soil and crops far below.

<blockquote>
<strong>Farmland Enrichment</strong>: If a bat roosts above crops, it scans up to <strong>20 blocks down</strong> to apply a bone meal growth tick.
</blockquote>

### 💡 Phototaxis (Lantern Hunting)

During the night, bats are dynamically attracted to bright artificial light sources. They break from their swarms to circle lanterns and torches using smooth curved approaches and vertical bobbing, simulating the hunting of insects attracted to the glow. Rather than getting stuck forever, they exhibit a natural "Moth Effect"—circling for a brief period before getting bored and hopping to a new light source!

<blockquote>
<strong>Insect Feeding</strong>: Bats will dynamically track light sources with a brightness level <strong>&gt;8</strong> within a 10-block radius, emitting <code>crit</code> particles to represent feeding before flying off to find their next meal.
</blockquote>

### 🛡️ Pest Control (Symbiotic Defense)

Bats are natural allies in pest management. They will aggressively dive-bomb and one-shot crawling pests that enter their vicinity.

<blockquote>
<strong>Predatory Prey</strong>: Bats target <strong>Silverfish</strong> and <strong>Endermites</strong> in a <strong>10-block radius</strong> and instantly defeat them.
</blockquote>

### 📢 Acoustic Panic

Caves are quiet for a reason. Loud noises (sprinting players, block mining, explosions) nearby will wake sleeping bat swarms instantly, sending them into a frantic, high-speed scatter and resetting their guano timers.

<blockquote>
<strong>Sleep Interruption</strong>: Noise events within <strong>16 blocks</strong> trigger immediate panic flight and clear the bat's flocking state.
</blockquote>

### ⚖️ Multiplayer

Better Bats is fully compatible with multiplayer environments. The flocking and guano algorithms run entirely server-side, ensuring vanilla clients can connect without issues.

---

## 🎭 Behavior Scenarios

Here is how bats naturally react in various environmental and player-driven scenarios:

*   **Daytime in Caves**: Bats steer towards dark areas (lowest sky light) to find deep cave pockets. If they find a suitable ceiling, they will roost and start producing guano.
*   **Daylight Avoidance**: If bats get near a cave exit during the day, the sky light steering force will guide them back into the dark interior.
*   **Nighttime Exploration**: At night, their preference reverses, steering them towards the highest sky light. Roosting bats will wake up and fly out of cave mouths to swarm under the open night sky.
*   **Murmuration Merging**: When solitary bats fly near each other, they seamlessly merge into a cohesive murmuring flock due to cohesion and alignment forces.
*   **Water & Ground Avoidance**: Bats constantly scan below and apply an upward steering force to avoid falling, crashing, or drowning in water.
*   **Acoustic Panic**: Loud noises (mining, player sprinting, explosions) wake resting bats, forcing them to scatter in panic before eventually regrouping.
*   **Predation (Silverfish/Endermites)**: Bats will swoop down from their flock to eliminate nearby Silverfish and Endermites, then rejoin the murmuration.
*   **Artificial Light Attraction**: At night, bats are drawn to lanterns and torches (brightness > 12) to hunt bugs, circling the light source before returning to the flock.

---

## ⚙️ Config

The mod works out of the box with zero setup.

* **Global Template**: `config/better-bats.json` (Sets defaults for new worlds)
* **In-Game**: Use `/gamerule [rule_name]` for core settings:
  * `bat_swarm_size`: Max bats allowed in a single flock. (Default: 5)
  * `bat_guano_threshold`: Ticks required for a roosting bat to drop guano. (Default: 12000)
  * `bat_pest_control`: Enables/disables hunting of silverfish and endermites. (Default: true)
  * `bat_alignment`: Strength of matching velocity direction with the swarm (0-100). (Default: 5)
  * `bat_cohesion`: Strength of pull towards the swarm's center of mass (0-100). (Default: 5)
  * `bat_separation`: Strength of collision avoidance from neighboring bats (0-100). (Default: 10)
  * `bat_spawn_weight`: Spawn weight of bats relative to vanilla (0-100). (Default: 10)

<blockquote>
<strong>Recommended Mod</strong>: Since this mod generates 6+ GameRules, it is highly recommended to use <a href="https://modrinth.com/mod/collapsible-gamerules">Collapsible Game Rules</a> for a cleaner UI.
</blockquote>

---

## 🧩 Suggested Mods

For the best experience, we recommend installing:
* **[Collapsible Game Rules](https://modrinth.com/mod/collapsible-gamerules)**: Prevents the GameRules menu from becoming cluttered.

---

## 📦 Installation & Environment

### 🖥️ Environment Support
* [ ] **Client-side only**: All functionality is done client-side and is compatible with vanilla servers.
* [x] **Server-side only**: All functionality is done server-side and is compatible with vanilla clients.
  * [x] Works in singleplayer too
  * [ ] Dedicated server only
* [ ] **Client and server**: Has some functionality on both the client and server.

### 📥 Install Instructions
1. Install **[Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)**.
2. Install **[DasikLibrary](https://www.curseforge.com/minecraft/mc-mods/dasik-library)**.
3. Download `better-bats-1.1.1+build.4.jar` and place it in your `mods` folder.

---

## 🧩 Compatibility

| Feature | Fabric (26.1+) |
| :--- | :---: |
| Singleplayer | ✅ |
| Multiplayer (LAN/Server) | ✅ |
| **VO: Better Dogs** | ✅ |
| Empty Dimensions | ✅ |

---

## ☕ Support

If you enjoy **Better Bats** and the **Vanilla Outsider** philosophy, consider fueling the next update with a coffee!

<a href="https://ko-fi.com/dasikigaijin/tip"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
<a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
<a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>

<blockquote>
<strong>Indonesian Users:</strong> SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!
</blockquote>

---

## 📜 Credits

| Role | Author |
| :--- | :--- |
| **Creator** | Dasik (Rifaditya) |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

---

<blockquote>
This mod is part of the <strong>Vanilla Outsider</strong> collection. You are free to use it in modpacks, videos, and servers.
<br><br>
<strong>Modpack Permissions:</strong> You are free to include this mod in modpacks, <strong>provided the modpack is hosted on the same platform</strong> (e.g. CurseForge).
<br><br>
<strong>Cross-platform distribution is not permitted.</strong> If you download this mod from CurseForge, your modpack must also be published on CurseForge.
</blockquote>

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
