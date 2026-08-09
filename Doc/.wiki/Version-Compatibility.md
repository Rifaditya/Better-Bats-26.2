# 📦 Version & Modloader Compatibility Matrix

| Minecraft Target | Mod Version | Build JAR Name | DasikLibrary Version | Compatibility Status |
|---|---|---|---|---|
| **MC 26.2** (Primary) | `1.1.23+26.2` | `better-bats-1.1.23+26.2.jar` | `>=1.8.2` (Built with `1.8.9`) | 🟢 Active Mainline Release |
| **MC 26.1.2** (Legacy) | `1.1.15-26.1.2` | `better-bats-1.1.15-26.1.2.jar` | `>=1.8.0` | 🟡 Maintained Legacy Branch |

> 📌 **Repository Source Disclaimer**: The documentation in this Wiki reflects the **current source code state in the repository**, which may include recent unreleased commits or developmental features ahead of public release builds on CurseForge and Modrinth.

---

## 🔒 1 Jar 1 Version Law & Strict Bounds

Better Bats strictly enforces the **1 Jar 1 Version** architectural mandate:
- **`fabric.mod.json` Bounds**:
  ```json
  "depends": {
      "fabricloader": ">=0.19.1",
      "minecraft": ">=26.2-",
      "java": ">=25",
      "fabric-api": "*",
      "dasik-library": ">=1.8.2"
  }
  ```
- **Open-Ended Bounds (`>=`)**: Open-ended lower bounds prevent rigid Fabric Loader pre-release locks while ensuring backwards safety.
- **Dependency Guard**: Better Bats requires `dasik-library` to be loaded at runtime; missing library dependencies will cause `BetterBatsFabric` to throw a `RuntimeException` during initialization.

---

## 🛡️ `ModVersionGuard` Class Enforcement

To prevent dangerous class loading crashes on mismatched Minecraft versions, `ModVersionGuard` executes during `onInitialize()` before any mixin or entity access occurs:

```java
public class ModVersionGuard {
    public static void checkClass(String modName, String className) {
        try {
            Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IncompatibleClassChangeError(
                "[" + modName + "] This mod build is incompatible with this version of Minecraft. Missing class: " + className
            );
        }
    }
}
```

---

## 🔗 Related Pages
- [[Minecraft 26.2 Setup & Technical Guide|Minecraft-26.2-Guide]]
- [[Developer Setup & Building from Source|Developer-Setup-and-Building]]
