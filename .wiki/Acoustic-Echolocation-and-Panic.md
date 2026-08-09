# 🔊 Acoustic Echolocation, Vibration Sensing & Panic

| Parameter | Specification Details |
|---|---|
| **Vibration Listener Mixin** | `net.vanillaoutsider.betterbats.mixin.GameEventDispatcherMixin` |
| **Panic AI Goal Class** | `net.vanillaoutsider.betterbats.ai.BatPanicGoal` (Goal Priority 1) |
| **Vibration Trigger Events** | `GameEvent.EXPLODE`, `GameEvent.BLOCK_DESTROY`, Sprinting `GameEvent.STEP` |
| **Detection Radius** | `16 blocks` (AABB inflation) |
| **Panic Duration** | `100 ticks` ($5.0\text{ seconds}$) |
| **Panic Flight Response** | Disperses from group, flies rapidly in opposite vector ($0.15\text{ accel}$) |
| **Panic Audio** | `BAT_TAKEOFF` (Vol 0.8, Pitch 1.0) & `PHANTOM_FLAP` (Vol 0.5, Pitch 0.6) |
| **Echolocation Condition** | `Sky Light == 0` && `Block Light < 4` |
| **Echolocation Probability** | `1/90 ticks` (~4.5s average frequency) |
| **Echolocation Particles** | `SCULK_SOUL` (2 particles, speed 0.02) |
| **Echolocation Pitch** | `1.8f` to `2.1f` high-pitched click |

---

## ⚡ Acoustic Vibration Sensing & Panic

Bats are acute acoustic sensors. Through `GameEventDispatcherMixin`, any loud environmental vibration occurring within 16 blocks immediately forces resting bats to un-roost, resets their guano timer, and activates a 5-second panic state (`BatPanicGoal`):

```java
@Inject(method = "post", at = @At("HEAD"))
private void betterbats$onGameEvent(Holder<GameEvent> gameEvent, Vec3 position, GameEvent.Context context, CallbackInfo ci) {
    boolean isLoudStep = gameEvent == GameEvent.STEP && context.sourceEntity() != null && context.sourceEntity().isSprinting();
    if (gameEvent == GameEvent.EXPLODE || gameEvent == GameEvent.BLOCK_DESTROY || isLoudStep) {
        List<Bat> bats = this.level.getEntitiesOfClass(Bat.class, new AABB(position, position).inflate(16.0));
        for (Bat bat : bats) {
            if (bat.isResting()) bat.setResting(false);
            if (bat instanceof BatStateAccessor accessor) {
                accessor.betterbats$resetGuanoTicks();
                accessor.betterbats$panic(position);
            }
        }
    }
}
```

---

## 🦇 Pitch-Dark Cave Echolocation Clicks

When bats fly through pitch-dark underground caverns (`Sky Light == 0` and `Block Light < 4`), they emit high-frequency echolocation clicks to navigate in total darkness.

Every ~4.5 seconds on average, the bat emits a high-pitched click sound (`SoundEvents.BAT_AMBIENT`, pitch `1.8F - 2.1F`) and produces subtle `SCULK_SOUL` sonic pulse particles:

```java
if (level.getBrightness(LightLayer.SKY, pos) == 0 && level.getBrightness(LightLayer.BLOCK, pos) < 4) {
    if (self.getRandom().nextInt(90) == 0) {
        level.playSound(null, pos, SoundEvents.BAT_AMBIENT, SoundSource.NEUTRAL, 0.35F, 1.8F + self.getRandom().nextFloat() * 0.3F);
        level.sendParticles(ParticleTypes.SCULK_SOUL, self.getX(), self.getY() + 0.1, self.getZ(), 2, 0.08, 0.08, 0.08, 0.02);
    }
}
```

---

## 🔗 Related Pages
- [[Bat Ecology, Photophobia & Daytime Roosting|Bat-Ecology-and-Photophobia]]
- [[Sound Effects & Visual Particles Reference|Sound-Effects-and-Visual-Particles]]
- [[Behavior Profiles, Goal Flags & State Accessors|Behavior-Profiles-and-Conditions]]
