# Checklist de pruebas manuales (baseline 1.20.1)

Esta checklist cubre las validaciones que no se ejecutaron automáticamente en el entorno de desarrollo. Ejecutar con **Java 17** y el baseline **Minecraft 1.20.1 + Forge 47.4.28**.

Versión de referencia: **`0.1.2-beta`** (artefacto `nether_ores_reborn-v0.1.2-1.20.1-beta.jar`).

## Preparación

- [ ] Compilar el proyecto: `.\gradlew.bat build` (con JDK 17)
- [ ] Lanzar cliente de desarrollo: `.\gradlew.bat runClient`
- [ ] (Opcional) Lanzar servidor dedicado de desarrollo: `.\gradlew.bat runServer`
- [ ] **Crear un mundo nuevo** (o usar uno con el Nether nunca explorado)
- [ ] Si existe un `config/nether-ores-reborn.toml` de una versión anterior, borrarlo antes de arrancar para regenerar los defaults
- [ ] Verificar que se genere/cargue `config/nether-ores-reborn.toml`

> Las menas solo se generan en **chunks nuevos**. Un chunk ya generado nunca se regenera, así que probar en zonas exploradas da falsos negativos.

## Smoke tests básicos (7.1 / generales)

- [ ] El juego inicia sin crash con el mod cargado
- [ ] El mod aparece en la lista de mods (nombre/metadata correctos, versión `0.1.2-beta`)
- [ ] El servidor dedicado inicia sin crash (si se prueba)

## Contenido y UI (2.3, 6.1, 6.2)

- [ ] Los 4 bloques existen en juego:
  - [ ] `basalt_coal_ore`
  - [ ] `basalt_diamond_ore`
  - [ ] `blackstone_iron_ore`
  - [ ] `netherrack_iron_ore`
- [ ] Los BlockItems se pueden obtener y colocar
- [ ] La pestaña creativa del mod muestra los 4 bloques
- [ ] No hay texturas faltantes (purple/black), modelos rotos o ítems invisibles
- [ ] Nombres/localización correctos en `en_us` y `es_es` (bloques y pestaña creativa)

## Generación en Nether (3.2)

> Usar un mundo nuevo o viajar a chunks no generados. Coordenadas de referencia: `/locate biome minecraft:basalt_deltas` y `/locate biome minecraft:nether_wastes`.

### Casos de regresión del fix de `0.1.2-beta`

Antes de `0.1.2-beta` las menas se inyectaban en el step `underground_ores`, antes de que se generaran el basalto y el blackstone, y por eso solo aparecía `netherrack_iron_ore`. Estos son los casos que **deben** pasar ahora:

- [ ] En **Basalt Deltas** se generan `basalt_coal_ore`
- [ ] En **Basalt Deltas** se generan `basalt_diamond_ore`
- [ ] En **Basalt Deltas** se genera `blackstone_iron_ore` entre **Y 5 y 32**
- [ ] En **Nether Wastes** (y demás biomas del Nether) se genera `blackstone_iron_ore` entre **Y 5 y 31**
- [ ] En **Nether Wastes** se genera `netherrack_iron_ore`
- [ ] En **Basalt Deltas** también se generan `netherrack_iron_ore` y `blackstone_iron_ore`

### Coherencia de bases

- [ ] Las menas de basalto aparecen **solo** dentro de basalto (no en netherrack ni blackstone)
- [ ] `blackstone_iron_ore` aparece **solo** dentro de blackstone
- [ ] `netherrack_iron_ore` aparece **solo** dentro de netherrack
- [ ] No se observan menas flotando en aire ni reemplazando bloques fuera de su base

### Distribución esperada (referencia)

- [ ] Las menas de basalto **no** aparecen en biomas sin basalto (Nether Wastes, Soul Sand Valley, Crimson Forest, Warped Forest). Esto es comportamiento vanilla-consistente, no un bug.
- [ ] La densidad en Basalt Deltas es claramente mayor que la de blackstone en el resto de biomas, porque ahí el blackstone solo existe entre Y 5 y 31.

## Drops y minería (4.2)

Validar con herramienta apropiada y comparar con/sin encantamientos según corresponda:

- [ ] `basalt_coal_ore` suelta **Coal** (sin Silk Touch)
- [ ] `basalt_diamond_ore` suelta **Diamond** (sin Silk Touch)
- [ ] `blackstone_iron_ore` suelta **Raw Iron** (sin Silk Touch)
- [ ] `netherrack_iron_ore` suelta **Raw Iron** (sin Silk Touch)
- [ ] **Silk Touch** devuelve el bloque de mena correspondiente (sin multiplicación del bloque por `dropMultiplier`)
- [ ] **Fortune** afecta los drops de recursos de forma esperada (al menos verificación básica)
- [ ] Los drops se comportan correctamente al explotar/minar (sin pérdidas anómalas evidentes)

## Configuración y comportamiento runtime (5.1 / 5.2)

> Reiniciar el juego/servidor después de cada cambio de config. Probar siempre en **chunks nuevos**.

- [ ] `general.enableGeneration=false` desactiva la generación del mod (verificar en chunks nuevos)
- [ ] `ores.<id>.enabled=false` desactiva la generación de una mena específica
- [ ] `ores.<id>.veinsPerChunk` modifica la frecuencia de generación (validación básica)
- [ ] `ores.<id>.minY` / `maxY` modifican el rango de altura (validación básica)
- [ ] `general.dropMultiplier` modifica la cantidad de drops de recursos (validación básica)
- [ ] `ores.<id>.dropMultiplier` modifica la cantidad de drops de recursos para una mena específica (validación básica)
- [ ] `dropMultiplier` con valores `< 1` reduce drops de recursos según lo esperado (comportamiento probabilístico/fraccional)
- [ ] `dropMultiplier` no multiplica el bloque obtenido con **Silk Touch**
- [ ] Un `.toml` viejo conserva sus valores y pisa los defaults nuevos (comportamiento esperado, documentado)

## Interfaz de configuración en juego (config-gui)

Abrir **Mods → Nether Ores Reborn → Config** y comprobar:

- [ ] El botón **Config** está habilitado solo para este mod; al seleccionar otro mod no se abre esta pantalla
- [ ] La pantalla se abre y **Done** / **Cancel** vuelven a la lista de mods
- [ ] La página General muestra `enableGeneration` y `dropMultiplier` con los valores efectivos del `.toml`
- [ ] Las cuatro páginas de mena muestran `enabled`, `veinsPerChunk`, `minY`, `maxY` y `dropMultiplier` con los valores del archivo, no los defaults
- [ ] No aparece ningún control para `veinSize` en ninguna página
- [ ] Cambiar un valor y pulsar **Done** actualiza `config/nether-ores-reborn.toml`; al reabrir se ve el valor guardado
- [ ] Cambiar un valor y pulsar **Cancel** no modifica el archivo
- [ ] **Reset page** seguido de **Done** persiste los defaults documentados de esa página
- [ ] `dropMultiplier` acepta `0`, `50`, `100` y `50.55`; rechaza `abc`, `-1`, `100.1` y `50.555`
- [ ] Un valor inválido resalta el campo y bloquea **Done** y el cambio de página
- [ ] `veinsPerChunk = 0` muestra el aviso de que desactiva la generación de esa mena
- [ ] Al dejar `minY` por encima de `maxY`, **Done** guarda un rango ordenado
- [ ] Un valor fuera de rango escrito a mano en el `.toml` (por ejemplo `minY = -64`) se muestra con el marcador `(outside UI range)` y sigue en el archivo tras un **Done** posterior
- [ ] En un mundo local se ve el aviso de que los cambios solo afectan a chunks nuevos
- [ ] Conectado a un servidor remoto se ven además el aviso de que solo se edita la copia local de la configuración
- [ ] Con `en_us` y `es_es` no se muestra ninguna clave `gui.…` sin traducir
- [ ] Un servidor dedicado inicia sin errores con el mod instalado

## Limitaciones conocidas a tener en cuenta

- [ ] `veinSize` existe en config pero **no se aplica** todavía a la generación (tamaño de vena estático en `configured_feature`: 12, 6, 10 y 10)
- [ ] Las texturas del mod se ven correctamente y sin artefactos visibles inesperados (son las definitivas)
- [ ] `veinsPerChunk` cambia la cantidad de **intentos** de generación, no la cantidad de bloques por vena

## Resultado de la prueba

- Fecha:
- Probado por:
- Versión/mod build:
- Resultado general: ☐ Aprobado ☐ Aprobado con observaciones ☐ Fallido
- Observaciones / bugs encontrados:
  - 
  - 
