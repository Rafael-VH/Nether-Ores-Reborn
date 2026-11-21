# Proposal: In-Game Config Screen (Forge 1.20.1)

## Intent

Expose the mod's configuration inside the game instead of requiring players to hand-edit
`config/nether-ores-reborn.toml`. The screen must open from the existing **Config** button of
Forge's Mods list, using Forge's own extension point and no new dependencies.

## Motivation

- The COMMON config has 22 user-facing values (2 general + 5 per ore). Today the only way to read
  or change them is a text file, which is undiscoverable for players and error-prone.
- An existing `.toml` from an older version silently overrides new defaults. The recent
  `blackstone_iron_ore` generation report showed that users cannot inspect effective values in game.
- `veinSize` is declared in the config but not applied by worldgen. A generated UI would make that
  gap look like a bug, so it must stay hidden until the value is actually wired.

## Scope

### In scope

- Client-only `Screen` built with vanilla widgets (`Button`, `CycleButton`, `AbstractSliderButton`).
- Registration through `net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory`, so
  Forge's `ModListScreen` enables its Config button only for this mod.
- Paged layout: one **General** page plus one page per ore variant.
- Controls: `enableGeneration`, global `dropMultiplier`; per ore `enabled`, `veinsPerChunk`,
  `minY`, `maxY`, `dropMultiplier`.
- Snapshot on open + `Cancel` restores, `Done` persists, `Reset to defaults` restores defaults.
- Refactor `NetherOresRebornConfig` so defaults live in one place shared by the spec builder and
  the screen.
- `en_us` / `es_es` keys and documentation updates.

### Out of scope

- Wiring `veinSize` to generation (separate change); the control stays hidden.
- Config sync to dedicated servers, server-side commands, or permission handling.
- Live regeneration of already generated chunks.
- Fabric/NeoForge config integration (future porting).
- Any change to worldgen JSON, loot tables, or block behavior.

## Risks

| Risk | Mitigation |
| :--- | :--- |
| COMMON config edited on a client does not affect a dedicated server | Detect a remote server and show an explicit warning on the screen |
| `ConfigScreenHandler` is a client-only class and crashes a dedicated server if loaded | Register behind `DistExecutor.safeRunWhenOn(Dist.CLIENT, ...)` from the mod constructor |
| `ConfigValue#set` throws when the spec is not loaded | The screen is only reachable after config load (Mods list is post-init) |
| Slider normalization (0..1) introduces off-by-one on integer values | Centralized mapping helpers plus explicit min/max/step per control |
| Players expect changes to affect the current world immediately | Permanent warning: changes apply to newly generated chunks only |
| Reset-to-defaults drifts from real defaults | Single source of truth extracted before any UI work (work unit 1) |

## Size estimate

Roughly 500–650 authored lines across 5 files (2 new Java files, 1 refactor, 2 lang files, docs).
Below the 800-line review budget; single PR is expected.

## Rollback

The change is additive: removing the extension point registration hides the button and returns the
mod to the TOML-only workflow. No data migration and no persisted format change. Each work unit is
an independent commit that can be reverted on its own.

## Success criteria

1. Selecting the mod in the Mods list enables the Config button; clicking it opens the screen.
2. Values shown match the effective `.toml` values.
3. `Done` writes the file, `Cancel` restores the snapshot, `Reset` restores documented defaults.
4. `./gradlew build` passes and no client class is referenced from common code paths.
5. Singleplayer works end to end; a remote-server connection shows the local-copy warning.
