# Delta for Config Screen

## ADDED Requirements

### Requirement: Mods-list entry point
The mod MUST expose its configuration through Forge's Mods list Config button, and the screen MUST be reachable only for this mod.

#### Scenario: Config button availability
- GIVEN the client is running with the mod loaded
- WHEN the player opens Forge's Mods list and selects "Nether Ores Reborn"
- THEN the Config button MUST be enabled
- AND selecting any other mod MUST NOT enable the button for this mod's extension point

#### Scenario: Screen opens and closes
- GIVEN the Config button is enabled for this mod
- WHEN the player clicks it
- THEN the config screen MUST open
- AND pressing Done or Cancel MUST return to the Mods list screen that opened it

### Requirement: General page controls
The General page MUST expose `general.enableGeneration` and `general.dropMultiplier`.

#### Scenario: Master switch
- GIVEN the General page is displayed
- WHEN the player toggles `enableGeneration` off and confirms
- THEN the value `false` MUST be persisted to `config/nether-ores-reborn.toml`
- AND newly generated chunks MUST contain no mod ores while it stays off

#### Scenario: Global drop multiplier
- GIVEN the General page is displayed
- WHEN the player enters a value within `[0.0, 100.0]` in the `dropMultiplier` field and confirms
- THEN that value MUST be persisted as `general.dropMultiplier`

### Requirement: Per-ore pages
Each of the four ore variants (`netherrack_iron_ore`, `blackstone_iron_ore`, `basalt_coal_ore`, `basalt_diamond_ore`) MUST have its own page exposing `enabled`, `veinsPerChunk`, `minY`, `maxY`, and `dropMultiplier`.

#### Scenario: Values are loaded from the effective config
- GIVEN a `config/nether-ores-reborn.toml` with non-default values for an ore
- WHEN the player opens that ore's page
- THEN every control MUST display the loaded effective value, not the built-in default

#### Scenario: Disabled ore is still editable
- GIVEN `ores.blackstone_iron_ore.enabled` is `false`
- WHEN the player opens the `blackstone_iron_ore` page
- THEN the remaining controls for that ore MUST remain editable
- AND re-enabling the ore MUST NOT require editing the file by hand

### Requirement: Integer control domains
Integer controls MUST enforce the canonical config domains: `veinsPerChunk` in `[0, 64]`, `minY` and `maxY` in the Nether range `[0, 127]`.

#### Scenario: Height range stays ordered
- GIVEN the player sets `minY` above the current `maxY` on a page
- WHEN the value is committed
- THEN the screen MUST keep the range ordered by adjusting the other bound
- AND the persisted file MUST NOT contain `minY` greater than `maxY`

#### Scenario: Zero attempts disables generation
- GIVEN the player sets `veinsPerChunk` to `0`
- WHEN the value is committed
- THEN the value MUST persist as `0`
- AND the screen MUST indicate that this disables generation for that ore

### Requirement: Decimal control validation
`dropMultiplier` fields MUST accept any value in `[0.0, 100.0]` and MUST reject invalid input without writing it.

#### Scenario: Invalid input is rejected
- GIVEN a `dropMultiplier` field
- WHEN the player types a non-numeric value or a value outside `[0.0, 100.0]`
- THEN the field MUST show an invalid state
- AND confirming MUST NOT persist the invalid value

### Requirement: Unrepresentable values are never silently overwritten
If the loaded config contains a value outside a control's UI domain, the screen MUST surface that state and MUST NOT overwrite the value unless the player edits that specific control.

#### Scenario: Hand-edited out-of-domain value
- GIVEN `ores.basalt_coal_ore.minY` is `-64` in the file (outside the Nether UI domain)
- WHEN the player opens that page and confirms without touching `minY`
- THEN the file MUST still contain `-64`

### Requirement: Persistence semantics
Done MUST persist the edited values; Cancel MUST discard them.

#### Scenario: Done writes the file
- GIVEN the player changed one or more controls
- WHEN the player presses Done
- THEN all changed values MUST be applied to the `ForgeConfigSpec`
- AND the spec MUST be saved to `config/nether-ores-reborn.toml`
- AND reopening the screen MUST show the saved values

#### Scenario: Cancel restores the snapshot
- GIVEN the player changed one or more controls
- WHEN the player presses Cancel
- THEN every control MUST return to the value captured when the screen opened
- AND the file MUST remain unchanged

### Requirement: Reset to defaults
The screen MUST offer a Reset control that restores the built-in defaults for the current page.

#### Scenario: Reset then confirm
- GIVEN the player modified values on an ore page
- WHEN the player presses Reset and then Done
- THEN every control on that page MUST hold its documented default
- AND the persisted file MUST match those defaults

### Requirement: Hidden unimplemented keys
`veinSize` MUST NOT be exposed by the screen while it is not applied to world generation.

#### Scenario: No dead control
- GIVEN the config schema still declares `veinSize`
- WHEN the player browses every page
- THEN no control for `veinSize` MUST be rendered

### Requirement: Application warnings
The screen MUST state that changes affect only newly generated chunks, and MUST warn when editing a copy that does not affect the server.

#### Scenario: Singleplayer warning
- GIVEN the player is in a singleplayer world
- WHEN the screen is open
- THEN the new-chunks warning MUST be visible

#### Scenario: Remote server warning
- GIVEN the client is connected to a dedicated server
- WHEN the screen is open
- THEN the screen MUST warn that the edit only affects the local client copy

### Requirement: Localization
All user-facing strings MUST come from language files, available in `en_us` and `es_es`.

#### Scenario: Language coverage
- GIVEN the game language is English or Spanish
- WHEN the screen is rendered
- THEN every label, tooltip, button, and warning MUST be localized
- AND no literal user-facing string MUST be hardcoded in Java

### Requirement: Side isolation
The config screen and its registration MUST NOT be loaded on a dedicated server.

#### Scenario: Dedicated server startup
- GIVEN a dedicated server starts with the mod installed
- WHEN the mod constructs and registers extension points
- THEN no client-only class (`ConfigScreenHandler`, `Screen` subclasses) MUST be loaded
- AND the server MUST start without error

### Requirement: Build integrity
The change MUST NOT add new runtime dependencies, mixins, or access transformers.

#### Scenario: Dependency surface
- GIVEN the change is reviewed
- WHEN `build.gradle` and `mods.toml` are inspected
- THEN no new dependency or mixin configuration MUST be present
- AND `./gradlew build` MUST succeed with JDK 17
