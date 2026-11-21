## Verification Report

**Change**: `config-gui`
**Version**: In-Game Config Screen (Forge 1.20.1), artifacts under `openspec/changes/config-gui/`
**Mode**: Standard — inline non-delegated audit. The `sdd-verify` skill is `delegate_only` and this runtime exposes no sub-agent/`task` primitive, so this is not a formal delegated verification.
**Date**: 2026-11-21

### Completeness

| Metric | Value |
|--------|-------|
| Tasks total | 10 |
| Tasks complete by checkbox | 3 |
| Tasks incomplete by checkbox | 7 |
| Tasks with commit evidence | 10 |

All 10 work items are implemented and committed (`df0a585`, `6833ee6`, `41eb9b0`, `edb24fc`, `69bcc07`). The 7 unchecked boxes are stale task-document state, not missing implementation; each has an `Observed` entry describing the remaining manual evidence.

### Build & Tests Execution

**Build**: ✅ Passed

```text
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'; .\gradlew.bat build test --console=plain
> Task :compileJava UP-TO-DATE
> Task :compileTestJava NO-SOURCE
> Task :test NO-SOURCE
> Task :build
BUILD SUCCESSFUL in 51s
```

**Tests**: ⚠️ 0 automated tests, 0 failed, 19 spec scenarios not covered by an automated runtime test.
`test NO-SOURCE` is the project's current state; the design explicitly documented manual UI verification instead of a client test harness.

**Runtime helper probes**: ✅ Passed

```text
jshell --class-path build/classes/java/main,<mapped forge jar>,<cached dependency jars> build/verify_probe.jsh
SLIDER veins=0.0,1.0 mid=32
SLIDER y=0.0,1.0 mid=64
SLIDER roundtrip5=5
HEIGHT minEdited=1
HEIGHT maxEdited=-1
HEIGHT ordered=0
HEIGHT untouched=0
HEIGHT outOfDomainMinEdited=0
DECIMAL abc=gui.nether_ores_reborn.error.invalid_number
DECIMAL -1=gui.nether_ores_reborn.error.invalid_number
DECIMAL 100.1=gui.nether_ores_reborn.error.out_of_range
DECIMAL 50.555=gui.nether_ores_reborn.error.invalid_number
DECIMAL 0=null
DECIMAL 50.55=null
```

**Dedicated server boot**: ✅ Passed

```text
.\gradlew.bat runServer --console=plain
[Server thread/INFO] [minecraft/DedicatedServer]: Done (12.655s)! For help, type "help"
```

No `NoClassDefFoundError`, `BootstrapMethodError`, or dist-cleaner rejection. The logged Netty `Reflective setAccessible(true) disabled` and `IllegalAccessException` are pre-existing environment warnings, not change-caused failures.

**Coverage**: ➖ Not available (`test NO-SOURCE`, no coverage tooling configured).

### Spec Compliance Matrix

| Requirement | Scenario | Test | Result |
|-------------|----------|------|--------|
| Mods-list entry point | Config button availability | none found (manual checklist only) | ❌ UNTESTED |
| Mods-list entry point | Screen opens and closes | none found (manual checklist only) | ❌ UNTESTED |
| General page controls | Master switch | none found (manual checklist only) | ❌ UNTESTED |
| General page controls | Global drop multiplier | `jshell > DecimalField.validationErrorKey` | ⚠️ PARTIAL |
| Per-ore pages | Values are loaded from the effective config | none found (manual checklist only) | ❌ UNTESTED |
| Per-ore pages | Disabled ore is still editable | none found (manual checklist only) | ❌ UNTESTED |
| Integer control domains | Height range stays ordered | `jshell > NetherOresConfigScreen.heightRangeFix` | ⚠️ PARTIAL |
| Integer control domains | Zero attempts disables generation | static hint render; no runtime test | ❌ UNTESTED |
| Decimal control validation | Invalid input is rejected | `jshell > DecimalField.validationErrorKey` | ⚠️ PARTIAL |
| Unrepresentable values | Hand-edited out-of-domain value | `jshell > heightRangeFix` + static marker; file persistence untested | ⚠️ PARTIAL |
| Persistence semantics | Done writes the file | none found (manual checklist only) | ❌ UNTESTED |
| Persistence semantics | Cancel restores the snapshot | none found (manual checklist only) | ❌ UNTESTED |
| Reset to defaults | Reset then confirm | none found (manual checklist only) | ❌ UNTESTED |
| Hidden unimplemented keys | No dead control | static grep: no `veinSize` in `config/client/**` | ⚠️ PARTIAL |
| Application warnings | Singleplayer warning | static render path; no visual runtime test | ❌ UNTESTED |
| Application warnings | Remote server warning | static render path; no visual runtime test | ❌ UNTESTED |
| Localization | Language coverage | scripted key comparison across Java, `en_us`, `es_es` | ⚠️ PARTIAL |
| Side isolation | Dedicated server startup | `runServer` boot to `Done (12.655s)!` | ✅ COMPLIANT |
| Build integrity | Dependency surface | `git diff 73ea2fe..HEAD -- build.gradle mods.toml` empty + build green | ✅ COMPLIANT |

**Compliance summary**: 2/19 compliant, 6/19 partial, 11/19 untested.

### Correctness (Static Evidence)

| Requirement | Status | Notes |
|-------------|--------|-------|
| Mods-list entry point | ✅ Implemented | `ConfigScreenRegistration` registers `ConfigScreenFactory` through Forge's extension point. |
| General page controls | ✅ Implemented | Both controls render from the spec and write through `pending`. |
| Per-ore pages | ✅ Implemented | Four ore pages expose all five supported values. |
| Integer control domains | ✅ Implemented | `IntSlider` centralizes 0–64 and 0–127 mapping. |
| Decimal control validation | ✅ Implemented | `DecimalField.commit()` validates on Enter/focus loss and blocks Done and page navigation. |
| Unrepresentable values | ✅ Implemented | `IntSlider` shows a raw-value marker; `heightRangeFix` protects untouched bounds. |
| Persistence semantics | ✅ Implemented | `Done` applies `pending` and saves; `Cancel` discards without writing. |
| Reset to defaults | ✅ Implemented | `resetPage()` puts all page defaults into `pending`, including `dropMultiplier`. |
| Hidden unimplemented keys | ✅ Implemented | No `veinSize` reference in screen or widget code. |
| Application warnings | ✅ Implemented | New-chunks line always rendered; remote line gated on `getCurrentServer() != null`. |
| Localization | ✅ Implemented | 21 referenced keys covered; navigation arrows translated. |
| Side isolation | ✅ Implemented | Dedicated server booted cleanly after all changes. |
| Build integrity | ✅ Implemented | No new dependency, mixin, or access transformer; build green on JDK 17. |

### Coherence (Design)

| Decision | Followed? | Notes |
|----------|-----------|-------|
| Paged layout instead of scrolling | ✅ Yes | General plus four ore pages. |
| Pending-edits map instead of snapshot | ✅ Yes | Cancel correct by construction; out-of-domain values preserved. |
| Defaults as single source of truth | ✅ Yes | `ORE_DEFAULTS` feeds spec and Reset. |
| Client-only registration via `DistExecutor.safeRunWhenOn` | ✅ Yes | Verified by dedicated-server boot and prior bytecode inspection. |
| Validated text field for `dropMultiplier` | ✅ Yes | 2 decimals, `[0.0, 100.0]`. |
| Hide `veinSize` | ✅ Yes | Static grep clean. |
| Warnings and i18n | ✅ Yes | Both warnings and both language files present. |
| Single PR with `size:exception` | ✅ Yes | Maintainer decision recorded in `design.md` and `tasks.md`; no PR created yet. |

### Issues Found

**CRITICAL**: 11 spec scenarios have no passing runtime covering test: Config button availability, screen open/close, master switch, values loaded from file, disabled ore editability, zero-attempt hint, Done persistence, Cancel discard, Reset persistence, singleplayer warning, remote-server warning. These are coverage gaps, not observed defects.

**WARNING**: 7 of 10 task checkboxes are stale relative to commit evidence. `docs/MANUAL_TESTING.md` contains the checklist that would close the 11 critical coverage gaps, but it has not been executed. The change is a single PR under an accepted `size:exception`.

**SUGGESTION**: Add a small JVM test source set for the pure helpers (`IntSlider`, `heightRangeFix`, `DecimalField.validationErrorKey`) so future ports do not depend only on ad-hoc `jshell` probes.

### Verdict

**FAIL** — coverage gate unmet: no automated runtime evidence exists for 11 UI scenarios and the manual checklist was not executed. No change-caused functional defect was observed; build, helper probes, i18n coverage, and dedicated-server boot all passed.
