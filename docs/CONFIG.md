# Configuration (`nether-ores-reborn.toml`)

Nether Ores Reborn uses a Forge **COMMON** config file generated at:

- `config/nether-ores-reborn.toml`

This document reflects the baseline implementation for **Minecraft 1.20.1 / Forge 47.4.28**.

## `[general]`

| Key | Type | Default | Range / Notes | Current behavior |
| --- | --- | --- | --- | --- |
| `enableGeneration` | boolean | `true` | Master switch for ore generation | Applied to worldgen via custom placement modifiers (disables mod ore placement when `false`) |
| `dropMultiplier` | double | `1.0` | `0.0` to `100.0` | Applied via Global Loot Modifier (global multiplier). `0` disables resource drops from affected ore loot; values between `0` and `1` scale down resource drops probabilistically; values above `1` scale up resource drops. Silk Touch/self-drops of the ore block are not multiplied. |

## `[ores.<ore_id>]`

Approved baseline ore IDs:

- `basalt_coal_ore`
- `basalt_diamond_ore`
- `blackstone_iron_ore`
- `netherrack_iron_ore`

| Key | Type | Default(s) | Range / Notes | Current behavior |
| --- | --- | --- | --- | --- |
| `enabled` | boolean | `true` (all) | Enables/disables this ore variant | Applied to worldgen via custom placement modifiers |
| `veinsPerChunk` | int | `basalt_coal_ore=24`, `basalt_diamond_ore=8`, `blackstone_iron_ore=16`, `netherrack_iron_ore=24` | `0` to `64` | Applied to worldgen via `nether_ores_reborn:config_count` in `placed_feature` JSON |
| `veinSize` | int | `basalt_coal_ore=12`, `basalt_diamond_ore=6`, `blackstone_iron_ore=10`, `netherrack_iron_ore=10` | `0` to `64` | **Configured in schema but not yet applied to generation** (configured features currently use static JSON sizes) |
| `minY` | int | `5` (all) | `-64` to `320` | Applied to worldgen via `nether_ores_reborn:config_uniform_height_range` in `placed_feature` JSON |
| `maxY` | int | `118` (all) | `-64` to `320` | Applied to worldgen via `nether_ores_reborn:config_uniform_height_range` in `placed_feature` JSON |
| `dropMultiplier` | double | `1.0` (all) | `0.0` to `100.0` | Applied via Global Loot Modifier (combined with global multiplier). Affects resource drops only; Silk Touch/self-drops of the ore block are not multiplied. |

## Notes and current limitations

- **Runtime validation pending:** config file generation/loading and in-game effects (generation/drops) still need validation in a dev client/world run.
- **Generation wiring (Phase 1):** `placed_feature` JSON files now use custom placement modifiers for config-aware generation toggles, count, and height range. `configured_feature` vein sizes remain static in JSON.
- **`veinSize` limitation:** the config key/default exists, but the ore size used by generation is still defined statically in `data/nether_ores_reborn/worldgen/configured_feature/*.json`.

## Related implementation files

- `src/main/java/com/rexthar/netheroresreborn/config/NetherOresRebornConfig.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigCountPlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigUniformHeightRangePlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/loot/OreDropMultiplierModifier.java`
- `src/main/resources/data/nether_ores_reborn/worldgen/`
- `src/main/resources/data/nether_ores_reborn/loot_modifiers/`
