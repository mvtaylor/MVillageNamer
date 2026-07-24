# MVillageNamer

This Paper plugin assigns names to all Villagers in the world. This includes baby Villagers upon birth as well as existing Villagers.

## Setup

The plugin comes with a configuration file preloaded with names. This may be edited to add your own names / remove the presets. It is located at `$SERVER_DIRECTORY/plugins/MVillageNamer/config.yml`. The format is as follows:

```yaml
debug-print: false # or true
names:
  - ["Benjamin", "Ben"]
  - James
  - "etc."
```

Each line is a string or array of strings, the latter of which can be used to list variants of the same name. Then, when that name is picked, a variant is picked randomly.

## License

Copyright (C) 2026 Maria Taylor

This project is licensed under the GNU General Public License, version 3 only.
See the LICENSE file for the full license text.
