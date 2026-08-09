# 🦇 3D BOIDs Flocking, Altitude Caps & Steering

| Parameter | Default Value | GameRule Key | Description |
|---|---|---|---|
| **Alignment Weight ($W_a$)** | `5` (Scale 0.05) | `better-bats:bat_alignment` | Velocity vector matching weight ($0 - 100$) |
| **Cohesion Weight ($W_c$)** | `5` (Scale 0.05) | `better-bats:bat_cohesion` | Attraction to flock center of mass ($0 - 100$) |
| **Separation Weight ($W_s$)** | `10` (Scale 0.10) | `better-bats:bat_separation` | Collision avoidance pushing force ($0 - 100$) |
| **Flock Size Limit** | `5` | `better-bats:bat_swarm_size` | Maximum members in a single flock |
| **Hard Altitude Cap** | `30 blocks` | Surface Heightmap | Maximum blocks above world surface (`WORLD_SURFACE`) |
| **Night Comfort Zone** | `5` to `20` blocks | Surface Heightmap | Ideal surface elevation range at night |
| **Min / Max Flight Speed** | `0.15` / `0.35` | Internal | Velocity magnitude limits (blocks/tick) |

---

## 🧮 3D BOIDs Mathematical Formulation

Calculated inside `BatFlightHelper.applyFlightForces(Bat bat)` for all active, non-resting bats:

### 1. Cohesion (Centering Force)
Draws individual bats toward the average spatial position ($\bar{\mathbf{P}}$) of neighboring bats within a 12-block radius:
$$\bar{\mathbf{P}} = \frac{1}{N} \sum_{i=1}^{N} \mathbf{P}_i, \quad \mathbf{F}_{\text{cohesion}} = (\bar{\mathbf{P}} - \mathbf{P}_{\text{self}}) \cdot W_c$$

### 2. Alignment (Velocity Matching)
Aligns the bat's flight heading with the average velocity vector ($\bar{\mathbf{V}}$) of neighbors:
$$\bar{\mathbf{V}} = \frac{1}{N} \sum_{i=1}^{N} \mathbf{V}_i, \quad \mathbf{F}_{\text{alignment}} = \bar{\mathbf{V}}_{\text{norm}} \cdot W_a$$

### 3. Separation (Collision Avoidance)
Pushes bats away from neighbors closer than 4 blocks ($d < 2.0\text{ blocks}$):
$$\mathbf{F}_{\text{separation}} = \left( \frac{1}{M} \sum_{k=1}^{M} \frac{\mathbf{P}_{\text{self}} - \mathbf{P}_k}{\|\mathbf{P}_{\text{self}} - \mathbf{P}_k\|} \cdot \frac{1}{\|\mathbf{P}_{\text{self}} - \mathbf{P}_k\|} \right) \cdot W_s$$

---

## 🌅 Twilight Streaming & Funneling

During twilight hours (Dusk: $12000 - 14000\text{ ticks}$; Dawn: $22000 - 24000\text{ ticks}$), bats exposed to open sky modify their steering multipliers to form tight, organized aerial streams entering or exiting caves:
- **Alignment Weight**: Multiplied by $\times 2.5$
- **Cohesion Weight**: Multiplied by $\times 2.5$
- **Separation Weight**: Reduced by $\times 0.4$

---

## 🏞️ Altitude Cap & Environmental Forces

```
┌──────────────────────────────────────────────────────────┐  Max Altitude Cap: Surface + 30
│  Downward proportional force applied: -min(0.25, dy*0.05)│
├──────────────────────────────────────────────────────────┤  Night Comfort Zone: Surface + 20
│  Parabolic downward steering                             │
│                                                          │
│  Optimal Flight Buffer                                   │
│                                                          │
│  Parabolic upward steering                               │
├──────────────────────────────────────────────────────────┤  Night Comfort Zone: Surface + 5
│  Ground Avoidance: Upward force = (6 - d) * 0.035       │
└──────────────────────────────────────────────────────────┘  Terrain / Fluid Surface
```

1. **Ground & Water Avoidance**: Scans 5 blocks downward. If solid terrain or fluid is detected at distance $d$, an upward velocity force of $(6 - d) \times 0.035$ is added.
2. **Ceiling Avoidance**: Scans 3 blocks upward. If a solid ceiling is detected at distance $d$, a downward force of $(4 - d) \times -0.035$ is added.
3. **Hard Altitude Cap**: If a bat exceeds $\text{Surface Y} + 30$, a proportional force scaling up to $-0.25\text{ blocks/tick}$ forces it downwards.

---

## 🐱 Predator Avoidance

Bats scan a 10-block radius for predatory entities (Cats, Ocelots, Phantoms). If a predator is detected, bats add a flee direction vector scaled by $+0.25$ away from the nearest predator, overriding non-essential goals.

---

## 🔗 Related Pages
- [[Bat Ecology, Photophobia & Daytime Roosting|Bat-Ecology-and-Photophobia]]
- [[Phototaxis & Nighttime Light Orbiting|Phototaxis-and-Light-Orbiting]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
