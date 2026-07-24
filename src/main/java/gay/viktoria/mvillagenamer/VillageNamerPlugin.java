/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */
package gay.viktoria.mvillagenamer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
//import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.logging.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.Chunk;


public class VillageNamerPlugin extends JavaPlugin implements Listener {
    
    //private VillagerDataManager vdmanager; //static or nah?

    //public final MiniMessage mm = MiniMessage.miniMessage();

    private NameGenerator NameGen;

    private boolean debugEnabled;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        //this.getLogger();
        getLogger().log(Level.INFO, "Registered events");

        // saveResource("config.yml", /* replace */ false); // shouldn't be necessary with the below
        saveDefaultConfig();

        // load the log verbosity from config
        String logverbLevelName = getConfig().getString("log-level");
        Level logLevel;
        try {
            logLevel = Level.parse(logverbLevelName);
        } catch (IllegalArgumentException e) {
            logLevel = Level.INFO;
            getLogger().warning("Invalid log level in config, defaulting to INFO");
        }
        
        getLogger().setLevel(logLevel);
        getLogger().info(String.format("Loaded log verbosity level from config: %s", logLevel.toString()));

        // end log verb section

        // Load debug boolean
        debugEnabled = getConfig().getBoolean("debug-print", false);
        getLogger().info("Loaded debug printing info from config: %s".formatted(Boolean.toString(debugEnabled)));

        // Register the /namechunk command
        this.getCommand("namechunk").setExecutor(new NameChunkCommand(this));
        getLogger().info("Successfully registered the /namechunk command");
        this.getCommand("renametag").setExecutor(new ReNameTagCommand());
        getLogger().info("Successfully registered the /renametag command");

        // TODO: stick that when right-clicked on villager renames them


        int lNsuccess = loadNames();
        switch (lNsuccess) {
            case 0: // success
                getLogger().log(Level.INFO, "Names successfully loaded from config");
                break;
            case 1: // failure
                getLogger().log(Level.WARNING, "Failed to load names from config!");
            default:
                break;
        }

        // Name existing villagers on startup
        assignNamesToExistingVillagers();

        // Schedule periodic check for new villagers that weren't caught by one of the EventHandlers
        startPeriodicNameCheck();
    }

    private int loadNames() {
        var names = getConfig().getList("names");
        if (names == null || names.isEmpty()) {
            getLogger().log(Level.WARNING, "Was not able to load any names from config.yml. Perhaps it is empty?");
            return 1;
        }

        
        CircularList<ArrayList<String>> namesCircList = new CircularList<ArrayList<String>>();

        for (var member : names.toArray()) {
            ArrayList<String> nameSubArr = new ArrayList<>();

            if (member instanceof String s) {
                nameSubArr.add(s);
            }
            else if (member instanceof List<?> l && l.stream().allMatch(String.class::isInstance)) {
                @SuppressWarnings("unchecked")
                List<String> strings = (List<String>) l;

                nameSubArr.addAll(strings);
            }

            namesCircList.add(nameSubArr);
            debug(() -> "Found name: %s".formatted(nameSubArr.toString()));
        }

        if (namesCircList.isEmpty()) {
            getLogger().log(Level.WARNING, "Was not able to load any names from config.yml. Perhaps it is empty?");
            return 1;
        }

        NameGen = new NameGenerator(namesCircList);

        return 0;
    }

    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            checkThenNameVillager(villager);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        nameChunk(chunk);
    }

    public void nameChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Villager) {
                Villager villager = (Villager) entity;
                checkThenNameVillager(villager);
            }
        }
    }

    private void startPeriodicNameCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                assignNamesToLoadedVillagers();
            }
        }.runTaskTimer(this, 0L, 20L * 900); // Run every 900 seconds (15 minutes)
    }

    private void assignNamesToLoadedVillagers() {
        @SuppressWarnings("unchecked")
        List<Player> onlinePlayers = (List<Player>) (getServer().getOnlinePlayers());
        for (Player player : onlinePlayers) {
            Chunk chunk = player.getChunk();
            nameChunk(chunk);
        }
    }

    private void assignNamesToExistingVillagers() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(Villager.class)) {
                Villager villager = (Villager) entity;
                checkThenNameVillager(villager);
            }
        }
    }

    private void checkThenNameVillager(Villager villager) {
        Component cname = villager.customName();
        String cnamestr = PlainTextComponentSerializer.plainText().serializeOrNull(cname);

        String customNameForLog = "<NO CUSTOM NAME>";
        String plaintextForLog = "<NO CUSTOM NAME>";
        if (cnamestr != null) {
            customNameForLog = cname.toString();
            plaintextForLog = cnamestr;
        }
        getLogger().fine(String.format("nameChunk: Found villager %s with custom name: '%s' ---- or as plain text, '%s'",
                villager.getUniqueId().toString(), customNameForLog, plaintextForLog));
        getLogger().finer("cnamestr: " + cnamestr); // comment out or make fine later
        if (cname == null || cnamestr == null || cnamestr.isEmpty() || cnamestr.equals("RENAMEME")) {
            getLogger().finer("Naming villager..."); // comment out or make fine when later
            nameVillager(villager);
        }
    }

    private void nameVillager(Villager villager) {
        String name = NameGen.getName();
        //villager.setCustomName(name);
        villager.customName(null); // clear name first
        villager.customName(Component.text(name));
        villager.setCustomNameVisible(false);
        
        // Log info message every time a name is set for a villager. Possibly disable later...
        // ... or make configurable.
        getLogger().info("Applied name '" + name + "' to villager " + villager.getUniqueId().toString());
    }

    public void debug(Supplier<String> messageSupplier) {
        if (debugEnabled) {
            getLogger().info("[DEBUG] " + messageSupplier.get());
        }
    }
}
