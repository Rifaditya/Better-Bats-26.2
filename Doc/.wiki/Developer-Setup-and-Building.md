# 💻 Developer Setup & Building from Source

| Requirement | Environment Specification |
|---|---|
| **JDK Version** | Java 25 (`Adoptium HotSpot jdk-25.0.3.9-hotspot`) |
| **Gradle Engine** | Gradle 9.3+ (`--no-daemon` mandatory) |
| **Fabric Loom Plugin** | Loom 1.15+ |
| **Build Command** | `./gradlew build --no-daemon` |
| **Test Command** | `./gradlew test --no-daemon` |
| **Output Jar Location** | `build/libs/better-bats-1.1.23+26.2.jar` |

---

## 🛠️ Environment Setup & Prerequisites

Before setting up or compiling Better Bats from source:

1. **Install JDK 25**: Download Java 25 (Adoptium OpenJDK). Verify using `java -version`.
2. **Configure `JAVA_HOME`**: Set `JAVA_HOME` to your JDK 25 path (e.g. `C:/Program Files/Eclipse Adoptium/jdk-25.0.3.9-hotspot`).
3. **Gradle Configuration**: Ensure `gradle.properties` references JDK 25:
   ```properties
   org.gradle.java.home=C:/Program Files/Eclipse Adoptium/jdk-25.0.3.9-hotspot
   ```

---

## 🚀 Building the Release JAR

To compile and package the production JAR without launching background daemons:

```bash
# Clean and build release JAR
./gradlew build --no-daemon
```

Upon successful compilation, the built release JAR will be generated in `build/libs/`:
- `better-bats-1.1.23+26.2.jar`
- `better-bats-1.1.23+26.2-sources.jar`

---

## 🧪 Automated Testing Verification

Execute JUnit and GameTest headless suites to verify entity mechanics prior to release:

```bash
# Run automated headless test suite
./gradlew test --no-daemon
```

---

## 📂 IDE Setup (IntelliJ IDEA / VS Code)

1. Clone the repository to your local workspace directory.
2. Open IntelliJ IDEA or VS Code and select **Open Project** pointing to `Better Bats 26.2`.
3. Import as a Gradle project. Allow Loom to auto-generate Fabric mappings and run configurations.
4. Execute `./gradlew genSources` if workspace source definitions require indexing.

---

## 🔗 Related Pages
- [[Minecraft 26.2 Setup & Technical Guide|Minecraft-26.2-Guide]]
- [[Architecture & Package Layout|Architecture-and-Package-Layout]]
- [[Mixin Reference & Injection Hooks|Mixin-Reference-and-Hooks]]
