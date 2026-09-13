# Better Bats User Guide

Better Bats overhauls the ambient Bat into a dynamic part of the cave ecosystem.

## ✨ Features

### 🦇 Hive Mind (Murmuration)
Bats no longer fly erratically alone. They form cohesive, leaderless swarms (murmurations) that navigate caves and the surface together using true BOIDs flocking math. Each bat behaves independently, adjusting its flight to stick close to, align with, and avoid colliding with its immediate neighbors.

### 💩 Guano Fertility
Bats roosting on cave ceilings or overhangs accumulate guano. Every 10 minutes, they will drop guano onto the ground below. If there is farmland with crops directly beneath the roost, the crops will receive a growth boost!

### 💡 Phototaxis (Light Attraction)
Bats are attracted to bright artificial light (lanterns, torches). You may see them circling lights to hunt for insects.

### 🗡️ Pest Control
Bats are natural predators of small pests. They will aggressively dive-bomb and eliminate **Silverfish** and **Endermites** in their vicinity.

### 📢 Acoustic Panic
Bats are sensitive to vibrations. Loud noises like explosions or mining nearby will cause resting bats to wake up and scatter in a panic.

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

## 📋 Compatibility
- **Singleplayer**: Supported
- **Multiplayer**: Supported (Server-side required)
- **Modpacks**: Free to include in modpacks on the same platform (Modrinth/CurseForge).
