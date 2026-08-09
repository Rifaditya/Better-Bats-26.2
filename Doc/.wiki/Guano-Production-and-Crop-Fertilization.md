# 🌱 Guano Production & Farmland Crop Fertilization

| Parameter | Specification Details |
|---|---|
| **NBT Persistence Tag** | `betterbats:guano_ticks` (Integer) |
| **Default Timer Threshold** | `12000 ticks` ($600\text{ seconds} / 10\text{ minutes}$) |
| **Speed GameRule Key** | `better-bats:bat_guano_threshold` |
| **Item Drop GameRule Key** | `better-bats:bat_drop_guano_item` (Default: `false`) |
| **Crop Target Interface** | `net.minecraft.world.level.block.BonemealableBlock` |
| **Farmland Search Depth** | Up to `20 blocks` vertically below roost position |
| **Fertilizer Particles** | `HAPPY_VILLAGER` (Success) / `MYCELIUM` (Soil drop) |
| **Item Entity Drop** | `minecraft:bone_meal` (When item drop enabled) |

---

## ⌛ Guano Accumulation Mechanics

When a bat rests (`isResting() == true`), its internal NBT guano counter (`betterbats:guano_ticks`) increments once per tick inside `BatMixin.betterbats$onTick`:

```
                  ┌────────────────────────┐
                  │    Bat is Resting      │
                  └───────────┬────────────┘
                              │
                    guano_ticks++
                              │
                 guano_ticks >= threshold?
                     ┌────────┴────────┐
                    YES                NO
                     │                 │
           Scan up to 20 blocks        Wait for next tick
           down for farmland/crops
                     │
         ┌───────────┴───────────┐
      Crop Found?             Soil / Block Only?
         │                       │
 🌾 Bonemeal Crop         ✨ Spawn Mycelium particles
 ✨ Spawn Happy Villager   📦 Drop Bone Meal Item
    particles                 (If bat_drop_guano_item is true)
```

$$\text{Guano Timer Progress} = \frac{\text{guano\_ticks}}{\text{bat\_guano\_threshold}} \times 100\%$$

---

## 🌾 Farmland Crop Bonemealing Logic

The bat scans vertically downwards up to 20 blocks below its roosting position:
1. **Farmland Crop Target**: If a block below is `FarmlandBlock` and the block directly above it implements `BonemealableBlock`:
   - Checks `crop.isValidBonemealTarget(level, cropPos, cropState)`.
   - Executes `crop.performBonemeal(level, level.getRandom(), cropPos, cropState)`.
   - Plays bone meal sound (`levelEvent(2005, cropPos, 0)`).
   - Spawns 5 `HAPPY_VILLAGER` particles around the crop.
2. **Soil / Physical Item Drop**: If no bonemealable crop is present:
   - Spawns 3 `MYCELIUM` particles on the soil surface.
   - If `better-bats:bat_drop_guano_item` is set to `true`, drops a physical `ItemStack(Items.BONE_MEAL)` item entity at the block location.

---

## 💾 Save & Load Persistence

The guano counter is preserved across game saves using modern MC 26.2 `ValueOutput` and `ValueInput` mixin hooks:

```java
@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
private void betterbats$onAddAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
    output.putInt("betterbats:guano_ticks", this.betterbats$guanoTicks);
}

@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
private void betterbats$onReadAdditionalSaveData(ValueInput input, CallbackInfo ci) {
    this.betterbats$guanoTicks = input.getIntOr("betterbats:guano_ticks", 0);
}
```

---

## 🔗 Related Pages
- [[Bat Ecology, Photophobia & Daytime Roosting|Bat-Ecology-and-Photophobia]]
- [[Dynamic GameRules Reference Table|Dynamic-GameRules-Reference]]
- [[Sound Effects & Visual Particles Reference|Sound-Effects-and-Visual-Particles]]
