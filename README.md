# ClearLags

Automatically clears dropped items on a configurable timer with warning messages, a manual `/clearlags` command, and an optional HUD countdown overlay.

---

**ClearLags** is a lightweight server-side Fabric mod that keeps your world clean by automatically removing dropped items on a configurable interval.

## Features

- **Auto-clear timer** — drops are cleared every N seconds (configurable, default 60).
- **Warning message** — broadcasts a customizable warning a few seconds before each clear (e.g., "Clearing in 10 seconds").
- **`/clearlags` command**:
  - `/clearlags clear` — triggers an immediate clear (permission: GAMEMASTER).
  - `/clearlags reload` — reloads config from disk (permission: ADMIN).
  - `/clearlags gui on|off|placement` — toggles the HUD overlay or cycles its screen position (4 corners).
- **Configurable messages** — all chat messages can be customized or disabled. Use `%killed` for item count and `%seconds` for countdown.
- **Optional HUD** — players with the mod see a small square overlay showing the seconds remaining before the next clear. It can be moved to any corner or turned off.
- **Client-optional** — the mod works fully on the server; the HUD overlay only appears for players who also have the mod installed.

## Config (`config/clearlags.json`)

```json
{
  "clearInterval": 60,
  "warningTime": 10,
  "warningMessage": "§e[ClearLags] §fClearing dropped items in §a%seconds §fseconds!",
  "warningMessageEnabled": true,
  "clearMessage": "§e[ClearLags] §fClearing dropped items...",
  "clearMessageEnabled": true,
  "afterClearMessage": "§e[ClearLags] §fCleared §a%killed §fitems!",
  "afterClearMessageEnabled": true
}
```

## Requirements

- Minecraft 26.1.2
- Fabric Loader >= 0.19.2
- Fabric API (see `libs/` for bundled modules)

## License

CC-BY-NC-4.0
