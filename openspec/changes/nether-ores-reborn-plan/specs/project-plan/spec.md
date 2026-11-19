# Delta for Project Plan

## ADDED Requirements

### Requirement: Clean-Room Compliance Guardrails
The project plan MUST enforce a clean-room implementation process for Nether Ores Reborn.

#### Scenario: Reference material usage
- GIVEN the original mod JAR and decompilation outputs may exist locally for analysis
- WHEN planning or implementing features inspired by the original mod
- THEN the plan MUST restrict usage to functional understanding only
- AND MUST prohibit copying code, assets, names, data files, or decompiled snippets into the repository
- AND MUST keep `/reference/` and `/recursos/` out of version control

### Requirement: Baseline Stack and Bootstrap Plan
The plan MUST target **Minecraft 1.20.1 + Forge 47.4.28 + Java 17 + ForgeGradle** as the initial baseline.

#### Scenario: Phase 1 bootstrap definition
- GIVEN no mod codebase is currently bootstrapped in the repository
- WHEN Phase 1 tasks are executed
- THEN the plan MUST require initialization from the official Forge 1.20.1 MDK
- AND MUST require a successful `./gradlew build` of the empty/template project before feature work
- AND MUST require definition of mod id, package namespace, and display metadata before content registration

### Requirement: Core Nether Ore Content Scope (Baseline)
The plan MUST define implementation work for Nether ore block families based on **Netherrack**, **Basalt**, and **Blackstone** variants.

#### Scenario: Baseline ore families planned
- GIVEN the project goal in `AGENTS.md`
- WHEN the planning artifacts are reviewed
- THEN the tasks/specs MUST include block/item registration, assets/data, world generation, and loot tables for all three base families
- AND MUST identify where the final ore list and per-ore drop behavior will be finalized
- AND MUST require user approval before creating final assets for any expanded variant set

### Requirement: Configurable Generation and Drops
The plan MUST include a configuration design for `config/nether-ores-reborn.toml` covering variant toggles, spawn frequency, height range, and drop multipliers.

#### Scenario: Configuration behavior specified
- GIVEN the mod must support configurable ore behavior
- WHEN design/tasks are defined
- THEN the plan MUST specify config keys/structure at a planning level
- AND MUST map each config value to affected systems (worldgen and/or loot/drop logic)
- AND MUST include verification steps for default config generation/loading

### Requirement: Data-Driven Resources and Original Assets
The plan MUST require original blockstates/models/textures/lang and data-driven loot tables/worldgen resources.

#### Scenario: Asset and data planning
- GIVEN the mod needs in-game content and resources
- WHEN resource tasks are planned
- THEN the plan MUST include separate tasks for textures/models/blockstates/lang and for loot tables/worldgen JSON/data
- AND MUST require `en_us.json` and `es_es.json`
- AND MUST prohibit using original mod assets as base/reference copies

### Requirement: Phase-Gated Verification
The plan MUST define verification checkpoints aligned with `AGENTS.md` phases and require successful builds before advancing.

#### Scenario: Phase completion gate
- GIVEN implementation proceeds in phases (bootstrap, registry, worldgen, loot, config, assets, polish)
- WHEN a phase is marked complete in tasks/progress tracking
- THEN the plan MUST require at least a phase-appropriate verification note (build/run/test command outcomes)
- AND MUST require resolution of build failures before starting the next dependent phase

### Requirement: Portability Planning for Future Versions
The plan MUST capture design considerations for later porting beyond 1.20.1 without implementing those ports now.

#### Scenario: Future upgrade readiness
- GIVEN the user intends to start on 1.20.1 and upgrade versions over time
- WHEN architecture decisions are documented
- THEN the design MUST identify likely version-sensitive areas (worldgen APIs, config APIs, data formats, registries)
- AND MUST recommend boundaries/seams to reduce rewrite cost in future ports

### Requirement: Nether Ore Generation Step Ordering
Ore generation MUST be injected in a generation step that runs after the vanilla features that create the ore's target block.

#### Scenario: Basalt and blackstone ore availability
- GIVEN `minecraft:ore` configured features can only replace blocks that already exist when their step runs
- AND `basalt_blobs`, `blackstone_blobs` and `ore_blackstone` are generated at `underground_decoration` in 1.20.1
- WHEN biome modifiers inject the mod's ores
- THEN they MUST use `underground_decoration`
- AND MUST NOT use `underground_ores`, which runs earlier and leaves basalt/blackstone targets absent
- AND the step MUST be re-validated against the target version's `GenerationStep.Decoration` order when porting

## MODIFIED Requirements

### Requirement: (none)
No existing specification is modified; this change introduces planning artifacts only.

## REMOVED Requirements

### Requirement: (none)
No existing requirement is removed by this planning change.
