# Design: Nether Ores Reborn (Baseline 1.20.1 Plan)

## Technical Approach

Implement the mod as a **clean-room Forge 1.20.1 (47.4.28)** project bootstrapped from the official MDK, using **Java 17** and **ForgeGradle**. The baseline prioritizes correctness, traceability, and phase-gated verification over feature breadth.

The plan follows `AGENTS.md` phases:
1. Phase 0: local functional analysis only (reference materials in ignored folders)
2. Phase 1: MDK bootstrap + build sanity
3. Phase 2: block/item/creative tab registration
4. Phase 3: Nether worldgen integration
5. Phase 4: loot tables (data-driven JSON)
6. Phase 5: config (`nether-ores-reborn.toml`) and wiring
7. Phase 6: original assets/lang
8. Phase 7: testing/polish (publication excluded from this plan)

## Architecture Decisions

### D1: Clean-room boundary as a hard constraint
- **Decision:** Treat legal/ethical rules in `AGENTS.md` as acceptance constraints, not guidelines.
- **Rationale:** The source inspiration is an unmaintained mod with no public source; legal risk must be minimized.
- **Implications:** Reference JAR/decompiled outputs stay local in `/reference/` and `/recursos/`; all names/assets/data/code are authored originally.

### D2: MDK-first bootstrap and stable baseline
- **Decision:** Use official Forge 1.20.1 MDK as the only starting point.
- **Rationale:** Ensures compatible toolchain defaults (Java 17, ForgeGradle, mappings ecosystem expected by 1.20.1).
- **Implications:** First milestone is a compiling template before any mod content.

### D3: Data-driven resources where practical
- **Decision:** Prefer JSON/data-driven definitions for loot tables and worldgen resources; use code for registration and conditional wiring as needed.
- **Rationale:** Easier iteration, review, and future porting of resource formats.
- **Implications:** Phase 3/4 produce/validate resource files under `data/`; config effects on data-driven systems must be explicitly designed (static files + conditional registration/code paths as appropriate for 1.20.1).

### D4: Explicit seams for future ports
- **Decision:** Organize by responsibility (registry, worldgen, config, datagen/data, assets) and avoid scattering version-sensitive logic.
- **Rationale:** User plans to upgrade versions after 1.20.1 baseline.
- **Implications:** Document version-sensitive APIs (worldgen JSON schema/code hooks, config API, registry events) during implementation to reduce migration cost.

## Planned Module/Folder Structure (Target)

> Paths/namespaces below include placeholders pending user confirmation; they define intent, not final identifiers.

- `src/main/java/com/<namespace>/netheroresreborn/`
  - `NetherOresReborn.java` (mod entrypoint, mod id/constants wiring)
  - `registry/` (blocks, items, creative tabs, possibly feature/placement registries depending on chosen 1.20.1 approach)
  - `worldgen/` (worldgen registration/helpers and biome integration hooks)
  - `config/` (Forge config spec/handler for `nether-ores-reborn.toml`)
  - `util/` (shared constants/helpers)
- `src/main/resources/`
  - `META-INF/mods.toml`
  - `assets/nether-ores-reborn/` (`blockstates/`, `models/block/`, `models/item/`, `textures/block/`, `lang/`)
  - `data/nether-ores-reborn/` (`loot_tables/blocks/`, `worldgen/` resources as needed)
- `openspec/changes/nether-ores-reborn-plan/` (this planning change)

## Configuration Design (Planning Level)

Target file: `config/nether-ores-reborn.toml`

### Proposed sections/keys (subject to refinement during implementation)
- `[general]`
  - `enableGeneration` (global master switch)
  - `dropMultiplier` (global multiplier, if applied globally)
- `[ores.<ore_id>]` (per ore variant or per base family entry, finalized after ore matrix decision)
  - `enabled`
  - `veinSize` (or equivalent)
  - `veinsPerChunk` / `chance`
  - `minY`, `maxY`
  - `dropMultiplier` (optional per-ore override)

### Mapping to systems
- Generation toggles/ranges/frequency -> worldgen registration/placement logic
- Drop multipliers -> loot behavior implementation strategy (data-defined where feasible; code-assisted only if necessary and justified)

## Data & Resource Strategy

- **Loot tables:** one JSON per block under `data/nether-ores-reborn/loot_tables/blocks/` with original structure/values.
- **Worldgen:** define configured/placed features and biome modifiers (or Forge 1.20.1-equivalent mechanism chosen during implementation) in a consistent, reviewable layout.
- **Localization:** `en_us.json` and `es_es.json` maintained together to avoid key drift.
- **Assets:** original 16x16 textures and matching block/item models/blockstates; naming aligned with mod id and block registry names.

## Verification Strategy (Phase Gates)

- **After Phase 1:** `./gradlew build` passes on clean template setup
- **After Phase 2:** build passes; blocks/items/creative tab visible in dev client
- **After Phase 3:** build passes; ore placement observable in Nether test world (targeted checks/logs/commands as appropriate)
- **After Phase 4:** loot table JSON validity and in-game drop checks
- **After Phase 5:** config file generation/load behavior and config-driven toggles validated
- **After Phase 6:** assets/lang load without missing texture/model/key issues
- **After Phase 7:** final build and client/server smoke checks, no `/reference/` artifacts in outputs

## Open Decisions (Need User Input Before/During Implementation)

1. **Namespace/mod id confirmation:** e.g. mod id `nether_ores_reborn`, package `com.<user>.netheroresreborn`
2. **Baseline ore matrix:** exact list of ores/variants and what each drops (conceptually inspired, originally defined)
3. **Config granularity:** per-variant vs per-family config entries
4. **Config UX:** Forge config only vs optional in-game config screen dependency later
5. **Reference analysis workflow (Phase 0):** confirm when local analysis in `/reference/` is complete enough to finalize the ore matrix

## Future Porting Notes (Post-Baseline)

- Re-evaluate worldgen/config APIs first when porting to newer versions
- Keep naming/resource IDs stable (`nether_ores_reborn:*`) to ease data migration
- Prefer isolated compatibility shims per version during future port phases (if multi-version strategy is adopted later)
