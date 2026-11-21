# Tasks: In-Game Config Screen (Forge 1.20.1)

Change: `config-gui` · Artifacts: `openspec/changes/config-gui/` · Delivery: single PR, 5 work-unit commits

## Sequencing

```
1 (defaults) ──► 2 (shell + General) ──► 3 (ore pages + IntSlider) ──┬─► 4 (decimal + reset)
                                                                     └─► 5 (i18n + warnings + docs)
```

Each unit ends with `./gradlew build` green and its own commit. Units 2–5 also require the manual
checks listed under them (there is no UI test harness in this project).

## Work units

### 1. Extract config defaults into a single source of truth

- [ ] 1.1 Add `OreDefaults` record and `ORE_DEFAULTS` map; add `DEFAULT_ENABLE_GENERATION` and
      `DEFAULT_GLOBAL_DROP_MULTIPLIER`
  - **Outcome:** `NetherOresRebornConfig` builds the spec from the records; `OreSettings` exposes
    `defaults()`; no value changes
  - **Verify:** all 22 defaults match the pre-refactor constructor arguments one by one; `javap -c`
    on the compiled class still shows `bipush 48` / `bipush 32` for `blackstone_iron_ore`
  - **Rollback boundary:** single commit, no behavior change

### 2. Screen shell, registration and General page

- [ ] 2.1 Add `ConfigScreenRegistration` (client-only) and call it from the mod constructor with
      `DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ConfigScreenRegistration::register)`
  - **Depends on:** 1.1
  - **Outcome:** Config button enabled in the Mods list for this mod only
  - **Verify:** `runClient`, select the mod, button enabled; select another mod, button disabled;
    `runServer` starts without `NoClassDefFoundError`
- [ ] 2.2 Add `NetherOresConfigScreen` with page enum, footer (`Prev`/`Next`/`Reset`/`Cancel`/`Done`),
      pending-edits map, and the General page (`enableGeneration`, `dropMultiplier` as a temporary
      text row)
  - **Depends on:** 2.1
  - **Outcome:** screen opens; Done applies + `SPEC.save()`; Cancel discards and the file is untouched;
    reopening shows the saved values
  - **Verify:** `runClient`: change `enableGeneration`, Done, inspect `config/nether-ores-reborn.toml`;
    repeat with Cancel and confirm the file is unchanged
  - **Rollback boundary:** removing the registration hides the button; TOML workflow unaffected

### 3. Per-ore pages and integer sliders

- [ ] 3.1 Add `IntSlider` with centralized `toSlider`/`fromSlider` mapping and integer label
  - **Depends on:** 2.2
  - **Verify:** slider endpoints produce exactly `0`/`64` and `0`/`127`; mid values round correctly
- [ ] 3.2 Add the four ore pages with `enabled`, `veinsPerChunk`, `minY`, `maxY`
  - **Depends on:** 3.1
  - **Outcome:** each page loads the effective values from the spec; `minY > maxY` adjusts the other
    bound on commit; `veinsPerChunk = 0` shows the "generation disabled" hint; `veinSize` is not
    rendered
  - **Verify:** seed a `.toml` with non-default values, open every page and compare against the file;
    set an inverted range and confirm the saved file is ordered

### 4. Decimal fields, Reset and out-of-domain handling

- [ ] 4.1 Add `DecimalField` (character filter, validation on Enter and focus loss, `isValid()`), use it
      for both `dropMultiplier` scopes, and block Done while any field is invalid
  - **Depends on:** 3.2
  - **Verify:** `abc`, `-1`, `100.1` and `50.55` are rejected; `0`, `50`, `100` are accepted
- [ ] 4.2 Add per-page Reset (defaults into `pending` only) and the out-of-domain marker that keeps the
      raw value untouched until the player edits that control
  - **Depends on:** 4.1
  - **Outcome:** Reset + Done persists documented defaults; a hand-edited `minY = -64` survives an
    unrelated Done
  - **Verify:** manual checks against `ORE_DEFAULTS`; file diff before/after confirming the untouched
    value

### 5. Warnings, i18n and documentation

- [ ] 5.1 Add the new-chunks warning and the remote-server warning
  - **Depends on:** 3.2
  - **Verify:** singleplayer shows one warning; a remote connection shows both
- [ ] 5.2 Add `gui.nether_ores_reborn.*` keys to `en_us` and `es_es`; remove every hardcoded label
  - **Depends on:** 5.1
  - **Verify:** switch language in game; no untranslated key (`gui.…`) is displayed
- [ ] 5.3 Update `docs/CONFIG.md` (in-game editing) and `docs/MANUAL_TESTING.md` (manual checklist)
  - **Depends on:** 5.2
  - **Verify:** checklist covers button, pages, Done/Cancel/Reset, warnings, side isolation, build

## Test-first policy

No test framework or runnable deterministic UI harness exists in this project, so no RED can be
observed for screen behavior. The applicable checks are `./gradlew build` plus the manual checklist
per unit; unit 1 is covered by value-by-value comparison and bytecode inspection. This exception is
recorded here rather than silently skipped.

## Review Workload Forecast

| Item | Value |
| :--- | :--- |
| Estimated authored changed lines | ~670 |
| Largest single commit | ~200 lines |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Decision needed before apply | No |
| Delivery strategy | `auto-forecast` → single PR |
| Review budget (session) | 800 lines |

Each work unit is independently revertible; the branch accumulates 5 commits, none above 400 authored
lines.

## Out of scope (tracked, not planned here)

- Wiring `veinSize` into worldgen
- Server-side config sync or commands
- Config sync between client and dedicated server
