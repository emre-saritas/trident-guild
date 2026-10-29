# TridentGuild

A guild (lonca) and guild-war plugin for a Paper/Spigot 1.16.5 Minecraft network, synced across servers with Redis and MySQL.

## Features

- Guild creation, invites, member/operator permissions and guild settings menus
- Guild chat across servers via Redis
- Guild upgrades, banners and point-based leaderboards
- Scheduled guild wars (`/war`) with beacon capture points and kill scoring
- PlaceholderAPI placeholders for guild and war data

## Tech stack

- Java 11, Maven
- Paper API 1.16.5, PlaceholderAPI
- MySQL, Redis (via `helper-redis`)

## Getting started

Requirements: JDK 11, Maven, and these jars placed in `lib/` (not committed): `spigot-1.16.5.jar`, `TridentSync-1.0.0.jar`, `TridentJobs.jar`, `helper.jar`, `helper-redis.jar`.

```bash
mvn package
```

Copy the jar from `target/` into the server's `plugins/` folder. The server also needs the plugins listed under `depend` in `plugin.yml`: TridentSync, TridentJobs, helper, helper-redis.

## Configuration

Config files live in `src/main/resources/`:

| File | Purpose |
|---|---|
| `config.yml` | General guild settings, prices, banner locations |
| `redis.yml` | Redis connection |
| `kingdomwar.yml` | War world, timing and spawn points |
| `menus.yml`, `messages.yml` | GUI and chat text |
| `upgrades.yml` | Guild upgrade tiers |

## Commands

| Command | Description |
|---|---|
| `/lonca` | Guild menu and management |
| `/lmsg` | Guild chat |
| `/war` | Guild war |
| `/tridentguild` | Admin commands |

## Project structure

```
src/main/java/tc/trident/tridentguild/
├── cmds/, menus/, listeners/   # player-facing entry points
├── invite/                     # guild invites
├── kingdomwars/                # guild war logic
├── mysql/                      # persistence
└── utils/
```

## License

[MIT](LICENSE)
