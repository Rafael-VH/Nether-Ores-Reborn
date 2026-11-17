# Nether Ores Reborn

Mod para **Minecraft 1.20.1 (Forge)** que agrega nuevas menas al Nether en variantes de **basalto**, **piedra negra (blackstone)** y **netherrack**, con un estilo pensado para integrarse al juego vanilla.

> Proyecto en desarrollo activo (baseline 1.20.1). Algunas validaciones en juego todavía están pendientes.

## ¿Qué añade este mod?

En esta versión inicial incluye **4 menas**:

- **Mena de carbón en basalto** → suelta carbón
- **Mena de diamante en basalto** → suelta diamante
- **Mena de hierro en piedra negra** → suelta hierro en bruto
- **Mena de hierro en netherrack** → suelta hierro en bruto

Las menas aparecen en el Nether y cuentan con nombres en **inglés** y **español**.

## Instalación (usuarios)

1. Instala **Minecraft 1.20.1**.
2. Instala **Minecraft Forge 47.4.28** (o una versión compatible de la serie 47 para 1.20.1).
3. Coloca el archivo `.jar` del mod en la carpeta `mods` de tu instalación.
4. Inicia el juego con el perfil de Forge.

## Configuración (usuarios)

El mod genera un archivo de configuración en:

- `config/nether-ores-reborn.toml`

Desde ahí podrás ajustar opciones generales y por mena (por ejemplo, activar/desactivar generación o parámetros de aparición), según la versión del mod que estés usando.

## Estado y notas

- Este es un **baseline inicial** enfocado en compatibilidad con 1.20.1.
- Las texturas actuales son **placeholders originales temporales** (se pueden mejorar en futuras versiones).
- Algunas comprobaciones manuales en juego (generación visual, drops en partidas, pruebas cliente/servidor) siguen pendientes.

## Licencia y créditos

- Proyecto de reimplementación *clean-room* (código y recursos propios).
- Inspirado conceptualmente en un mod antiguo abandonado, sin copiar su código ni sus assets.

## Para desarrolladores

La información técnica (compilación, estructura, configuración avanzada, notas de porteo, etc.) está en la carpeta [`docs/`](docs/), especialmente:

- [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)
- [`docs/CONFIG.md`](docs/CONFIG.md)
- [`docs/PORTING_NOTES.md`](docs/PORTING_NOTES.md)
