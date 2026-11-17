# Proposal: Nether Ores Reborn Baseline Plan (MC 1.20.1)

## Intent

Establish a detailed, phase-driven implementation plan for rebuilding **Nether Ores Reborn** from scratch on **Minecraft 1.20.1 + Forge 47.4.28 (Java 17)**, with original code/assets and a clean-room approach inspired conceptually by the abandoned "Nether Ores Plus+" mod. This plan provides a controlled baseline before progressive porting to newer Minecraft versions.

## Scope

### In scope (baseline 1.20.1)
- Project bootstrap from official Forge 1.20.1 MDK (no MCreator)
- Core mod scaffolding (mod id, package, main class, registries, creative tab)
- Nether ore block families for base materials:
  - Netherrack-based ore variants
  - Basalt-based ore variants
  - Blackstone-based ore variants
- World generation in Nether (configurable placement/rarity/height)
- Data-driven loot tables per ore block (JSON), aligned with configurable behavior where applicable
- Configuration file `config/nether-ores-reborn.toml` for enable/disable, spawn parameters, and drop multipliers
- Original assets and data: blockstates, models, textures, lang (`en_us`, `es_es`)
- Verification/build checkpoints per phase (`./gradlew build`, client/server run checks)

### Out of scope (for this baseline plan execution wave)
- Publishing to CurseForge/Modrinth/GitHub releases
- Copying or extracting any code/assets/data from the original mod/JAR
- Multi-loader abstraction implementation (Fabric/NeoForge common modules) beyond documenting future porting strategy
- Advanced integrations beyond optional tags/config UI hooks unless explicitly approved later
- Port implementation to >1.20.1 (only planning/portability considerations included)

## Motivation

- The original mod is unmaintained and MCreator-based; a clean modern reimplementation improves maintainability and legal safety.
- Starting at 1.20.1 provides a stable baseline matching the original mod's era while enabling incremental upgrades.
- A detailed plan is needed to control scope, preserve clean-room compliance, and ensure each phase is buildable/verifiable.

## Success Criteria

- A complete, actionable SDD artifact set exists (proposal/specs/design/tasks) aligned with `AGENTS.md`
- Plan explicitly covers phases 0–7 from `AGENTS.md` with concrete tasks, dependencies, and verification gates
- Clean-room/legal constraints and `/reference/` + `/recursos/` handling are embedded in requirements and tasks
- Pending decisions are clearly identified (mod id/package, exact ore list/drops, config UX choices)

## Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Accidental reuse of original code/assets/names | Legal/compliance issue | Enforce clean-room checklist in specs/tasks; keep `/reference/` local-only; review naming/assets before commits/releases |
| Scope creep (extra ores/mechanics) | Delays baseline delivery | Lock baseline to AGENTS scope; require explicit approval for additions |
| Forge 1.20.1 worldgen/config API complexity | Implementation churn | Plan small verifiable increments (registry -> datagen/data -> worldgen -> config wiring) and compile/run checks each phase |
| Underspecified drops/ore variants | Rework in data/assets | Add a decision task early (after reference analysis notes) to finalize ore matrix before assets/loot implementation |
| Portability debt for future upgrades | Harder migrations | Capture version seams in design (registry/worldgen/config/data layers), avoid unnecessary 1.20.1-specific coupling |

## Dependencies / Preconditions

- Java 17 installed and selected for Gradle/IDE
- Access to official Forge 1.20.1 MDK (downloaded during bootstrap, not from `/recursos/` installer extraction assumptions)
- Local reference materials remain in ignored folders (`/reference/`, `/recursos/`)
- User decisions still needed:
  - Root package / author namespace (e.g. `com.<user>.netheroresreborn`)
  - Mod id confirmation (`nether_ores_reborn` recommended)
  - Final baseline ore list and drop behavior (after functional reference analysis)

## Rollback Considerations

- Planning-only change: rollback is trivial (revert `openspec/` artifacts and Engram entries if needed)
- During implementation phases, keep commits/work units phase-scoped so a failed experiment (e.g. worldgen approach) can be reverted without discarding registry/assets work
- Preserve data-driven JSON (loot/worldgen/config schema) to simplify regeneration/iteration

## Non-Goals (Important)

- No MCreator workflows, no copying decompiled code, no texture extraction
- No guarantee that gameplay balance matches the original mod exactly; baseline targets conceptual parity with original, compliant implementation
