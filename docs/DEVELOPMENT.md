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
- **Versión inicial:** `0.1.0`

## Estructura y componentes implementados

- Registros: bloques, BlockItems y pestaña creativa
- Worldgen (data-driven + placements config-aware): `configured_feature`, `placed_feature`, `forge/biome_modifier`
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

## Estado de verificación (resumen)

- **Build:** verificado (`BUILD SUCCESSFUL` con JDK 17 en el entorno de desarrollo)
- **Pendiente manual:** smoke tests cliente/servidor y validaciones en juego (generación en Nether, drops en gameplay, visual/idiomas, comportamiento runtime de config)
- **Higiene clean-room:** chequeos realizados sin hallazgos en archivos trackeados ni en el jar inspeccionado (ver tareas/registro del plan)
