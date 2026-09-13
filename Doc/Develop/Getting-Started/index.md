# Getting Started

## Requirements
| Dependency | Version |
| :--- | :--- |
| Minecraft | 26.1.2 |
| Fabric Loader | ≥ 0.19.1 |
| Fabric API | 0.145.4+26.1.2 |
| Java | 25 |

## Building from Source
1. Clone the repository.
2. Ensure you have JDK 25 installed.
3. Run the following command:
```bash
./gradlew build
```
The compiled JAR will be located in `build/libs/better-bats-1.0.0+build.1.jar`.

## Integration with DasikLibrary
This mod requires **DasikLibrary** v1.6.9+ at runtime. Ensure it is present in your `mods` folder.
```gradle
repositories {
    maven { url = "https://maven.dasik.net/" }
}

dependencies {
    implementation "net.dasik.social:dasik-library:1.6.9+build.22"
}
```
