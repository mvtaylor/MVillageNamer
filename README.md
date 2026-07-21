# MVillageNamer

This Paper plugin assigns names to all Villagers in the world. This includes baby Villagers upon birth as well as existing Villagers.

## Setup

The plugin comes with a configuration file preloaded with names. This may be edited to add your own names / remove the presets. It is located at `$SERVER_DIRECTORY/plugins/MVillageNamer/config.yml`. The format is as follows:

```yaml
log-level: FINE # SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST
names:
  - ["Benjamin", "Ben"]
  - ["James"]
```

Each line is an array of names, to be used to list variants of the same "parent" name, and a variant is picked randomly.

To add a name with a single variant, you must use an array with only one member, rather than just the name on its own.
