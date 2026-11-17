# Checklist de pruebas manuales (baseline 1.20.1)

Esta checklist cubre las validaciones pendientes que no se ejecutaron automáticamente en este entorno. Ejecutar con **Java 17** y el baseline **Minecraft 1.20.1 + Forge 47.4.28**.

## Preparación

- [ ] Compilar el proyecto: `.\gradlew.bat build` (con JDK 17)
- [ ] Lanzar cliente de desarrollo: `.\gradlew.bat runClient`
- [ ] (Opcional) Lanzar servidor dedicado de desarrollo: `.\gradlew.bat runServer`
- [ ] Crear/cargar un mundo de pruebas con acceso al Nether
- [ ] Verificar que se genere/cargue `config/nether-ores-reborn.toml`

## Smoke tests básicos (7.1 / generales)

- [ ] El juego inicia sin crash con el mod cargado
- [ ] El mod aparece en la lista de mods (nombre/metadata correctos)
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

> Probar en un mundo/chunks nuevos o en zonas no exploradas para observar generación.

- [ ] Se generan menas de **carbón en basalto** en áreas con basalto
- [ ] Se generan menas de **diamante en basalto** en áreas con basalto
- [ ] Se generan menas de **hierro en piedra negra** en áreas con blackstone
- [ ] Se generan menas de **hierro en netherrack** en áreas con netherrack
- [ ] No se observan generaciones fuera de las bases aprobadas (p. ej. carbón/diamante en netherrack o blackstone)

## Drops y minería (4.2)

Validar con herramienta apropiada y comparar con/sin encantamientos según corresponda:

- [ ] `basalt_coal_ore` suelta **Coal** (sin Silk Touch)
- [ ] `basalt_diamond_ore` suelta **Diamond** (sin Silk Touch)
- [ ] `blackstone_iron_ore` suelta **Raw Iron** (sin Silk Touch)
- [ ] `netherrack_iron_ore` suelta **Raw Iron** (sin Silk Touch)
- [ ] **Silk Touch** devuelve el bloque de mena correspondiente
- [ ] **Fortune** afecta los drops de forma esperada (al menos verificación básica)
- [ ] Los drops se comportan correctamente al explotar/minar (sin pérdidas anómalas evidentes)

## Configuración y comportamiento runtime (5.1 / 5.2)

> Reiniciar el juego/servidor según sea necesario para que los cambios de config tomen efecto.

- [ ] `general.enableGeneration=false` desactiva la generación del mod (verificar en chunks nuevos)
- [ ] `ores.<id>.enabled=false` desactiva la generación de una mena específica
- [ ] `ores.<id>.veinsPerChunk` modifica la frecuencia de generación (validación básica)
- [ ] `ores.<id>.minY` / `maxY` modifican el rango de altura (validación básica)
- [ ] `general.dropMultiplier` modifica la cantidad de drops (validación básica)
- [ ] `ores.<id>.dropMultiplier` modifica la cantidad de drops para una mena específica (validación básica)

## Limitaciones conocidas a tener en cuenta

- [ ] `veinSize` existe en config pero **no se aplica** todavía a la generación (tamaño de veta estático en `configured_feature`)
- [ ] Las texturas actuales son **placeholders originales temporales**

## Resultado de la prueba

- Fecha:
- Probado por:
- Versión/mod build:
- Resultado general: ☐ Aprobado ☐ Aprobado con observaciones ☐ Fallido
- Observaciones / bugs encontrados:
  - 
  - 
