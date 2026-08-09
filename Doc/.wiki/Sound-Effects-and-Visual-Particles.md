# 🔊 Sound Effects & Visual Particles Reference

Exhaustive reference for all visual particle emissions and sound events utilized across Better Bats.

---

## 🎨 Particle Emission Matrix

| Particle Type | Trigger Condition | Quantity | Spread / Velocity | Description |
|---|---|---|---|---|
| `ParticleTypes.SCULK_SOUL` | Echolocation click in dark caves (`Sky=0`, `Block<4`) | `2` | `dx:0.08, dy:0.08, dz:0.08`, Speed `0.02` | Echolocation sonic pulse emitted from bat head |
| `ParticleTypes.HAPPY_VILLAGER` | Successful guano crop bonemeal growth | `5` | `dx:0.2, dy:0.2, dz:0.2`, Speed `0.05` | Green sparkles around fertilized crop |
| `ParticleTypes.MYCELIUM` | Guano accumulation on non-crop soil | `3` | `dx:0.2, dy:0.1, dz:0.2`, Speed `0.01` | Spore particles over soil block |
| `ParticleTypes.CRIT` | Nighttime phototaxis light orbiting (`BatHuntLightGoal`) | `3` | `dx:0.2, dy:0.2, dz:0.2`, Speed `0.05` | Critical trail particles emitted near light center |

---

## 🎵 Sound Events & World Events Matrix

| Sound / Event Key | Category / Source | Volume | Pitch | Trigger Context |
|---|---|---|---|---|
| `SoundEvents.BAT_AMBIENT` | Neutral | `0.35f` | `1.8f - 2.1f` | Pitch-dark cave echolocation click |
| `SoundEvents.BAT_AMBIENT` | Neutral | `1.0f` | `0.5f` | Predatory dive-bomb attack impact |
| `SoundEvents.BAT_TAKEOFF` | Neutral / Panic | `0.8f` | `1.0f` | Sudden vibration / sound panic trigger |
| `SoundEvents.PHANTOM_FLAP` | Neutral / Panic | `0.5f` | `0.6f` | Secondary rapid wing flap during panic flight |
| `levelEvent 1025` | World Level Event | N/A | N/A | Bat takeoff / un-roosting sound (if not silent) |
| `levelEvent 2005` | World Level Event | N/A | N/A | Crop bone meal fertilization particle & sound |

---

## 🔗 Related Pages
- [[Acoustic Echolocation, Vibration Sensing & Panic|Acoustic-Echolocation-and-Panic]]
- [[Guano Production & Farmland Crop Fertilization|Guano-Production-and-Crop-Fertilization]]
- [[Phototaxis & Nighttime Light Orbiting|Phototaxis-and-Light-Orbiting]]
