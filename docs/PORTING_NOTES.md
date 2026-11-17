# Porting Notes (Post-1.20.1 Baseline)

Baseline: **Minecraft 1.20.1 + Forge 47.4.28 (Java 17, ForgeGradle, official mappings)**

## Current baseline status

- Core baseline implemented for approved matrix (4 ore blocks):
  - `basalt_coal_ore` (Coal), `basalt_diamond_ore` (Diamond), `blackstone_iron_ore` (Raw Iron), `netherrack_iron_ore` (Raw Iron)
- Registries, data-driven worldgen/resources, config schema/wiring (partial), assets/lang, and docs are in place.
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
- Current implementation mixes data files and custom placement modifiers:
  - Config-aware: `enableGeneration`, per-ore `enabled`, `veinsPerChunk`, `minY`, `maxY`
  - **Static/limitation:** `veinSize` is still defined in `configured_feature` JSON and not yet driven by config
- When porting, decide whether to:
  - keep data-driven worldgen + custom modifiers/codecs, or
  - move configured/placed feature definitions to code/datagen for full config/dynamic control

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
- Nether generation places approved ores as expected
- Drops match approved matrix (including silk touch/fortune expectations)
- Config file generates and key toggles/parameters take effect
- No `/reference/` or `/recursos/` content leaks into repo artifacts/releases
