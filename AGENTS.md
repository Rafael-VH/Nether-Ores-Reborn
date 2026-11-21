# AGENTS.md

## 1. Descripción del Proyecto

**Nombre:** Nether Ores Reborn
**Objetivo:** Recrear desde cero, con código 100% original, un mod que añada menas (ores) al Nether con variantes de Netherrack, Basalt y Blackstone, inspirado conceptualmente en el mod abandonado "Nether Ores Plus+" (MCreator, Forge 1.20.1), pero implementado con **buenas prácticas modernas** empezando por **Minecraft 1.20.1** y con un plan de **portar progresivamente a versiones superiores**.

**Estado del mod original:**
- Última actualización: Forge 1.20.1
- Sin mantenimiento desde hace ~2 años
- Autor no responde
- Creado con MCreator
- Sin repositorio público de código fuente

**Filosofía del proyecto:** *Clean room implementation*. Se usa el mod original únicamente como referencia de **concepto** y **funcionalidad esperada**, NUNCA se copia código, texturas, modelos, sonidos ni nombres únicos. Todo el código y todos los assets son originales.

---

## 2. Stack Técnico

| Componente | Valor |
| :--- | :--- |
| **Minecraft (baseline inicial)** | **1.20.1** (con plan de portar a versiones superiores después) |
| **Loader principal** | **Forge 47.4.28** (instalador local: `recursos/forge-1.20.1-47.4.28-installer.jar`) |
| **Loader secundario (opcional)** | [DEFINIR según estrategia de porteo futuro: Fabric/NeoForge/etc.] |
| **Java** | **17** (compatible con Forge 1.20.1 47.x) |
| **Gradle** | Versión compatible con el template/MDK de Forge 1.20.1 |
| **IDE** | IntelliJ IDEA (con soporte Java 17+) |
| **Sistema de build** | **ForgeGradle** (según MDK oficial de Forge 1.20.1) |

### Reglas del stack
- **NO usar MCreator** en ninguna fase.
- Partir del **MDK oficial de Forge 1.20.1** (o template equivalente oficial), no de proyectos generados por MCreator.
- Mantener código y assets **originales** (clean room), aunque se tome como referencia funcional el mod original.
- Definir y documentar el **paquete raíz** y el **mod id** antes de implementar (ej. `nether_ores_reborn`).
- Al portar a versiones superiores, actualizar explícitamente mappings, Java, Gradle y APIs según la versión/loader destino.

---

## 3. Herramientas de Apoyo

### 3.1 REA (Reverse Engineer Anything)
Servidor MCP para ingeniería inversa asistida por IA. Configuración:
- Variable de entorno: `REA_VINEFLOWER_JAR=/ruta/a/vineflower.jar`
- Setup: `npx rea-agents setup`
- Herramientas disponibles: `inspect_java_archive`, `inspect_java_class`, `decompile_java_class`

### 3.2 Vineflower
Descompilador principal. Descargar de: https://github.com/Vineflower/vineflower
Usado **exclusivamente** para análisis local del JAR original. Nunca subir código descompilado a Git.

### 3.3 OpenCode
Agente de IA principal. Usa este archivo `AGENTS.md` como guía de proyecto.

### 3.4 Referencia del mod original
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/nether-ores-plus
- El `.jar` original debe permanecer en una carpeta **ignorada por git** (`/reference/`).

---

## 4. Reglas Legales y Éticas (NO NEGOCIABLES)

1. **NUNCA** copiar código del mod original al proyecto, ni siquiera parcialmente.
2. **NUNCA** extraer texturas, modelos, sonidos o archivos de datos del JAR original para usarlos en el mod.
3. **NUNCA** subir a Git: JAR original, código descompilado, capturas del código descompilado.
4. Todo asset (texturas, modelos, sonidos, nombres) debe crearse desde cero.
5. Los nombres de bloques, items y clases deben ser **propios** (ej. "Nether Ore Reborn" en lugar de "Nether Ores Plus+").
6. Se permite consultar la **funcionalidad** (qué hace el mod), pero la **implementación** debe ser original.
7. La carpeta `/reference/` debe estar en `.gitignore`.

---

## 5. Alcance Funcional (inspirado en el concepto original)

El mod debe implementar las siguientes funcionalidades, **reinterpretadas de forma original**:

### 5.1 Bloques de mena
- Mena de **Netherrack** (base: netherrack)
- Mena de **Basalt** (base: basalt)
- Mena de **Blackstone** (base: blackstone)

**Matriz aprobada (no ampliar sin aprobación explícita del usuario):**

| ID de bloque | Base | Drop base |
| :--- | :--- | :--- |
| `netherrack_iron_ore` | Netherrack | Raw Iron |
| `blackstone_iron_ore` | Blackstone | Raw Iron |
| `basalt_coal_ore` | Basalt | Coal |
| `basalt_diamond_ore` | Basalt | Diamond |

Variantes descartadas en el baseline: esmeralda, lapis, redstone y oro.

### 5.2 Comportamiento
- Cada variante debe generar drops con una **tabla de loot configurable**.
- Las menas deben spawnear en el bioma del Nether correspondiente durante la generación del mundo.
- Los drops deben poder consultarse vía **datapack JSON** (loot tables estándar de Minecraft).
- **Contrato de orden de generación (crítico):** las menas se inyectan en el step `underground_decoration`, no en `underground_ores`. Un `minecraft:ore` solo puede reemplazar bloques que ya existen cuando corre su fase, y el basalto (`basalt_blobs`) y el blackstone (`blackstone_blobs`, `ore_blackstone`) se generan en `underground_decoration`. Meter las menas antes hace que las variantes de basalto y blackstone no aparezcan, **sin ningún error visible**. Ver `docs/DEVELOPMENT.md` y `docs/PORTING_NOTES.md`.

### 5.3 Configuración
- Archivo `config/nether-ores-reborn.toml` con:
  - Activar/desactivar cada variante de mena.
  - Frecuencia de spawn por chunk.
  - Rango de altura mínima y máxima.
  - Multiplicador de drops.

### 5.4 Integración opcional (no bloqueante)
- Mod Menu (Fabric) o config screen (NeoForge) para editar la config in-game.
- Compatibilidad con menas de otros mods vía tag de bloque (opcional, si el agente lo estima conveniente).

---

## 6. Flujo de Trabajo con el Agente (OpenCode)

### Control de versiones entre fases
- Después de completar cada fase (o un hito verificable dentro de una fase), se recomienda crear un **commit** para mantener el historial claro y facilitar revisiones/rollbacks.
- Para crear commits, se recomienda usar una **skill especializada** para la tarea (por ejemplo, `git-commit`) cuando esté disponible.
- Los **commits** solo pueden realizarse con **aprobación explícita** del usuario; **nunca** de forma automática.
- Los **push** al repositorio remoto solo pueden realizarse con **aprobación explícita** del usuario; **nunca** de forma automática.

### Preferencias de sesión SDD (preflight)
Definidas por el usuario el 2026-11-20. Se aplican por defecto en cada sesión SDD de este proyecto; no volver a preguntarlas salvo que el usuario pida cambiarlas explícitamente.

| Grupo | Valor canónico | Significado |
| :--- | :--- | :--- |
| Ritmo | `interactive` | Mostrar cada fase y esperar confirmación antes de continuar |
| Artefactos | `both` | Archivos OpenSpec en el repo + copia de recuperación en Engram |
| PRs | `auto-forecast` | Decidir single vs encadenado a partir de la estimación de tamaño |
| Revisión | `review_budget_lines: 800` | Frenar y consultar si la estimación supera 800 líneas cambiadas |

- Artefactos OpenSpec: `openspec/changes/<change>/`
- Artefactos Engram: `sdd/<change>/<fase>` (por ejemplo `sdd/<change>/tasks`)
- Contexto del proyecto para SDD: `sdd-init/nether-ores-reborn`

### Fase 0 — Análisis del mod original (solo referencia local)
1. Colocar el JAR original en `/reference/original.jar`.
2. Usar REA: `inspect_java_archive` para obtener estructura general.
3. Usar REA: `decompile_java_class` sobre las clases clave (registro de bloques, generación de mundo, loot tables).
4. **Documentar la funcionalidad** en `/reference/notes.md` (descripción en lenguaje natural, no copiar código).
5. Confirmar que `/reference/` está en `.gitignore` antes de continuar.

### Fase 1 — Bootstrap del proyecto
1. Inicializar proyecto base para **Minecraft 1.20.1 + Forge 47.4.28** usando el **MDK oficial de Forge 1.20.1** (no MCreator).
2. Verificar:
   - **Java 17** configurado en el entorno/IDE/Gradle.
   - `./gradlew build` compila el template vacío sin errores.
   - Estructura y configuración de **ForgeGradle** correctas para 1.20.1.
3. Configurar `.gitignore`:
   ```
   /reference/
   /recursos/
   /run/
   /build/
   .gradle/
   *.jar
   !gradle/wrapper/gradle-wrapper.jar
   ```
4. Definir valores base del mod (mod id, paquete, nombre visible, versión) antes de registrar contenido.

### Fase 2 — Registro de bloques (Forge 47.4.28)
1. Crear clase `NetherOresReborn` (clase principal).
2. Registrar las 4 variantes de mena aprobadas con sus `Block` y `BlockItem`.
3. Añadir los bloques a los creative tabs correspondientes.
4. Compilar (`./gradlew build`) y verificar en un mundo de prueba.

### Fase 3 — Generación de mundo
1. Añadir las menas al **worldgen** del Nether mediante el pipeline data-driven de Forge:
   - `data/nether_ores_reborn/worldgen/configured_feature/*.json` (tipo `minecraft:ore` con `block_match`).
   - `data/nether_ores_reborn/worldgen/placed_feature/*.json` (count, altura, bioma).
   - `data/nether_ores_reborn/forge/biome_modifier/*.json` con `forge:add_features` sobre `#minecraft:is_nether`.
2. Usar el step **`underground_decoration`** (ver 5.2). Verificar el orden real de `GenerationStep.Decoration` en la versión objetivo antes de cambiarlo.
3. Verificar en mundo de prueba con `/locate biome` y generando chunks nuevos (los chunks existentes no se regeneran).

### Fase 4 — Loot tables
1. Crear las loot tables JSON en `data/nether_ores_reborn/loot_tables/blocks/`.
2. Cada tabla debe replicar la **funcionalidad** (drops con probabilidades) sin copiar la estructura exacta del original.
3. Probar drops con `/loot give` o `LootContext`.

### Fase 5 — Configuración
1. Implementar `ModConfig` con `ModConfigSpec` (config COMMON de Forge).
2. Cargar los valores desde `config/nether-ores-reborn.toml`.
3. Aplicar los valores en la generación de mundo y en los drops. Ojo: `veinSize` sigue sin aplicarse (el tamaño de vena vive estático en `configured_feature`).

### Fase 6 — Assets originales
1. Crear texturas propias 16x16 para cada mena (puede usar Aseprite, GIMP o Krita).
2. Crear `blockstates` y `models` JSON propios.
3. Crear `lang/en_us.json` y `lang/es_es.json` con nombres propios.
4. **NO** usar texturas del mod original como base ni como referencia directa.

### Fase 7 — Testing y pulido
1. Compilar: `./gradlew build`.
2. Probar en cliente y servidor.
3. Verificar que no haya dependencias de MCreator ni de código legado.
4. Revisar que el `.jar` final no contenga restos de `/reference/`.

### Fase 8 — Publicación (opcional)
1. Añadir `LICENSE` (recomendado: MIT o LGPL-3.0).
2. Añadir `README.md` con créditos conceptuales al mod original.
3. Publicar en CurseForge / Modrinth con atribución al concepto original (sin usar sus assets ni su nombre).
4. **NO** publicar el JAR original ni el código descompilado.

---

## 7. Convenciones de Código

### Nomenclatura
- Paquete raíz: `com.rexthar.netheroresreborn`.
- Clases: PascalCase.
- Métodos/variables: camelCase.
- Constantes: SCREAMING_SNAKE_CASE.
- Prefijo de registro: `nether_ores_reborn:` (nunca reutilizar `nether_ores_plus`).

### Estilo
- Seguir las convenciones de **Java 17** (el baseline): records, sealed classes, pattern matching para `instanceof` y switch expressions están disponibles y se usan cuando aportan claridad.
- Usar `@Override` explícito.
- Documentar todas las clases públicas con Javadoc.
- No dejar código comentado sin justificación.
- No dejar TODOs sin contexto.

### Estructura de carpetas (estado actual)

```
src/main/java/com/rexthar/netheroresreborn/
├── NetherOresReborn.java
├── config/
│   └── NetherOresRebornConfig.java
├── loot/
│   ├── ModLootModifiers.java
│   └── OreDropMultiplierModifier.java
├── registry/
│   ├── ModBlocks.java
│   ├── ModItems.java
│   └── ModCreativeTabs.java
├── util/
│   └── ModConstants.java
└── worldgen/placement/
    ├── ConfigCountPlacement.java
    ├── ConfigUniformHeightRangePlacement.java
    └── ModPlacementModifiers.java

src/main/resources/
├── assets/nether_ores_reborn/          # namespace con guiones bajos, NO guiones
│   ├── blockstates/
│   ├── models/
│   ├── textures/
│   └── lang/
├── data/nether_ores_reborn/
│   ├── forge/biome_modifier/           # forge:add_features (step: underground_decoration)
│   ├── loot_modifiers/
│   ├── loot_tables/
│   └── worldgen/
│       ├── configured_feature/
│       └── placed_feature/
└── META-INF/mods.toml
```

El worldgen del baseline es **data-driven**: no hay clases `ModConfiguredFeatures` / `ModPlacedFeatures` / `ModBiomeModifications`; solo existen placement modifiers custom en `worldgen/placement/`.

---

## 8. Comandos Útiles

```bash
# Compilar
./gradlew build

# Ejecutar cliente
./gradlew runClient

# Ejecutar servidor
./gradlew runServer

# Refrescar dependencias (necesario al migrar de versión)
./gradlew --refresh-dependencies

# Limpiar
./gradlew clean
```

---

## 9. Recursos de Referencia

| Recurso | URL |
| :--- | :--- |
| Guía de porteo Fabric 26.1 | https://docs.fabricmc.net/26.1.2/develop/porting/ |
| Documentación NeoForge | https://docs.neoforged.net/ |
| Migración NeoForge 1.21.11 → 26.1 | https://docs.neoforged.net/primer/docs/26.2/ |
| Vineflower | https://github.com/Vineflower/vineflower |
| REA | https://github.com/morluto/rea |
| OpenCode Docs | https://opencode.ai/docs |
| Mod original (solo referencia conceptual) | https://www.curseforge.com/minecraft/mc-mods/nether-ores-plus |

---

## 10. Advertencias para el Agente

- ⚠️ **No inventar funcionalidad** que no exista en el mod original sin consultar antes al usuario.
- ⚠️ **No copiar la estructura de clases** del código descompilado al pie de la letra; reescribir con patrones modernos (records, builders, `DeferredRegister`/`Registry`).
- ⚠️ **No omitir la compilación** después de cada fase. Un build roto debe resolverse antes de avanzar.
- ⚠️ **No crear assets** sin antes confirmar que el usuario aprobó la lista de menas y sus atributos. Las texturas actuales son **definitivas**.
- ⚠️ **No publicar** en ningún registro público (CurseForge, Modrinth, GitHub) sin autorización explícita del usuario.
- ⚠️ **No eliminar** `/reference/` ni su contenido sin autorización; es material de consulta local.
- ⚠️ **Verificar** en cada release que el `.jar` no incluya nada de `/reference/` ni `/recursos/`.
- ⚠️ **No cambiar el `step` de generación** sin verificar antes el orden real de `GenerationStep.Decoration` en la versión objetivo y en qué step se generan los bloques base. Un step incorrecto **no produce ningún error**: las menas simplemente no aparecen. Ver 5.2 y `docs/DEVELOPMENT.md`.
- ⚠️ **No confundir "probado en un mundo viejo" con "no genera"**. Las menas solo aparecen en chunks nuevos; los ya generados no se regeneran.
- ⚠️ **No commitear ni pushear sin aprobación explícita del usuario.**

---

## 11. Historial de Cambios del Proyecto

| Fecha | Fase | Notas |
| :--- | :--- | :--- |
| 2026-11-17 | Fase 0 | Inicio del proyecto, análisis del original |
| 2026-11-17 | Planificación | Cambio de baseline: se inicia en **Minecraft 1.20.1 + Forge 47.4.28** con plan de porteo progresivo a versiones superiores |
| 2026-11-17 | Fases 1–4 | Bootstrap del MDK, registros de bloques/ítems, worldgen data-driven, loot tables, config COMMON y Global Loot Modifier |
| 2026-11-18 | Release `v0.1.0-beta` | Primera versión publicada con los 4 bloques y generación en el Nether |
| 2026-11-18 | Release `v0.1.1-beta` | Config cableada al worldgen (`config_count`, `config_uniform_height_range`), corrección de multiplicadores de drops y manejo de Silk Touch, limpieza de entrypoint, documentación |
| 2026-11-18 | Release `v0.1.2-beta` | **Fix de generación:** biome modifiers movidos de `underground_ores` a `underground_decoration` (causa de que solo `netherrack_iron_ore` apareciera). Texturas definitivas. Auditoría completa y documentación actualizada |
| 2026-11-20 | Fix worldgen | `blackstone_iron_ore` pasa a 48 intentos por chunk y rango Y 5–32 (el blackstone vanilla fuera de Basalt Deltas solo existe en Y 5–31). Se registran las preferencias de sesión SDD |

---

*Última actualización de este archivo: 2026-11-20 — Actualizar cada vez que cambie el alcance o el stack.*

---

### 📌 Notas sobre el archivo

1. **Está diseñado para ser iterativo**: a medida que avances, actualiza las secciones 5 (alcance), 7 (nomenclatura) y 11 (historial).
2. **Los campos marcados con `[DEFINIR]` o `[FECHA]`** debes completarlos tú antes de la primera ejecución real de OpenCode.
3. **La sección de reglas legales (4) y las advertencias (10)** son la red de seguridad para que el agente no derive hacia prácticas grises.
4. **El flujo por fases (6)** permite que OpenCode trabaje de forma incremental y verificable, con compilación al final de cada fase.
5. **Si más adelante decides soportar ambos loaders**, añade una sección `## 12. Estrategia Multi-Loader` con la estructura de carpetas (`common/`, `fabric/`, `neoforge/`) y las reglas de abstracción entre ellos.

Guarda este archivo en la **raíz del repositorio del proyecto** como `AGENTS.md` y estarás listo para que OpenCode lo use como guía durante todo el desarrollo.
