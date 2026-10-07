# Changelog

All notable changes to this project will be documented in this file.

## [1.1.48+26.3]

### Fixed
- **Mixin Descriptor Fix**: Corrected `NaturalSpawnerMixin` injection target descriptor for Minecraft 26.3's updated `NaturalSpawner.mobsAt` method signature, resolving startup world generation crashes.

## [1.1.47+26.3] (Broken)

### Added
- **Minecraft 26.3 Compatibility**: Scaffolding and calibration for Minecraft 26.3 (`26.3-snapshot-6` / `26.3-rc-2`), Fabric Loader `0.19.3`, and Fabric API `0.156.1+26.3`.
- **Dependency Alignment**: Compiled against DasikLibrary 1.8.39 with Java 25 toolchain.
