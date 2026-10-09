# Durability Peek

Fabric client mod for **Minecraft Java 26.3**. Hover over a damageable item to
see its remaining durability, maximum durability and remaining percentage.

Example: **Durability: 250 / 1000 (25.0%)**

- Works with tools, weapons, armor, elytra and other damageable items.
- Displays full durability on new items and updates after damage or repairs.
- Uses the item's actual durability components, including custom maximum values.
- Green at 50% or more, yellow at 25–50%, red below 25%.
- English and German text, selected through Minecraft's language setting.
- Works with normal and advanced tooltips (F3+H), with one durability line.
- Unbreakable items keep Minecraft's existing unbreakable tooltip.
- Client only; works in singleplayer and on servers without server installation.

## Installation

1. Install **Fabric Loader 0.19.5 or newer** for **Minecraft 26.3**.
2. Install **Fabric API 0.161.0+26.3 or newer for Minecraft 26.3**.
3. Copy `durability-peek-26.3-1.0.2.jar` into that profile's `mods` folder.
   Remove older versions of this mod before updating, including versions released
   before the rename, so only one durability display mod is installed.
4. Launch the Fabric profile and hover over a tool in an inventory.

Minecraft 26.3 requires **Java 25**. For the standard launcher profile on Windows,
the mods directory is `%APPDATA%\.minecraft\mods`. Custom launchers and profiles
may use a different game directory.

Official downloads: [Fabric](https://fabricmc.net/use/installer/) and
[Fabric API](https://modrinth.com/mod/fabric-api/versions?g=26.3).

## Build

With JDK 25 installed:

```powershell
.\gradlew.bat build
```

The installable mod is `build/libs/durability-peek-26.3-1.0.2.jar`.
The `-sources.jar` is for development and should not be installed in `mods`.

Run `.\gradlew.bat releaseZip` to create an installation ZIP containing the mod,
this guide and its license in `build/distributions`.

## Development verification

`build` runs tests against Minecraft's item classes for full durability, damage,
repair, armor, custom maximum durability, preservation of lore, advanced tooltip
replacement and skipping empty, non-damageable and unbreakable items.

For a visual check, run `.\gradlew.bat runClient` and hover over damaged and
undamaged items with F3+H both enabled and disabled.

Version 1.0.2 is built against Fabric API 0.161.0+26.3 and accepts that version.
The minimum API version in the mod metadata is generated from the build dependency.
The build and all six automated tests pass against Minecraft 26.3.
The tests include Minecraft's actual normal and advanced tooltip generation.
A visual in-game check has not yet been performed.
