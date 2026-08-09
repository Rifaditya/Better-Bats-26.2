# 🤖 Behavior Profiles, Goal Flags & State Accessors

Overview of the Goal Priority Matrix, `BatStateAccessor` interface, and flag management controlling bat state transitions.

---

## 📊 AI Goal Priority Matrix

All goals set move flags (`this.setFlags(EnumSet.of(Goal.Flag.MOVE))`) to prevent movement conflicts:

| Priority Level | Goal Class | Activation Condition | Behavior Summary |
|---|---|---|---|
| **Priority 1** | `BatPanicGoal` | Vibration / Explosion event received (`panicTicks > 0`) | Disperses from group, flies rapidly in opposite vector of noise source for 5 seconds ($100\text{ ticks}$). |
| **Priority 2** | `BatSleepGoal` | Daytime bright outside OR raining exposed to sky | Searches 16-block radius for dark roost surface (`Sky=0`, `Block<=7`), prioritizing resting bat clusters. |
| **Priority 4** | `BatHuntLightGoal` | Nighttime, Block Light > 8 detected within 10 blocks | Approaches with curved banking, orbits light center for 10–30s with vertical sine bobbing and `CRIT` particles. |
| **Priority 5** | `BatDiveBombGoal` | `bat_pest_control == true`, Silverfish/Endermite within 8 blocks | Dives towards pest at $0.3\text{ b/t}$, dealing $10.0 \times \text{attack\_damage\_trait}$ damage upon impact. |

---

## 🕹️ `BatStateAccessor` Interface Protocol

`BatStateAccessor` provides lightweight duck-typing access to internal state flags on vanilla `Bat` entities without polluting vanilla entity classes:

```java
public interface BatStateAccessor {
    boolean betterbats$isGoalActive();
    void betterbats$setGoalActive(boolean active);
    void betterbats$resetGuanoTicks();
    void betterbats$panic(Vec3 source);
    boolean betterbats$isPanicked();
    int betterbats$getPanicTicks();
    void betterbats$setPanicTicks(int ticks);
    Vec3 betterbats$getPanicSource();
}
```

- When `betterbats$isGoalActive()` returns `true`, ambient `BatFlightHelper` BOIDs flocking and organic wandering are temporarily suspended, allowing the active custom goal full control over the entity's delta movement.

---

## 🔗 Related Pages
- [[Mixin Reference & Injection Hooks|Mixin-Reference-and-Hooks]]
- [[Architecture & Package Layout|Architecture-and-Package-Layout]]
