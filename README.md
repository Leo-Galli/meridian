# Meridian

A modern client-side HUD suite for Minecraft 1.21.8 (Fabric). Meridian brings an extensive, fully configurable heads-up display to your game: keystrokes, click tracking, ping, memory, server details, armor, a compass, waypoints and quick-chat macros.

> **Website: [https://meridian-mod.vercel.app/](https://meridian-mod.vercel.app/)** — live landing page with full feature overview.
> **Download: [https://meridian-mod.vercel.app/download](https://meridian-mod.vercel.app/download)** — dedicated page that always links the latest release jar from GitHub.

## Features

- **Config menu** bound to **Page Up** by default. The key can be changed at any time from inside the menu itself: open the menu, click *Menu Key* and press the new key.
- **Mod Menu integration** - when [Mod Menu](https://modrinth.com/mod/modmenu) is installed, Meridian gets a *Config* button that opens the full settings panel, and the *Website* button links to the official site.
- **Keystrokes** - real-time WASD, spacebar and mouse button overlay with press highlighting.
- **CPS Counter** - live left and right clicks-per-second with activity indicators.
- **Ping** - current server latency in milliseconds.
- **RAM Usage** - dynamic memory consumption with usage bar and percentage.
- **Server Info** - name and address of the server you are connected to.
- **Waypoints** - save, manage and render custom waypoints in the world, colored and fully editable.
- **AutoText Macros** - bind preset chat messages to keys and send them instantly while in game.
- **Backup & Transfer** - export your settings, waypoints, macros and menu key to `meridian-transfer.json` and import them on another computer.
- **FPS counter**, **Armor Status** with durability bars and a **Direction Compass**.

Every module is fully customizable: toggle it independently, rescale it, recolor text and accents, set background opacity and text shadow, fine-tune its X/Y position in pixel steps, reset a single module or everything at once, and reposition it by dragging in the built-in HUD Layout Editor.

## Requirements

- Minecraft 1.21.8
- Fabric Loader 0.16.13 or newer
- Fabric API 0.136.1 or newer
- Java 21 or newer

## Installation

1. Install the Fabric Loader for Minecraft 1.21.8.
2. Drop `fabric-api` and `meridian.jar` into your `mods` folder.
3. Launch the game, press **Page Up** (or your custom key) to open the Meridian menu.
4. Optional: install [Mod Menu](https://modrinth.com/mod/modmenu) for an in-game config button and quick access to the website.

## Website

The `website/` directory contains the official Meridian landing page, built with [Astro](https://astro.build).

```bash
cd website
npm install
npm run dev       # local development
npm run build     # production build (outputs to website/dist/)
```

The site is deployed to Vercel and lives at [https://meridian-mod.vercel.app/](https://meridian-mod.vercel.app/).

## Development

```bash
./gradlew build
```

The resulting jar is written to `build/libs/meridian.jar`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.