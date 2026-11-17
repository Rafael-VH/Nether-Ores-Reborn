# Configuration (`nether-ores-reborn.toml`)

Nether Ores Reborn uses a Forge **COMMON** config file generated at:

- `config/nether-ores-reborn.toml`

This document reflects the baseline implementation for **Minecraft 1.20.1 / Forge 47.4.28**.

## `[general]`

| Key | Type | Default | Range / Notes | Current behavior |
| --- | --- | --- | --- | --- |
| `enableGeneration` | boolean | `true` | Master switch for ore generation | Applied to worldgen (disables all mod ore placement when `false`) |
| `dropMultiplier` | double | `1.0` | `0.0` to `100.0` | Applied via Global Loot Modifier (global multiplier) |

## `[ores.<ore_id>]`

Approved baseline ore IDs:

- `basalt_coal_ore`
- `basalt_diamond_ore`
- `blackstone_iron_ore`
- `netherrack_iron_ore`

| Key | Type | Default(s) | Range / Notes | Current behavior |
| --- | --- | --- | --- | --- |
| `enabled` | boolean | `true` (all) | Enables/disables this ore variant | Applied to worldgen |
| `veinsPerChunk` | int | `basalt_coal_ore=8`, `basalt_diamond_ore=2`, `blackstone_iron_ore=6`, `netherrack_iron_ore=6` | `0` to `64` | Applied to worldgen via custom placement modifier |
| `veinSize` | int | `basalt_coal_ore=12`, `basalt_diamond_ore=6`, `blackstone_iron_ore=10`, `netherrack_iron_ore=10` | `0` to `64` | **Configured in schema but not yet applied to generation** (configured features currently use static JSON sizes) |
| `minY` | int | `5` (all) | `-64` to `320` | Applied to worldgen via custom placement modifier |
| `maxY` | int | `118` (all) | `-64` to `320` | Applied to worldgen via custom placement modifier |
| `dropMultiplier` | double | `1.0` (all) | `0.0` to `100.0` | Applied via Global Loot Modifier (combined with global multiplier) |

## Notes and current limitations

- **Runtime validation pending:** config file generation/loading and in-game effects (generation/drops) still need validation in a dev client/world run.
- **`veinSize` limitation:** the config key/default exists, but the ore size used by generation is still defined statically in `data/nether_ores_reborn/worldgen/configured_feature/*.json`.
- **Data-driven baseline:** current worldgen uses data files plus custom placement modifiers for config-aware count/height/toggles.

## Related implementation files

- `src/main/java/com/rexthar/netheroresreborn/config/NetherOresRebornConfig.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigCountPlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigUniformHeightRangePlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/loot/OreDropMultiplierModifier.java`
- `src/main/resources/data/nether_ores_reborn/worldgen/`
- `src/main/resources/data/nether_ores_reborn/loot_modifiers/`
