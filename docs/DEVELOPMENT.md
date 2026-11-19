# Desarrollo (información técnica)

Este documento reúne información técnica para desarrollo y mantenimiento del proyecto. Para una descripción general orientada a usuarios, consulta el [`README.md`](../README.md).

## Baseline técnico

- **Minecraft:** 1.20.1
- **Forge:** 47.4.28
- **Java:** 17 (requerido/recomendado para compilar este baseline)
- **Build system:** ForgeGradle (MDK oficial)
- **Mod ID:** `nether_ores_reborn`
- **Paquete raíz:** `com.rexthar.netheroresreborn`
- **Nombre visible:** Nether Ores Reborn
- **Versión actual:** `0.1.2-beta` (primera versión publicada: `0.1.0`)

## Estructura y componentes implementados

- Registros: bloques, BlockItems y pestaña creativa
- Worldgen (data-driven + placements config-aware): `configured_feature`, `placed_feature`, `forge/biome_modifier`
- Placement modifiers custom: `nether_ores_reborn:config_count` y `nether_ores_reborn:config_uniform_height_range`
- Loot tables por bloque
- Configuración COMMON (`nether-ores-reborn.toml`)
- Global Loot Modifier para multiplicadores de drops
- Assets originales (texturas/modelos/blockstates) y localización `en_us` / `es_es`

## Matriz aprobada (baseline)

| ID de bloque | Base | Drop base |
| --- | --- | --- |
| `basalt_coal_ore` | Basalt | Coal |
| `basalt_diamond_ore` | Basalt | Diamond |
| `blackstone_iron_ore` | Blackstone | Raw Iron |
| `netherrack_iron_ore` | Netherrack | Raw Iron |

## Pipeline de worldgen (piezas y orden de ejecución)

La generación se compone de tres piezas de datos:

1. **`data/nether_ores_reborn/worldgen/configured_feature/<ore>.json`** — tipo `minecraft:ore`, con `targets` que usan `minecraft:block_match` para indicar qué bloque reemplazar (basalt, blackstone o netherrack) y el `state` de la mena a colocar. El `size` de la vena está fijo acá.
2. **`data/nether_ores_reborn/worldgen/placed_feature/<ore>.json`** — referencia al configured feature y aplica la cadena `config_count` → `in_square` → `config_uniform_height_range` → `biome`.
3. **`data/nether_ores_reborn/forge/biome_modifier/add_<ore>.json`** — `forge:add_features` sobre `#minecraft:is_nether` en el step `underground_decoration`.

### Regla crítica: el step debe correr después de los bloques objetivo

Minecraft ejecuta la generación en 11 fases secuenciales (`GenerationStep.Decoration`):

```
0 RAW_GENERATION        6 UNDERGROUND_ORES
1 LAKES                 7 UNDERGROUND_DECORATION
2 LOCAL_MODIFICATIONS   8 FLUID_SPRINGS
3 UNDERGROUND_STRUCTURES 9 VEGETAL_DECORATION
4 SURFACE_STRUCTURES    10 TOP_LAYER_MODIFICATION
5 STRONGHOLDS
```

Un `minecraft:ore` solo puede reemplazar bloques que **ya existen** cuando corre su fase. En el Nether:

- `netherrack` existe desde `raw_generation` (es el terreno base).
- `basalt_blobs` y `blackstone_blobs` (Basalt Deltas) y `ore_blackstone` (resto de biomas) se generan en **`UNDERGROUND_DECORATION`** (índice 7).

Por eso las menas del mod se inyectan en `underground_decoration` y **no** en `underground_ores`. Estuvieron en `underground_ores` hasta `0.1.2-beta`, y esa fue la causa de que solo `netherrack_iron_ore` apareciera en el mundo: las menas de basalto y blackstone intentaban reemplazar bloques que todavía no existían.

Además, Forge **agrega al final** de la lista de features de la fase (`BiomeGenerationSettingsBuilder.addFeature` → `List.add`), así que las menas corren después de los blobs vanilla del mismo step.

### Cómo verificar esto si se toca el worldgen

- Orden real de las fases: `javap -p -classpath <forge-mapped.jar> 'net.minecraft.world.level.levelgen.GenerationStep$Decoration'`.
- Features vanilla por bioma y por índice de step: los JSON de `data/minecraft/worldgen/biome/*.json` dentro de `client-extra.jar` en la caché de ForgeGradle (`~/.gradle/caches/forge_gradle/minecraft_repo/versions/<mc>/client-extra.jar`).
- Ubicación de cada feature: `data/minecraft/worldgen/placed_feature/*.json` en el mismo jar.

> Cuidado al inspeccionar estos JSON: `ConvertFrom-Json` de PowerShell aplana arrays anidados. Usar un escáner de corchetes o leer el índice del array directamente.

## Compilación y ejecución (desarrollo)

> La primera compilación puede tardar varios minutos (ForgeGradle descarga/prepara dependencias y mappings).

### Windows (PowerShell) con JDK 17

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:Path="$env:JAVA_HOME\bin;$env:Path"

# Compilar
.\gradlew.bat build

# Cliente de desarrollo
.\gradlew.bat runClient

# Servidor de desarrollo
.\gradlew.bat runServer
```

## Configuración

- Archivo generado/usado: `config/nether-ores-reborn.toml`
- Documentación de claves y defaults: [`CONFIG.md`](CONFIG.md)

### Estado y limitaciones (config/worldgen)

- Los `placed_feature` usan modifiers custom para aplicar configuración de generación (`enableGeneration`, `enabled`, `veinsPerChunk`, `minY`, `maxY`).
- `veinSize` existe en la configuración, pero **aún no se aplica** a la generación porque el tamaño de veta sigue definido estáticamente en `data/nether_ores_reborn/worldgen/configured_feature/*.json`.
- Los multiplicadores de drops se aplican vía Global Loot Modifier (`general.dropMultiplier` y `ores.<id>.dropMultiplier`) sobre drops de recursos (no sobre el bloque obtenido con Silk Touch).

## Documentación técnica relacionada

- [`CONFIG.md`](CONFIG.md): claves, valores por defecto y comportamiento actual
- [`PORTING_NOTES.md`](PORTING_NOTES.md): notas para porteo a versiones superiores
- [`../AGENTS.md`](../AGENTS.md): guía del proyecto, reglas clean-room y flujo por fases
- `../openspec/changes/nether-ores-reborn-plan/`: artefactos SDD (propuesta, specs, diseño, tareas)

## Release

- Versión definida en `gradle.properties` (`mod_version`).
- Nombre del artefacto en `build.gradle` (`archiveFileName`), con el patrón `nether_ores_reborn-v<version>-<mc_version>-beta.jar`.
- Ejemplo vigente: `nether_ores_reborn-v0.1.2-1.20.1-beta.jar`.
- Publicación en GitHub Releases con tag `v<version>-beta` y el `.jar` adjunto. Versiones publicadas: `v0.1.0-beta`, `v0.1.1-beta`, `v0.1.2-beta`.
- Los commits y los push requieren aprobación explícita del usuario (ver `AGENTS.md`).

## Estado de verificación (resumen)

- **Build:** verificado (`BUILD SUCCESSFUL` con JDK 17).
- **Publicación:** `v0.1.0-beta`, `v0.1.1-beta` y `v0.1.2-beta` publicadas con artefacto adjunto; el `.jar` de `0.1.2-beta` fue verificado contra el árbol de trabajo (mismas texturas, `mods.toml` en `0.1.2-beta`, biome modifiers en `underground_decoration`).
- **Generación:** estructura verificada estáticamente (datos, step, orden de ejecución). La causa del bug de generación previo a `0.1.2-beta` está identificada y corregida.
- **Pendiente manual:** smoke tests cliente/servidor y validación en juego (densidad real percibida, drops en gameplay, visual/idiomas, comportamiento runtime de config).
- **Limitación conocida:** `veinSize` existe en la config pero no se aplica (tamaño de vena estático en `configured_feature`).
- **Higiene clean-room:** chequeos realizados sin hallazgos en archivos trackeados ni en el jar inspeccionado (ver tareas/registro del plan).
