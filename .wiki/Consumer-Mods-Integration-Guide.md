# 🔌 Consumer Mods Integration Guide

Guide for third-party developer integration with Better Bats and DasikLibrary APIs.

---

## 📦 Gradle Dependency Setup

Add DasikLibrary to your third-party mod `build.gradle` and `fabric.mod.json`:

```groovy
dependencies {
    modImplementation "net.dasik.social:dasik-library:1.8.9+26.2"
}
```

```json
"depends": {
    "dasik-library": ">=1.8.2"
}
```

---

## 🧬 Reading Bat Genetics & Trait Values

Third-party mods can read bat genetics using `DasikAnimalGeneticsAPI`:

```java
import net.dasik.social.api.genetics.DasikAnimalGeneticsAPI;
import net.minecraft.world.entity.ambient.Bat;

public class BatInspector {
    public static void inspectBat(Bat bat) {
        // Read scale trait multiplier (Default baseline: 1.0f)
        float scale = DasikAnimalGeneticsAPI.getTrait(bat, "scale", 1.0f);
        
        // Read movement speed trait (Default baseline: 0.0f)
        float speedBonus = DasikAnimalGeneticsAPI.getTrait(bat, "movement_speed", 0.0f);
        
        // Read attack damage multiplier trait (Default baseline: 2.0f)
        float attackDamage = DasikAnimalGeneticsAPI.getTrait(bat, "attack_damage", 2.0f);
        
        System.out.println("Bat scale: " + scale + ", Damage multiplier: " + attackDamage);
    }
}
```

---

## 🔊 Triggering Panic State Programmatically

To trigger panic flight programmatically on any bat entity:

```java
import net.vanillaoutsider.betterbats.BatStateAccessor;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.phys.Vec3;

public class BatSoundEmitter {
    public static void scareBat(Bat bat, Vec3 soundOrigin) {
        if (bat instanceof BatStateAccessor accessor) {
            accessor.betterbats$panic(soundOrigin);
        }
    }
}
```

---

## ⚙️ Querying Live Dynamic GameRules

Read active Better Bats GameRules from any server world instance using `DynamicGameRuleManager`:

```java
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterbats.BetterBatsFabric;
import net.minecraft.world.level.Level;

public class BatRuleChecker {
    public static void checkRules(Level level) {
        int maxSwarm = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_SWARM_SIZE);
        boolean pestControl = DynamicGameRuleManager.getBoolean(level, BetterBatsFabric.BAT_PEST_CONTROL);
        int guanoTimer = DynamicGameRuleManager.getInt(level, BetterBatsFabric.BAT_GUANO_THRESHOLD);
    }
}
```

---

## 🔗 Related Pages
- [[Animal Genetics & Inherited Chiroptera Traits|Animal-Genetics-and-Trait-Inheritance]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Acoustic Echolocation, Vibration Sensing & Panic|Acoustic-Echolocation-and-Panic]]
