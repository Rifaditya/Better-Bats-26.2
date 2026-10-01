# Architecture & Design Overview: Better Bats (MC 1.21.11)

Better Bats enhances vanilla Minecraft bats with murmuration flocking, guano fertilization, phototaxis AI, pest hunting, and trait genetics under Minecraft 1.21.11.

## Package Structure

- `net.vanillaoutsider.betterbats`
  - `BetterBatsFabric.java`: Mod initializer, Dynamic GameRule registration, animal genetics registration, command registration.
  - `BetterBatsFabricClient.java`: Client entrypoint.
  - `BatStateAccessor.java`: Accessor interface for transient bat state (guano ticks, panic, goals).
- `net.vanillaoutsider.betterbats.ai`
  - `BatFlightHelper.java`: Flocking algorithms (BOIDs: Cohesion, Alignment, Separation), altitude caps, comfort zones, and predator avoidance.
  - `BatDiveBombGoal.java`: AI goal targeting subterranean pests (silverfish, endermites).
  - `BatHuntLightGoal.java`: AI goal for night phototaxis behavior circling block light sources.
  - `BatPanicGoal.java`: AI goal for rapid dispersal from loud vibrations/events.
  - `BatRoostHelper.java`: Valid roost checker (solid ceilings, pointed dripstone, hanging lanterns, chains, leaves).
  - `BatSleepGoal.java`: Daylight and storm roost seeking with peer clustering.
- `net.vanillaoutsider.betterbats.command`
  - `BetterBatsCommand.java`: Brigadier command suite (`/betterbats` and `/bb`).
  - `BatCommandHelper.java`: Diagnostic inspect and test flock spawner.
  - `CommandSuggestionsHelper.java`: Tab suggestions and input normalization.
- `net.vanillaoutsider.betterbats.config`
  - `BetterBatsConfig.java`: Config backing and sparse JSON persistence.
  - `ModMenuIntegration.java`: ModMenu screen provider.
  - `YaclScreenHelper.java`: YACL v3 client GUI configuration.
- `net.vanillaoutsider.betterbats.mixin`
  - `BatMixin.java`: Mixin into `Bat` injecting AI goals, flocking, genetics, and guano lifecycle.
  - `GameEventDispatcherMixin.java`: Intercepts vibrations/loud sounds to trigger bat panic.
  - `MobAccessor.java`: Accessor for goal selector.
  - `NaturalSpawnerMixin.java`: Dynamic spawn weight adjustment for ambient category.
- `net.vanillaoutsider.betterbats.util`
  - `BatDebugHelper.java`: Zero-allocation debug mode gating.
  - `ModVersionGuard.java`: Runtime class verification.
