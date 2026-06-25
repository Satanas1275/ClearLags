# Changelog

## v1.0.0 — Initial Release

- Auto-clear dropped items on a configurable timer (default: 60s).
- Warning message before each clear (customizable text & time).
- `/clearlags clear` — manual clear command.
- `/clearlags reload` — hot-reload config without restart.
- `/clearlags gui on|off|placement` — toggle/move the HUD countdown overlay.
- Per-player GUI settings (position & visibility) persisted to `config/clearlags_gui.json`.
- Fully customizable messages with `%killed` and `%seconds` placeholders.
- Server-side only; HUD overlay requires the mod on the client but is entirely optional.
- Fabric API (26.1.2) — modules bundled separately to avoid Loom remap issues.
