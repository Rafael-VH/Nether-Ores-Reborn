# Porting Notes (Post-1.20.1 Baseline)

Baseline: **Minecraft 1.20.1 + Forge 47.4.28 (Java 17, ForgeGradle, official mappings)**

## Current baseline status

- Core baseline implemented for approved matrix (4 ore blocks):
  - `basalt_coal_ore` (Coal), `basalt_diamond_ore` (Diamond), `blackstone_iron_ore` (Raw Iron), `netherrack_iron_ore` (Raw Iron)
- Registries, data-driven worldgen/resources, config schema/wiring, assets/lang, and docs are in place.
- Config wiring covers `enableGeneration`, per-ore `enabled`, `veinsPerChunk`, `minY`, `maxY` (worldgen) and drop multipliers (Global Loot Modifier). Known gap: `veinSize` is declared in config but still static in `configured_feature` JSON.
- Published releases: `v0.1.0-beta`, `v0.1.1-beta`, `v0.1.2-beta`.
- Manual in-game validations are still pending (generation visibility, drops in gameplay, client/server smoke, locale/visual checks).

## Version-sensitive areas to review first when porting

### 1. Java / Gradle / Build tooling
- Reconfirm required Java version for target MC/loader.
- Update Gradle wrapper and ForgeGradle/loader tooling as required by target version.
- Revalidate toolchain configuration and CI/dev commands (`build`, `runClient`, `runServer`).

### 2. Mappings and code APIs
- Reconfirm mapping channel/version strategy for the target.
- Review API changes in registries, events, config registration, and worldgen hooks.
- Watch for renamed/moved classes (placement modifiers, loot modifiers, registry APIs).

### 3. Worldgen pipeline

**Port this area with the most care.** The mod's generation depends on an ordering contract that is easy to break silently, because a wrong step produces no error: ores simply never appear.

- Each ore is a `configured_feature` (`minecraft:ore` + `minecraft:block_match` target) plus a `placed_feature` using `nether_ores_reborn:config_count` and `nether_ores_reborn:config_uniform_height_range`, injected through `forge:add_features` biome modifiers on `#minecraft:is_nether`.
- **Generation step contract:** ores are injected at `underground_decoration`, which is where vanilla generates `basalt_blobs`, `blackstone_blobs` and `ore_blackstone`. A `minecraft:ore` can only replace blocks that already exist when its step runs, so injecting at `underground_ores` (which runs earlier) silently produces no basalt/blackstone ores. This was the actual bug fixed in `0.1.2-beta`.
- Forge appends biome modifier features after the vanilla features of the same step, so the ores run after the blobs that create their targets.
- **Verify on the target version:**
  1. Read the real enum order of `GenerationStep.Decoration` from the mapped loader/Minecraft jar.
  2. Inspect the target `data/minecraft/worldgen/biome/*.json` and confirm at which step index `basalt_blobs`, `blackstone_blobs` and `ore_blackstone` are placed.
  3. Confirm whether the loader appends or prepends biome modifier features inside a step.
  4. Re-check that `minecraft:block_match` still exists (or which predicate type replaced it) for `minecraft:ore` targets.
- `veinSize` remains static in `configured_feature` JSON and is not driven by config.
- When porting, decide whether to:
  - keep data-driven worldgen + custom modifiers/codecs, or
  - move configured/placed feature definitions to code/datagen for full config/dynamic control (including `veinSize`).

### 4. Loot and drops
- Drop multipliers are implemented via **Global Loot Modifier** (Forge-specific).
- Validate GLM codec/registry APIs and loot condition syntax on target version/loader.
- Re-test silk touch / fortune / explosion decay behavior in-game.

### 5. Data/resource formats
- Revalidate JSON schemas for:
  - `worldgen/configured_feature`, `worldgen/placed_feature`
  - biome modifiers (`forge/biome_modifier` in this baseline)
  - loot tables and loot modifier definitions
  - lang/models/blockstates/pack metadata format versions
- **Loader-specific data folders:** Forge uses `data/<modid>/forge/biome_modifier/`, while NeoForge uses `data/<modid>/neoforge/biome_modifier/` and `neoforge:add_features` as the modifier type. The modifier payload (`biomes`, `features`, `step`) is otherwise the same; the `step` value must be re-validated against the target version.
- Check for renamed folders or loader-specific data locations when changing loader/version.

### 6. Config system
- Reconfirm config API and file naming/registration behavior.
- Preserve stable config keys where possible to reduce migration friction for users.
- If changing behavior semantics, document migration notes clearly.

## Recommended porting sequence (incremental)

1. **Toolchain bootstrap**: compile empty/mod skeleton on target version.
2. **Registries/content load**: blocks/items/tabs visible and IDs unchanged.
3. **Worldgen**: restore generation and revalidate config-aware behavior.
4. **Loot/drops**: restore baseline drops and multipliers.
5. **Assets/lang/polish**: fix resource format issues and UI naming.
6. **Verification**: build + client/server smoke + targeted gameplay checks.

## Manual validation checklist to run before/after port

- `./gradlew build` (or platform equivalent) succeeds
- Dev client starts and blocks/items/tab/localized names render correctly
- Nether generation places approved ores as expected, **in newly generated chunks** (existing chunks are never regenerated)
- Basalt ores appear in Basalt Deltas; blackstone ore appears in every Nether biome between Y 5 and 31 plus full-height in Basalt Deltas; netherrack ore appears everywhere in the Nether
- Drops match approved matrix (including silk touch/fortune expectations)
- Config file generates and key toggles/parameters take effect (delete any pre-existing `.toml` first, since stale values override new defaults)
- No `/reference/` or `/recursos/` content leaks into repo artifacts/releases
