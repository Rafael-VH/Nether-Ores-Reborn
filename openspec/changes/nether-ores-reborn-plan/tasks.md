# Tasks: Nether Ores Reborn Baseline Plan (MC 1.20.1)

## Phase 0 — Local Reference Analysis (Clean Room)

- [x] 0.1 Confirm `/reference/` and `/recursos/` are gitignored and contain no files tracked by Git
  - **Outcome:** Local analysis materials remain outside version control
  - **Verify:** `.gitignore` updated to include `/reference/` and `/recursos/`; `git check-ignore` confirms `recursos` ignored; `git status` shows no tracked `/reference/` or `/recursos/` contents (2026-11-17)
- [x] 0.2 Perform functional-only analysis of the original mod (no code/asset copying)
  - **Outcome:** Functional notes captured locally in `reference/notes.md` (local-only, gitignored) describing observed behaviors conceptually
  - **Verify:** Notes written in natural language with clean-room constraints; no source/assets copied into repo
- [x] 0.3 Finalize baseline ore matrix proposal for user approval (variants + drops concept)
  - **Outcome:** Approved baseline ore matrix recorded (4 blocks): Coal (Basalt) -> Coal, Diamond (Basalt) -> Diamond, Iron (Blackstone) -> Raw Iron, Iron (Netherrack) -> Raw Iron; Emerald/Lapis/Redstone/Gold excluded
  - **Verify:** User explicitly approved the final matrix on 2026-11-17; recorded in `reference/notes.md` and SDD tasks

## Phase 1 — Bootstrap (Forge 1.20.1 MDK)

- [x] 1.1 Define project identifiers (mod id, package namespace, display name, versioning baseline)
  - **Depends on:** 0.3
  - **Outcome:** Agreed identifiers documented: `mod_id=nether_ores_reborn`, package `com.rexthar.netheroresreborn`, display name `Nether Ores Reborn`, initial version `0.1.0` (2026-11-17)
  - **Verify:** User-confirmed values recorded in `AGENTS.md` and SDD plan artifacts; to be reflected in scaffolded mod metadata during Phase 1.2
- [x] 1.2 Initialize project from official Forge 1.20.1 MDK (Java 17 / ForgeGradle)
  - **Depends on:** 1.1
  - **Outcome:** Clean MDK-based project structure in repo (no MCreator artifacts), customized for `nether_ores_reborn` / `com.rexthar.netheroresreborn`
  - **Verify:** MDK 1.20.1-47.4.28 files integrated; `.\gradlew.bat build` **BUILD SUCCESSFUL** (Java 17 toolchain via `JAVA_HOME=C:\Program Files\Java\jdk-17`) on 2026-11-17
- [x] 1.3 Configure repository hygiene for baseline workflow
  - **Depends on:** 1.2
  - **Outcome:** `.gitignore` includes `/reference/`, `/recursos/`, `/run/`, `/build/`, `.gradle/` and additional Forge/IDE/log ignores
  - **Verify:** Generated build artifacts are ignored (`git check-ignore` confirms `build`, `.gradle`); local reference/resources remain untracked/ignored (2026-11-17)

## Phase 2 — Core Registries and Content Skeleton

- [x] 2.1 Create main mod class and shared constants/utilities
  - **Depends on:** 1.3
  - **Outcome:** Mod entrypoint initializes registries and common constants (`ModConstants`, `ModBlocks`, `ModItems`, `ModCreativeTabs`)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17
- [x] 2.2 Implement block registrations for approved Netherrack/Basalt/Blackstone ore variants
  - **Depends on:** 2.1, 0.3
  - **Outcome:** All approved ore blocks registered with consistent naming and properties (`basalt_coal_ore`, `basalt_diamond_ore`, `blackstone_iron_ore`, `netherrack_iron_ore`)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; in-dev registry visibility to be confirmed in client smoke test (2.3/7.1)
- [x] 2.3 Implement BlockItem registrations and creative tab placement
  - **Depends on:** 2.2
  - **Outcome:** All ore blocks obtainable via items and visible in creative inventory tab(s) (`nether_ores_reborn_tab`)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; dev client smoke test pending

## Phase 3 — World Generation (Nether)

- [x] 3.1 Design and implement baseline worldgen definitions for ore placement (configured/placed features and biome integration approach for 1.20.1)
  - **Depends on:** 2.2
  - **Outcome:** Nether ore generation wired for approved variants with default parameters (configured/placed features + Forge biome modifiers for `#minecraft:is_nether`)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; in-game runtime validation pending (3.2)
- [ ] 3.2 Validate in-game Nether generation behavior (dev world)
  - **Depends on:** 3.1
  - **Outcome:** Ore blocks generate in Nether under expected conditions
  - **Verify:** Client run + test world checks (exploration/logs/commands) confirm generation

## Phase 4 — Loot Tables and Drops

- [x] 4.1 Create original loot table JSON files for each ore block
  - **Depends on:** 2.2, 0.3
  - **Outcome:** Each approved ore block has a corresponding loot table under `data/nether-ores-reborn/loot_tables/blocks/`
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; in-game drop validation pending (4.2)
- [ ] 4.2 Validate drop behavior in-game
  - **Depends on:** 4.1
  - **Outcome:** Breaking ores yields intended drops (baseline behavior)
  - **Verify:** Dev client tests confirm expected drops for representative variants/tools/conditions

## Phase 5 — Configuration (`nether-ores-reborn.toml`)

- [x] 5.1 Implement config schema and defaults (toggles, spawn params, drop multipliers)
  - **Depends on:** 1.2
  - **Outcome:** Config spec loads and generates/defaults are available (`config/nether-ores-reborn.toml` via COMMON config)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; runtime config file generation/load to be validated in run environment
- [x] 5.2 Wire config values into worldgen and drop behavior (as designed)
  - **Depends on:** 3.1, 4.1, 5.1
  - **Outcome:** Enabled flags/ranges/frequencies/multipliers affect runtime behavior according to spec (with noted limitation)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17. Wired: `general.enableGeneration`, per-ore `enabled`, `veinsPerChunk`, `minY`, `maxY` via custom placement modifiers; drop multipliers via GLM. **Pending/limitation:** `veinSize` remains static in `configured_feature` JSONs (config value exists but is not yet applied); runtime validation pending.
- [x] 5.3 Document config keys and defaults in planning/docs notes (repo-appropriate location)
  - **Depends on:** 5.2
  - **Outcome:** Configuration is understandable for users/contributors
  - **Verify:** Added `docs/CONFIG.md` documenting implemented keys/defaults and current limitations (2026-11-17)

## Phase 6 — Original Assets and Localization

- [x] 6.1 Produce original textures (16x16), blockstates, and block/item models for approved variants
  - **Depends on:** 2.2, 0.3
  - **Outcome:** Complete original visual/resource set for baseline ores (temporary original placeholders generated)
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; dev client visual verification pending
- [x] 6.2 Add localization files (`en_us.json`, `es_es.json`) with original naming
  - **Depends on:** 2.2
  - **Outcome:** Block/item names localized in English and Spanish
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; lang keys added for 4 blocks and creative tab (in-game locale rendering pending)

## Phase 7 — Verification, Polish, and Port-Readiness

- [ ] 7.1 Run full baseline verification (build + client/server smoke tests)
  - **Depends on:** 3.2, 4.2, 5.2, 6.1, 6.2
  - **Outcome:** Baseline mod is stable enough for iterative use on 1.20.1
  - **Verify:** `.\gradlew.bat build` **BUILD SUCCESSFUL** with JDK 17 on 2026-11-17; client/server smoke tests and in-game feature validation (3.2, 4.2, visual/config runtime checks) remain pending manual execution
- [x] 7.2 Clean-room and packaging hygiene check
  - **Depends on:** 7.1
  - **Outcome:** No original mod assets/code/decompiled artifacts found in tracked repo files or inspected build jar
  - **Verify:** `git ls-files` scan showed no `reference/`/`recursos/`/original-mod identifiers; `.gitignore` covers local/reference/build outputs; `build/libs/nether_ores_reborn-0.1.0.jar` entry scan found no `reference`/`recursos`/`netheroresplus` hits (2026-11-17)
- [x] 7.3 Capture porting notes for next Minecraft versions
  - **Depends on:** 7.1
  - **Outcome:** Documented list of version-sensitive areas (worldgen/config/registries/data) and upgrade priorities
  - **Verify:** Added `docs/PORTING_NOTES.md` (2026-11-17)

## Cross-Cutting / Tracking Tasks

- [ ] X.1 Keep `AGENTS.md` and SDD artifacts in sync when scope/stack decisions change
  - **Depends on:** none
  - **Outcome:** Planning and guidance remain consistent
  - **Verify:** Updated sections/history reflect approved decisions
- [ ] X.2 Record key decisions and verification evidence in Engram/OpenSpec progress tracking
  - **Depends on:** none
  - **Outcome:** Resumable context for future sessions/porting work
  - **Verify:** Memory/progress entries exist for major milestones and blockers
