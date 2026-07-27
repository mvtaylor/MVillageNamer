/*
 * Copyright (C) 2026 Maria Taylor
 * SPDX-License-Identifier: GPL-3.0-only
 */
package gay.viktoria.mvillagenamer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
    
    private NameGenerator NameGen;

    private boolean debugEnabled;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "Registered events");

        saveDefaultConfig();

        // Load debug-printing enabled/disabled
        debugEnabled = getConfig().getBoolean("debug-print", false);
        getLogger().info("Loaded debug printing info from config: %s".formatted(Boolean.toString(debugEnabled)));

        // Register the /namechunk command
        this.getCommand("namechunk").setExecutor(new NameChunkCommand(this));
        getLogger().info("Successfully registered the /namechunk command");
        this.getCommand("renametag").setExecutor(new ReNameTagCommand());
        getLogger().info("Successfully registered the /renametag command");

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            VNamesCommand vncmd = new VNamesCommand(this);
            commands.registrar().register(vncmd.createCommand(), "Manage villager names");
        });


        int lNsuccess = loadNames();
        switch (lNsuccess) {
            case 0: // success
                getLogger().log(Level.INFO, "Names successfully loaded from config");
                break;
            case 1: // failure
                getLogger().log(Level.SEVERE, "Failed to load names from config! Disabling.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            default:
                break;
        }

        // Name existing villagers on startup
        assignNamesToExistingVillagers();

        // Schedule periodic check for new villagers that weren't caught by one of the EventHandlers
        startPeriodicNameCheck();
    }

    int loadNames() {
        try {
            final CircularList<ArrayList<String>> names = getConfigNames();
            NameGen = new NameGenerator(names);

            return 0;
        } catch (IllegalStateException e) {
            getLogger().log(Level.SEVERE, "Was not able to load any names from config.yml. Perhaps it is empty?");
            return 1;
        }
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

        final String customNameForLog;
        final String plaintextForLog;
        if (cnamestr != null) {
            customNameForLog = cname.toString();
            plaintextForLog = cnamestr;
        } else {
            customNameForLog = "<NO CUSTOM NAME>";
            plaintextForLog = "<NO CUSTOM NAME>";
        }
        debug(() -> String.format("nameChunk: Found villager %s with custom name: '%s' ---- or as plain text, '%s'",
                villager.getUniqueId().toString(), customNameForLog, plaintextForLog));
        debug(() -> "cnamestr: " + cnamestr);
        if (cname == null || cnamestr == null || cnamestr.isEmpty() || cnamestr.equals("RENAMEME")) {
            debug(() -> "Naming villager...");
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

    CircularList<ArrayList<String>> getConfigNames() throws IllegalStateException {
        var names = getConfig().getList("names");
        if (names == null || names.isEmpty()) {
            // Shouldn't reach this point because .getList() should return the default config list if the "names" path doesn't exist or it is empty
            throw new IllegalStateException("Was not able to load any names from config.yml. Perhaps it is empty?");
            //return 1;
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
            throw new IllegalStateException("Was not able to load any names from config.yml. Perhaps it is empty?");
            //return 1;
        }

        return namesCircList;
    }

    public void addName(String name) {
        List<?> l = getConfig().getList("names");
        if (!l.stream().allMatch(Object.class::isInstance)) {
            return;
        };

        @SuppressWarnings("unchecked")
        List<Object> names = (List<Object>) l;
        names.add(name);

        getConfig().set("names", names);
        
        saveConfig();

        reloadConfig();

        loadNames();
    }

    public void addName(List<String> nameVariants) {
        List<?> l = getConfig().getList("names");
        if (!l.stream().allMatch(Object.class::isInstance)) {
            return;
        }
        ;

        @SuppressWarnings("unchecked")
        List<Object> names = (List<Object>) l;

        List<String> newNames = new ArrayList<>();
        newNames.addAll(nameVariants);

        names.add(newNames);

        getConfig().set("names", names);
        saveConfig();
        reloadConfig();
        loadNames();
    }

    public void removeName(String name) {
        List<?> l = getConfig().getList("names");
        if (!l.stream().allMatch(Object.class::isInstance)) {
            throw new IllegalStateException("All list members must be Object's");
        }

        @SuppressWarnings("unchecked")
        List<Object> names = (List<Object>) l;

        // int numRemoved = 0;

        names.forEach(obj -> {
            if (obj instanceof String s && s.equals(name)) {
                names.remove(obj);
            }
            else if (obj instanceof List<?> variants) {
                variants.forEach(variant -> {
                    if (variant instanceof String s && s.equals(name)) {
                        variants.remove(variant);
                    }
                });
            } 
        });

        getConfig().set("names", names);
        saveConfig();
        reloadConfig();
        loadNames();
    }
}
