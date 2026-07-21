# MVillageNamer

This Paper plugin assigns names to all Villagers in the world. This includes baby Villagers upon birth as well as existing Villagers.

## Setup

The plugin comes with a configuration file preloaded with names. This may be edited to add your own names / remove the presets. It is located at `$SERVER_DIRECTORY/plugins/MVillageNamer/config.yml`. The format is as follows:

```yaml
log-level: FINE # SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST
names:
  - ["Benjamin", "Ben"]
  - ["James"]
  - ["etc."]
```

Each line is an array of names, to be used to list variants of the same name, and a variant is picked randomly.

To add a name with a single variant, at this time you must use an array with only one variant, rather than just the name on its own.

## License

Copyright (C) 2026 Maria Taylor

This project is licensed under the GNU General Public License, version 3 only.
See the LICENSE file for the full license text.
