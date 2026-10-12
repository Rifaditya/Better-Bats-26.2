# 🦇 Better Bats — MC 1.21.11 Changelog

## [1.0.1+1.21.11] - 2026-10-12
### Fixed
- **Knot ClassLoader Runtime Safety**: Purged string-based class reflection check during mod initialization to prevent runtime classloading failures in production environments.

## [1.0.0+1.21.11] — 2026-10-01
### Added
- **MC 1.21.11 Multi-Era Port**: First release of Better Bats for Minecraft 1.21.11.
- **Bat Murmuration & BOIDs Flocking**: Bats coordinate flight dynamics using flocking vectors (Alignment, Cohesion, Separation).
- **Cave Guano & Natural Crop Fertilization**: Resting bats drop guano and fertilize farmland crops over time.
- **Phototaxis AI**: Bats seek light sources at night and return to dark cave roosts during daylight.
- **Pest Control Behavior**: Bats actively hunt silverfish and endermites in subterranean caves.
- **Dynamic GameRules & Brigadier Command Suite**: Full control via `/betterbats` and `/bb` command suite.
- **Genetics & Trait Variation**: Variable wingspan scale, flight velocity, and attack attributes powered by DasikLibrary.
