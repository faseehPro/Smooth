# SmoothPVP Client

A fair-play Fabric mod for **Minecraft 1.21.11** focused on:
- **Clean custom HUD** — FPS, ping, coordinates, potion timers, WASD/space keystrokes
- **PVP feel** — hit-indicator flash, optional disabled view-bob & hurt-camera-shake for a steadier crosshair
- **FPS/smoothness** — particle cap toggle (biggest FPS killer in close combat)

**Not included, on purpose:** killaura, reach extension, autoclickers, x-ray/ESP, auto-aim.
Those give an unfair advantage over other players and break most server rules —
this mod only changes what *your own client renders/feels like*, not how combat
actually resolves.

> **Heads up on 1.21.11:** this is a big transitional Minecraft release — it's
> the last obfuscated version, Fabric's HUD rendering API was rewritten
> (this project now uses the new `HudElementRegistry` instead of the old,
> removed `HudRenderCallback`), and the project uses Mojang's official
> mappings instead of Yarn since Fabric is discontinuing Yarn after this
> version. Some exact version numbers in `gradle.properties`/`build.gradle`
> (Fabric API build, Loom version) may drift quickly — if the build fails on
> a "could not find" dependency error, check https://fabricmc.net/develop
> for the current numbers for 1.21.11 and swap them in.

## Project layout

```
smoothpvp-client/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── src/main/
    ├── java/com/smoothpvp/client/
    │   ├── SmoothPvpClient.java      # mod entrypoint, keybind, tick loop
    │   ├── config/ClientConfig.java  # all toggles, saved to config file
    │   ├── hud/HudRenderer.java      # the actual HUD drawing
    │   └── mixin/
    │       ├── GameRendererMixin.java     # view-bob / hurt-shake cancel
    │       └── ParticleManagerMixin.java  # particle cap
    └── resources/
        ├── fabric.mod.json
        └── smoothpvp.mixins.json
```

## Building it

You'll need:
- **JDK 17** (Minecraft 1.20.1 requires it)
- An internet connection the first time, so Gradle can download Fabric Loom,
  the Minecraft jar, Yarn mappings, and Fabric API

Steps:
```bash
# 1. Add the Gradle wrapper (one-time, only needed if you don't already have one)
gradle wrapper --gradle-version 8.8

# 2. Build the mod jar
./gradlew build

# Output appears at:
# build/libs/smoothpvp-client-1.0.0.jar
```

Then drop that jar into your `.minecraft/mods/` folder alongside the
matching **Fabric Loader** and **Fabric API** for 1.20.1, and launch.

## Building it online (no local Gradle/Java needed)

This project includes a ready-made GitHub Actions workflow at
`.github/workflows/build.yml` that builds the jar in the cloud for you:

1. Create a new (can be private) repo on [github.com](https://github.com) and
   upload/push this whole `smoothpvp-client` folder into it.
2. GitHub will automatically run the "Build Mod Jar" workflow (or click the
   **Actions** tab → **Build Mod Jar** → **Run workflow** if it doesn't
   trigger automatically).
3. Once it finishes (a couple minutes), open that workflow run and scroll to
   **Artifacts** — download `smoothpvp-client-jar`. Unzip it and you'll have
   your `.jar`.

No installs on your own machine required — GitHub's servers do the actual
Gradle/Java/Minecraft-download work. If the build fails, check the red step
in the log; it's almost always a version-number mismatch (see the callout
above about 1.21.11 dependency numbers drifting).

Alternative if you'd rather have a full online dev environment instead of a
one-shot build: open the repo in **GitHub Codespaces** (button on the repo
page → Code → Codespaces → Create), which gives you a full Linux VM with
internet in your browser — run `gradle build` there directly.

## In-game controls

| Key | Action |
|---|---|
| `]` (right bracket) | Toggle the whole HUD on/off |

All individual toggles (show FPS, show coords, disable view-bob, particle cap
number, etc.) live in `.minecraft/config/smoothpvp-client.properties` — edit
that file directly and restart, or wire up a proper in-game options screen
later using Fabric's `Screen` API if you want click-to-toggle menus.

## Notes on the mixins

`GameRendererMixin` and `ParticleManagerMixin` target method names based on
Yarn mappings for 1.20.1. If you bump the Minecraft/Yarn version, mixin
method names (`bobView`, `bobViewWhenHurt`, `getMaxParticleCount`, etc.) may
shift — check the new mappings on the [Fabric mappings browser](https://linkie.shedaniel.dev/mappings)
and update the `method = "..."` strings accordingly. This is the single
biggest source of build errors when upgrading a Fabric mod's MC version.

## Ideas for next steps

- In-game config GUI (Cloth Config or Fabric's own `Screen` subclass)
- Cosmetic crosshair shapes/colors picker
- Death/kill feed overlay
- Server ping graph instead of a single number
- Toggle-sprint / toggle-sneak (still fair — just remaps a hold to a toggle)
