# Configuration (`nether-ores-reborn.toml`)

Nether Ores Reborn uses a Forge **COMMON** config file generated at:

- `config/nether-ores-reborn.toml`

This document reflects the current implementation for **Minecraft 1.20.1 / Forge 47.4.28** (mod version `0.1.2-beta`).

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
| `veinsPerChunk` | int | `basalt_coal_ore=24`, `basalt_diamond_ore=8`, `blackstone_iron_ore=48`, `netherrack_iron_ore=24` | `0` to `64` | Applied to worldgen via `nether_ores_reborn:config_count` in `placed_feature` JSON |
| `veinSize` | int | `basalt_coal_ore=12`, `basalt_diamond_ore=6`, `blackstone_iron_ore=10`, `netherrack_iron_ore=10` | `0` to `64` | **Configured in schema but not yet applied to generation** (configured features currently use static JSON sizes) |
| `minY` | int | `5` (all) | `-64` to `320` | Applied to worldgen via `nether_ores_reborn:config_uniform_height_range` in `placed_feature` JSON |
| `maxY` | int | `118` (all except `blackstone_iron_ore=32`) | `-64` to `320` | Applied to worldgen via `nether_ores_reborn:config_uniform_height_range` in `placed_feature` JSON |
| `dropMultiplier` | double | `1.0` (all) | `0.0` to `100.0` | Applied via Global Loot Modifier (combined with global multiplier). Affects resource drops only; Silk Touch/self-drops of the ore block are not multiplied. |

## Generation wiring

- Each ore is defined as a `configured_feature` (`minecraft:ore` with a `minecraft:block_match` target) plus a `placed_feature` using `nether_ores_reborn:config_count` and `nether_ores_reborn:config_uniform_height_range`, so `enableGeneration`, `enabled`, `veinsPerChunk`, `minY` and `maxY` are read at world generation time.
- Biome integration uses `forge:add_features` biome modifiers (`biome_modifier/*.json`) targeting `#minecraft:is_nether`.
- **Generation step:** the biome modifiers inject at `underground_decoration`, the same step vanilla uses for its Nether ores (`ore_quartz_nether`, `ore_gold_nether`, `ore_blackstone`). This is required because `basalt_blobs`, `blackstone_blobs` and `ore_blackstone` are generated in that same step; running earlier would attempt to replace basalt/blackstone that does not exist yet.
- Forge appends biome modifier features after the vanilla features of the same step, so the ores run after the blobs that create their target blocks.

## Where each ore can appear

Each ore replaces an existing base block, so its availability depends on the terrain:

| Ore | Availability |
| --- | --- |
| `basalt_coal_ore` | Basalt Deltas only (the only Nether biome that generates basalt) |
| `basalt_diamond_ore` | Basalt Deltas only |
| `blackstone_iron_ore` | Every Nether biome between Y 5 and 32, where vanilla generates blackstone (`ore_blackstone` outside Basalt Deltas, `blackstone_blobs` inside them) |
| `netherrack_iron_ore` | All Nether biomes |

## Notes and current limitations

- **`veinSize` limitation:** the config key/default exists, but the ore size used by generation is still defined statically in `data/nether_ores_reborn/worldgen/configured_feature/*.json`.
- **Existing worlds:** config changes only affect chunks that have not been generated yet. Exploring new chunks or creating a new world is required to observe differences.
- **Upgrading from an older version:** an existing `nether-ores-reborn.toml` keeps its previous values and overrides new defaults. Delete the file to regenerate defaults or adjust the values manually.
- **Density expectations:** `veinsPerChunk` is the number of placement attempts, not the number of vein blocks. The effective amount of ore also depends on how much of the target block exists at that height in that biome.
- **`blackstone_iron_ore` height range:** its default `maxY` is `32` on purpose. Outside Basalt Deltas, vanilla blackstone only exists between Y 5 and 31 (`ore_blackstone`, 2 blobs of size 33 per chunk), so sampling above that height wastes almost every attempt and the ore becomes effectively invisible. In Basalt Deltas blackstone also exists low, so the restricted range keeps it visible there too.

## Related implementation files

- `src/main/java/com/rexthar/netheroresreborn/config/NetherOresRebornConfig.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigCountPlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/worldgen/placement/ConfigUniformHeightRangePlacement.java`
- `src/main/java/com/rexthar/netheroresreborn/loot/OreDropMultiplierModifier.java`
- `src/main/resources/data/nether_ores_reborn/worldgen/`
- `src/main/resources/data/nether_ores_reborn/forge/biome_modifier/`
- `src/main/resources/data/nether_ores_reborn/loot_modifiers/`
