# Nether Ores Reborn

Mod para **Minecraft 1.20.1 (Forge)** que agrega nuevas menas al Nether en variantes de **basalto**, **piedra negra (blackstone)** y **netherrack**, con un estilo pensado para integrarse al juego vanilla.

**Versión actual:** `0.1.2-beta` — [descargar release](https://github.com/Rafael-VH/Nether-Ores-Reborn/releases/tag/v0.1.2-beta)

> Proyecto en desarrollo activo (baseline 1.20.1). La validación manual de generación en partidas reales sigue pendiente.

## ¿Qué añade este mod?

En esta versión incluye **4 menas**:

- **Mena de carbón en basalto** → suelta carbón
- **Mena de diamante en basalto** → suelta diamante
- **Mena de hierro en piedra negra** → suelta hierro en bruto
- **Mena de hierro en netherrack** → suelta hierro en bruto

Los drops responden a **Fortune**; con **Silk Touch** obtenés el bloque de mena. Los nombres están en **inglés** y **español**.

## Dónde aparecen

Las menas **reemplazan un bloque base que ya existe**, no lo crean. Por eso dependen del terreno donde se generan:

| Mena | Dónde aparece |
| --- | --- |
| `basalt_coal_ore` | **Basalt Deltas** (donde hay basalto natural) |
| `basalt_diamond_ore` | **Basalt Deltas** (donde hay basalto natural) |
| `blackstone_iron_ore` | **Basalt Deltas** (cualquier altura) y el resto de biomas del Nether (entre Y 5 y 31, donde el blackstone se genera de forma nativa) |
| `netherrack_iron_ore` | Todos los biomas del Nether |

Consecuencia importante: si un bioma no tiene basalto, no vas a encontrar menas de basalto ahí. Es comportamiento consistente con vanilla, no un error.

## Instalación (usuarios)

1. Instala **Minecraft 1.20.1**.
2. Instala **Minecraft Forge 47.4.28** (o una versión compatible de la serie 47 para 1.20.1).
3. Coloca el archivo `.jar` del mod en la carpeta `mods` de tu instalación.
4. Inicia el juego con el perfil de Forge.

El artefacto del release se llama `nether_ores_reborn-v0.1.2-1.20.1-beta.jar`.

## Configuración (usuarios)

El mod genera un archivo de configuración en:

- `config/nether-ores-reborn.toml`

Opciones disponibles:

| Clave | Default | Qué hace |
| --- | --- | --- |
| `general.enableGeneration` | `true` | Interruptor maestro de generación de las menas del mod |
| `general.dropMultiplier` | `1.0` | Multiplicador global de drops de recursos |
| `ores.<id>.enabled` | `true` | Activa o desactiva esa variante de mena |
| `ores.<id>.veinsPerChunk` | `basalt_coal_ore=24`, `basalt_diamond_ore=8`, `blackstone_iron_ore=16`, `netherrack_iron_ore=24` | Intentos de generación por chunk |
| `ores.<id>.minY` / `maxY` | `5` / `118` | Rango vertical de aparición |
| `ores.<id>.dropMultiplier` | `1.0` | Multiplicador de drops por mena |

Los IDs válidos son `basalt_coal_ore`, `basalt_diamond_ore`, `blackstone_iron_ore` y `netherrack_iron_ore`.

Notas:

- `veinSize` existe en el archivo pero **todavía no se aplica**: el tamaño de vena está fijo en los datos de worldgen (12, 6, 10 y 10 respectivamente).
- Si actualizás desde una versión anterior, tu `.toml` existente **pisa los defaults nuevos**. Borralo para regenerarlo o ajustá los valores a mano.
- Los cambios de configuración no afectan **chunks ya generados**: hay que explorar chunks nuevos o crear un mundo nuevo para verlos.

## Estado y notas

- Baseline enfocado en **1.20.1 + Forge 47.4.28** (Java 17).
- Las texturas del mod son **originales y finales**.
- La generación está integrada en el paso `underground_decoration` del worldgen, igual que las menas del Nether vanilla, para asegurar que el basalto y el blackstone existan antes de colocar las menas.
- Queda pendiente la validación manual en juego (densidad percibida, comportamiento en cliente y servidor dedicado).

## Licencia y créditos

- Proyecto de reimplementación *clean-room* (código y recursos propios).
- Inspirado conceptualmente en un mod antiguo abandonado, sin copiar su código ni sus assets.

## Para desarrolladores

La información técnica (compilación, estructura, configuración avanzada, notas de porteo, etc.) está en la carpeta [`docs/`](docs/), especialmente:

- [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)
- [`docs/CONFIG.md`](docs/CONFIG.md)
- [`docs/PORTING_NOTES.md`](docs/PORTING_NOTES.md)
- [`docs/MANUAL_TESTING.md`](docs/MANUAL_TESTING.md)
