# Ascension Cores — Claude Context

## Environment

- Minecraft 26.1.2, Fabric Loader 0.19.2, Fabric API 0.149.0+26.1.2
- Gradle 9.4.1, JDK 26 at `C:\Program Files\Java\jdk-26.0.1`
- Build: `.\gradlew.bat build` — jar lands in `build/libs/`
- Run server: `.\gradlew.bat runServer` — accepts EULA at `run/eula.txt`

## MC 26 Mapping Rules (Critical)

MC 26.1.2 ships pre-mapped with official Mojang names. **Do not add a mappings line** to build.gradle and **do not call** `loom.officialMojangMappings()` — Loom rejects it ("Cannot use Mojang mappings in a non-obfuscated environment").

Access widener header must use `official` namespace:
```
accessWidener v2 official
```

If an access widener is needed, add to `build.gradle` inside the `loom {}` block:
```groovy
loom {
    accessWidenerPath = file('src/main/resources/ascensioncores.accesswidener')
}
```

## Finding Actual Class/Method Signatures

When the compiler can't find a class or a method signature looks wrong, inspect the merged jar directly:

```powershell
$jar = "C:\Git\Ascension Cores\.gradle\loom-cache\minecraftMaven\net\minecraft\minecraft-merged-*\26.1.2\*.jar"
cd $env:TEMP
& "C:\Program Files\Java\jdk-26.0.1\bin\jar.exe" xf $jar net/minecraft/the/ClassName.class
& "C:\Program Files\Java\jdk-26.0.1\bin\javap.exe" -p net/minecraft/the/ClassName.class
```

Known gotchas discovered during CullTag development:
- `ClipContext` is in `net.minecraft.world.level`, not `world.phys`
- `Connection.send()` second param is `io.netty.channel.ChannelFutureListener`, not `PacketSendListener`
- `CommandSourceStack.hasPermission(int)` is gone — use `src.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)` etc. (see `net.minecraft.server.permissions.Permissions` for constants)

## Mixin Rules

- `@Shadow` must target the class that **declares** the field, not a subclass. If shadowing a field on a parent, write the mixin against the parent class.
- Implementing an interface on a parent mixin automatically makes it available on all subclass instances — use this for duck-typing.
- Keep mixins minimal. Prefer event callbacks over mixins when Fabric API covers the use case.

## Config Style

Follow the Bad Dream Mod / CullTag pattern — `Files.writeString` with a text block, `StandardCharsets.UTF_8`. Do **not** use `Properties.store()` (adds timestamp noise and encoding issues on Windows).

```java
Files.writeString(CONFIG_PATH, toPropertiesString(), StandardCharsets.UTF_8);

private static String toPropertiesString() {
    return """
            # My config
            someKey=%s
            """.formatted(someValue);
}
```

## Commands

Permission check for op-level-2 commands:
```java
Commands.literal("ascensioncores")
    .requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
```

Register via:
```java
CommandRegistrationCallback.EVENT.register(
    (dispatcher, registryAccess, environment) -> MyCommand.register(dispatcher));
```

## fabric.mod.json

- `"environment": "server"` for server-only mods
- `"${version}"` is substituted by `processResources` from `mod_version` in gradle.properties
- No `icon` field unless the file actually exists in `src/main/resources/assets/ascensioncores/`

## Package / ID Conventions

- Mod ID: `ascensioncores`
- Root package: `com.ascensioncores`
- Mixin package: `com.ascensioncores.mixin`
