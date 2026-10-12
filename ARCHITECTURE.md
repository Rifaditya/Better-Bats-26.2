# Architecture & Symbol Index: Better Bats

## 1. Mod Metadata & Entrypoint
- **Mod ID**: `better-bats`
- **Main Entrypoint**: `net.vanillaoutsider.betterbats.BetterBatsFabric` (`net.fabricmc.api.ModInitializer`)
- **Client Entrypoint**: `net.vanillaoutsider.betterbats.BetterBatsFabricClient`

## 2. Bytecode Mixin Target Registry
| Target Vanilla Class | Mixin Class | Purpose |
| :--- | :--- | :--- |
| `Vanilla Class` | `net.vanillaoutsider.betterbats.mixin.BatMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterbats.mixin.GameEventDispatcherMixin` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterbats.mixin.MobAccessor` | Core mixin hook |
| `Vanilla Class` | `net.vanillaoutsider.betterbats.mixin.NaturalSpawnerMixin` | Core mixin hook |

## 3. Core Mechanics & Subsystems
- **Source Root**: `src/main/java/`
- **Resource Root**: `src/main/resources/`

## 4. Dynamic GameRules & Commands
- **GameRules / Commands**: Configured dynamically via namespaced keys (`better-bats:*`).

## 5. Configuration & Sidedness Isolation
- **Sidedness**: Server-safe logic in main, client isolated in `src/client/java` or client entrypoint.
