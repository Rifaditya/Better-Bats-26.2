# ⚔️ Pest Control & Predatory Dive-Bomb Combat

| Parameter | Specification Details |
|---|---|
| **AI Goal Class** | `net.vanillaoutsider.betterbats.ai.BatDiveBombGoal` |
| **GameRule Toggle** | `better-bats:bat_pest_control` (Default: `true`) |
| **Target Pest Entities** | `Silverfish` (`EntityTypes.SILVERFISH`), `Endermite` (`EntityTypes.ENDERMITE`) |
| **Detection Radius** | `8 blocks` (Bounding box inflation) |
| **Base Impact Damage** | `10.0` points ($5\text{ hearts}$) |
| **Genetics Damage Multiplier** | `attack_damage` trait ($1.0\times - 4.0\times$) |
| **Dive-Bomb Speed** | `0.3 blocks/tick` |
| **Audio Cue** | `minecraft:entity.bat.ambient` (Pitch `0.5f`) |

---

## 🦅 Predatory Hunting Mechanics

When enabled via the `better-bats:bat_pest_control` GameRule, bats act as ecological pest control agents, scanning their surroundings for crawling arthropod pests (Silverfish and Endermites).

```
                  ┌───────────────────────────────┐
                  │    Bat in Flight (Active)     │
                  └───────────────┬───────────────┘
                                  │
               GameRule bat_pest_control == true?
                                  │
                Scans 8 blocks for Silverfish/Endermite
                                  │
                       Pest Entity Found?
                         ┌────────┴────────┐
                        YES                NO
                         │                 │
                Clear Group Leader         Continue Ambient
                Set Goal Active = true     Flight / Flocking
                         │
                 Accelerate 0.3 b/t
                 Direct Dive-Bomb
                         │
                   Distance < 1.0?
                         │
           💥 Deal (10.0 * attack_damage_trait)
           🎵 Play BAT_AMBIENT sound (Pitch 0.5)
```

---

## 🧮 Damage Formula & Genetics Integration

The damage dealt to a pest upon impact is scaled dynamically by the bat's individual genetics:

$$\text{Damage} = 10.0 \times \text{attack\_damage\_trait}$$

- **Base Trait Value**: Default baseline is $2.0$.
- **Genetic Trait Range**: $1.0\times$ to $4.0\times$ (configured in `EntityGeneticsRegistry`).
- **Effective Damage Range**: $10.0$ to $40.0$ damage points (instantly slaying standard Silverfish with $8\text{ HP}$ or Endermites with $8\text{ HP}$).

```java
if (dist < 1.0) {
    float attackDamageTrait = DasikAnimalGeneticsAPI.getTrait(this.bat, "attack_damage", 2.0f);
    float damage = 10.0f * attackDamageTrait;
    this.targetPest.hurt(this.bat.damageSources().mobAttack(this.bat), damage);
    this.bat.playSound(SoundEvents.BAT_AMBIENT, 1.0f, 0.5f); 
    this.targetPest = null;
}
```

---

## 🔗 Related Pages
- [[Animal Genetics & Inherited Chiroptera Traits|Animal-Genetics-and-Trait-Inheritance]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]]
