# 🏮 Phototaxis & Nighttime Light Orbiting

| Parameter | Specification Details |
|---|---|
| **AI Goal Class** | `net.vanillaoutsider.betterbats.ai.BatHuntLightGoal` |
| **Detection Light Level** | `Block Light > 8` (`LIGHT_DETECT_THRESHOLD`) |
| **Continue Light Level** | `Block Light > 6` (`LIGHT_CONTINUE_THRESHOLD`) |
| **Search Radius** | `10 blocks` (Deterministic `BlockPos.findClosestMatch`) |
| **Search Throttle** | Executes light search once every `30 ticks` (~1.5s) |
| **Orbit Duration** | `200` to `600 ticks` ($10 - 30\text{ seconds}$) |
| **Goal Cooldown** | `200` to `400 ticks` ($10 - 20\text{ seconds}$) |
| **Approach Mode** | Curved banking flight vector ($d > 2.5\text{ blocks}$) |
| **Orbiting Mode** | Tangential velocity vector with sine-wave vertical bobbing |
| **Visual Particles** | `CRIT` (10% tick probability during orbit) |

---

## 🌙 Phototaxis (The Moth Effect)

At night (`!level.isBrightOutside()`), bats display positive phototaxis, attracting them to artificial block light sources such as torches, lanterns, campfire embers, and glowstone. 

Rather than orbiting a single lantern forever, bats operate on a **Moth Effect** cycle: they seek a nearby lantern, bank towards it, orbit in a gentle bobbing motion for 10–30 seconds, and then trigger a 10–20 second cooldown before hopping to another nearby light source or returning to high flight.

```
                   ┌──────────────────────────┐
                   │    Nighttime Ambient     │
                   └────────────┬─────────────┘
                                │
                  Scans 10 blocks for Block Light > 8
                                │
                    Light Found (targetLight)?
                     ┌──────────┴──────────┐
                    YES                   NO
                     │                    │
              Distance > 2.5 blocks?    Wander / BOIDs
               ┌─────┴─────┐
              YES          NO
               │           │
       Curved Banking    Tangential Orbit
       Approach Flight   + Sine Bobbing (sin(t * 0.15) * 0.03)
                         + CRIT Particles
               │           │
               └─────┬─────┘
                     │
           Ticks >= maxCirclingTicks? (10-30s)
                     │
           Cooldown 10-20s -> Hop to next light
```

---

## 📐 Flight Vector Math

### 1. Curved Banking Approach ($d > 2.5\text{ blocks}$)
To prevent rigid straight-line movement, the approach velocity blends the direct direction vector with a perpendicular tangent vector:

$$\mathbf{V}_{\text{tangent}} = (-dz, \ dx) \cdot 0.5$$
$$\mathbf{V}_{\text{steer}} = \frac{(dx, dz) + \mathbf{V}_{\text{tangent}}}{\sqrt{dx^2 + dz^2}} \cdot 0.12$$

### 2. Tangential Orbiting & Vertical Sine Bobbing ($d \le 2.5\text{ blocks}$)
Once within 2.5 blocks of the light center, the bat calculates a perpendicular cross-product vector for smooth circular orbiting, combined with a vertical sine-wave oscillation:

$$\text{Y}_{\text{bobbing}} = \sin(\text{circlingTicks} \times 0.15) \times 0.03$$
$$\mathbf{V}_{\text{final}} = \left( (V_x + \text{Cross}_x \cdot S) \times 0.9, \ V_y \times 0.9 + \text{Y}_{\text{bobbing}}, \ (V_z + \text{Cross}_z \cdot S) \times 0.9 \right)$$

---

## 🔗 Related Pages
- [[3D BOIDs Flocking, Altitude Caps & Steering|3D-BOIDs-Flocking-and-Steering]]
- [[Sound Effects & Visual Particles Reference|Sound-Effects-and-Visual-Particles]]
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]]
