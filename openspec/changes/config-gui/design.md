# Design: In-Game Config Screen (Forge 1.20.1)

## 1. Architecture overview

Two layers, strictly separated:

| Layer | Package | Loaded on server |
| :--- | :--- | :--- |
| Config model + defaults | `com.rexthar.netheroresreborn.config` | yes |
| Screen, widgets, registration | `com.rexthar.netheroresreborn.config.client` | **no** |

The mod constructor registers the screen through a `DistExecutor.safeRunWhenOn` supplier, so no
client class is referenced from a code path a dedicated server executes.

```
NetherOresReborn (constructor)
└── DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ConfigScreenRegistration::register)
    └── ModLoadingContext#registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ...)
        └── NetherOresConfigScreen(parent = ModListScreen)
```

## 2. Config model refactor (work unit 1)

Current defaults are inline constructor arguments, so a Reset feature would duplicate them.
Extract a single source of truth without changing any built value:

```java
public record OreDefaults(int veinsPerChunk, int veinSize, int minY, int maxY, double dropMultiplier) {}

public static final boolean DEFAULT_ENABLE_GENERATION = true;
public static final double DEFAULT_GLOBAL_DROP_MULTIPLIER = 1.0D;
public static final Map<String, OreDefaults> ORE_DEFAULTS = Map.of(
        "basalt_coal_ore",     new OreDefaults(24, 12,  5, 118, 1.0D),
        "basalt_diamond_ore",  new OreDefaults( 8,  6,  5, 118, 1.0D),
        "blackstone_iron_ore", new OreDefaults(48, 10,  5,  32, 1.0D),
        "netherrack_iron_ore", new OreDefaults(24, 10,  5, 118, 1.0D));
```

`OreSettings` keeps its public fields and is constructed from `ORE_DEFAULTS.get(name)`; the generated
`.toml` must be byte-identical before and after this refactor (verification for unit 1).

`OreSettings` also gains read helpers used by the screen (`defaults()`, `displayNameKey()`), so the
screen never hardcodes a key string.

## 3. Screen composition

### Pages

`ConfigPage` enum: `GENERAL`, `NETHERRACK_IRON_ORE`, `BLACKSTONE_IRON_ORE`, `BASALT_COAL_ORE`,
`BASALT_DIAMOND_ORE`. `init()` renders **only** the active page plus a fixed footer:

```
[ < ]  Page title                       [ > ]
  row 0: <label>  <control>
  row 1: <label>  <control>
  ...
[ Reset page ]      [ Cancel ]  [ Done ]
```

Paging wins over a custom scroll container: 5 rows per page fit without scrolling, and a hand-written
scroll list is the most defect-prone widget in a screen like this.

### Control mapping

| Config value | Control | Domain |
| :--- | :--- | :--- |
| `enabled`, `enableGeneration` | `CycleButton.onOff` | boolean |
| `veinsPerChunk` | `IntSlider` | 0–64, step 1 |
| `minY`, `maxY` | `IntSlider` | 0–127 (Nether), step 1 |
| `dropMultiplier` | validated `EditBox` | 0.0–100.0, 2 decimals |

`dropMultiplier` cannot be a 0–10 slider: the schema allows up to 100, and a slider that cannot
represent a legal value forces either silent truncation or an inconsistent UI.

### Pending-edits model

Edits are **not** applied to the spec while the screen is open:

```java
private final Map<ForgeConfigSpec.ConfigValue<?>, Object> pending = new LinkedHashMap<>();
```

- A control reads `pending.getOrDefault(value, value.get())`.
- `Done` applies `pending` (`value.set(v)`), calls `SPEC.save()`, then closes.
- `Cancel` discards `pending` and closes; nothing was ever written, so the file cannot change.
- `Reset page` writes defaults for that page's keys into `pending` only.

Consequences: Cancel is correct by construction, and a hand-edited out-of-domain value is never
overwritten because only keys the player actually touched enter `pending`. This replaces a
snapshot/restore design, which needs a second map and a restore path that can itself fail.

### Out-of-domain values

A control whose loaded value is outside its UI domain renders the raw value with an explicit
`(!)` marker and does not enter `pending` until the player moves that control.

### Height-range ordering

`minY`/`maxY` adjust the opposite bound on commit (pushing `minY` above `maxY` raises `maxY`), so the
persisted file can never contain an inverted range.

## 4. Widgets

Three small reusable widgets keep the screen readable:

- `IntSlider extends AbstractSliderButton` — formats the integer label and exposes `getIntValue()`;
  includes the static mapping helpers `toSlider(int, min, max)` / `fromSlider(double, min, max)` in one
  place so the off-by-one lives in exactly one spot.
- `DecimalField extends EditBox` — `setFilter` restricts characters, `commit()` parses and validates on
  Enter and on focus loss, and exposes `isValid()`.
- Toggle rows use `CycleButton.onOffBuilder(Component)` with the localized label.

## 5. Side isolation and registration

```java
// ConfigScreenRegistration: no client types in this class file
public static void register() {
    ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigScreenFactory::create));
}

// ConfigScreenFactory: client-only, loaded only when Forge calls the extension point
public static Screen create(Minecraft minecraft, Screen parent) {
    return new NetherOresConfigScreen(parent);
}
```

Called from the mod constructor with `DistExecutor.safeRunWhenOn(Dist.CLIENT,
() -> ConfigScreenRegistration::register)`.

Two constraints were confirmed against Forge 47.4.28 by a real `runServer` boot:

1. `validateSafeReferent` resolves the supplier on **both** sides. The registered method must be a
   no-argument static method in a **different class** from the one that captures it. A nested lambda
   or a bound method reference implemented in `NetherOresReborn` fails with "Unsafe Referent usage
   found in safe referent method".
2. The registration class itself must contain no client types. An inline `(minecraft, parent) -> ...`
   lambda makes javac emit a synthetic method with `Screen` in its signature, which the runtime dist
   cleaner rejects on a dedicated server. Moving that lambda to `ConfigScreenFactory` keeps
   `ConfigScreenRegistration` server-loadable.

`ModLoadingContext.get()` is deprecated in 47.4.28 but the safe-referent pattern requires a
no-argument static method, so the deprecation is suppressed locally with an explanatory comment.

## 6. Warnings and i18n

- Permanent line: changes apply to newly generated chunks only.
- Extra line when `Minecraft.getInstance().getCurrentServer() != null`: the edit affects the local
  client copy, not the server.
- All strings via `Component.translatable("gui.nether_ores_reborn.…")`; keys added to `en_us` and
  `es_es` in the same work unit as the control that uses them.

## 7. Error handling

| Situation | Behavior |
| :--- | :--- |
| Spec not loaded when the screen is built | Render an error line, disable all controls, keep Done/Cancel working |
| Invalid decimal typed | Field marked invalid, `Done` blocked while any invalid field exists |
| Value out of UI domain | Raw value shown with marker, never written unless edited |

## 8. Porting seams (future versions)

Registration and widget construction are the only Forge-1.20.1-specific pieces. NeoForge 1.20.2+
replaces the extension point with `IConfigScreenFactory`; porting swaps
`ConfigScreenRegistration` and leaves the model, pages, and pending-edits logic untouched.

## 9. Files and size estimate

| File | Change | Est. lines |
| :--- | :--- | ---: |
| `config/NetherOresRebornConfig.java` | refactor: defaults as records/map | ~+40 |
| `config/client/ConfigScreenRegistration.java` | new | ~25 |
| `config/client/NetherOresConfigScreen.java` | new | ~320 |
| `config/client/widgets/IntSlider.java` | new | ~70 |
| `config/client/widgets/DecimalField.java` | new | ~80 |
| `lang/en_us.json`, `lang/es_es.json` | keys | ~60 |
| docs (`CONFIG.md`, `MANUAL_TESTING.md`) | updates | ~40 |

Total ≈ 635 authored lines → under the 800-line review budget, single PR.

## 10. Verification strategy

No automated UI tests exist in the project and adding a client test harness is out of scope.

- Unit 1: build + regenerate `.toml` and diff against the previous file (must be identical).
- Units 2–4: `./gradlew build` plus the manual checklist added to `MANUAL_TESTING.md`
  (button enabled, values match file, Done/Cancel/Reset, remote warning, dedicated server boots).
- Static check for side isolation: `runServer` starts without a `NoClassDefFoundError`;
  `grep` confirms no `client.*` import in `NetherOresReborn` outside the deferred supplier.

## 11. Alternatives rejected

| Alternative | Why not |
| :--- | :--- |
| Scrollable single page | Custom scroll container is the riskiest widget here; paging needs no new code |
| Snapshot/restore for Cancel | Pending-edits is simpler and cannot lose an out-of-domain value |
| `dropMultiplier` slider 0–10 | Cannot represent legal values up to 100 |
| Configured / Cloth Config | Adds a dependency and still needs the same registration hook |
