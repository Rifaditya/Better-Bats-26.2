# 🧬 Animal Genetics & Inherited Chiroptera Traits

| Trait Key | Target Attribute | Modifier Operation | Default Base | Min Bound | Max Bound | Mutation Rule |
|---|---|---|---|---|---|---|
| `scale` | `minecraft:generic.scale` | `ADD_VALUE` | `0.0f` | `0.75f` | `1.30f` | `uniform(0.75f, 1.30f)` |
| `movement_speed` | `minecraft:generic.movement_speed` | `ADD_MULTIPLIED_BASE` | `0.0f` | `-0.04f` | `0.08f` | `uniform(-0.04f, 0.08f)` |
| `attack_damage` | `minecraft:generic.attack_damage` | `ADD_VALUE` | `0.0f` | `1.0f` | `4.0f` | `uniform(1.0f, 4.0f)` |

---

## 🧬 Genetics Registration via DasikLibrary

Better Bats registers custom genetic traits for `EntityTypes.BAT` inside `BetterBatsFabric.java` using `EntityGeneticsRegistry.register`:

```java
EntityGeneticsRegistry.register(
    EntityTypes.BAT,
    new GeneticsConfig(
        Map.of(
            "scale", new TraitConfig(
                "scale", "minecraft:generic.scale", "ADD_VALUE", 0.0f, 1.0f, 0.75f, 1.30f
            ),
            "movement_speed", new TraitConfig(
                "movement_speed", "minecraft:generic.movement_speed", "ADD_MULTIPLIED_BASE", 0.0f, 1.0f, -0.04f, 0.08f
            ),
            "attack_damage", new TraitConfig(
                "attack_damage", "minecraft:generic.attack_damage", "ADD_VALUE", 0.0f, 1.0f, 1.0f, 4.0f
            )
        ),
        Map.of(
            "default", Map.of(
                "scale", new MutationRule("uniform", 0.75f, 1.30f),
                "movement_speed", new MutationRule("uniform", -0.04f, 0.08f),
                "attack_damage", new MutationRule("uniform", 1.0f, 4.0f)
            )
        )
    )
);
```

---

## 🦇 Trait Effects & Impact

1. **Scale / Wingspan (`scale`)**:
   - Directly scales the physical model rendering size and collision bounding box of the bat.
   - Range: $0.75\times$ (dwarf micro-bat) to $1.30\times$ (giant megabat / flying fox).
2. **Movement Speed (`movement_speed`)**:
   - Modifies base flight locomotion velocity by $-4\%$ up to $+8\%$.
3. **Attack Damage (`attack_damage`)**:
   - Multiplies predatory dive-bomb damage against Silverfish and Endermites:
     $$\text{Pest Damage} = 10.0 \times \text{attack\_damage\_trait}$$

---

## 🔄 Automatic Genetics Roll & Persistence

When a bat ticks for the first time without genetics (`!DasikAnimalGeneticsAPI.hasGenetics(self)`), `BatMixin.betterbats$onCustomServerAiStep` automatically rolls its genetic profile and applies attribute modifiers:

```java
if (!DasikAnimalGeneticsAPI.hasGenetics(self)) {
    DasikAnimalGeneticsAPI.rollStats(self, "better-bats:bat");
    GeneticsEngine.applyGeneticsModifiers(self);
}
```

Genetics data is stored safely in vanilla entity NBT compound tags (`EntityGenetics`), ensuring total uninstallation safety without corrupting world saves.

---

## 🔗 Related Pages
- [[Pest Control & Predatory Dive-Bomb Combat|Pest-Control-and-Predatory-Combat]]
- [[Consumer Mods Integration Guide|Consumer-Mods-Integration-Guide]]
