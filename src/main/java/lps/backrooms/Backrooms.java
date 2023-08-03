package lps.backrooms;

import lps.backrooms.Levels.Arena;
import lps.backrooms.Levels.LevelOne;
import lps.backrooms.Levels.LevelZero;
import lps.backrooms.commands.adminCommands;
import lps.backrooms.commands.backroomsCommands;
import lps.backrooms.commands.partyCommands;
import lps.backrooms.listeners.ItemRelatedListeners;
import lps.backrooms.listeners.playerJoinListener;
import lps.backrooms.listeners.playerMovementListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Backrooms extends JavaPlugin {

    public static Backrooms plugin;
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public ArrayList<Party> parties = new ArrayList<>();


    public ArrayList<Party> getParties(){
        return this.parties;
    }

    public ArrayList<Arena> getArenas(){return arenas;}

    public static Backrooms getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new playerMovementListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new playerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemRelatedListeners(), this);
        getCommand("br").setExecutor(new backroomsCommands());
        getCommand("party").setExecutor(new partyCommands());
        getCommand("bradmin").setExecutor(new adminCommands());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        loadArenasFromConfig();
    }


    public static void loadArenasFromConfig() {

        arenas.clear();

        File arenasFolder = new File(Backrooms.getPlugin().getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }
        File[] arenasFiles = arenasFolder.listFiles();

        assert arenasFiles != null;
        if (arenasFiles.length == 0) {
            return;
        }
        for (File file : arenasFiles) {

            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            if (configuration.getInt("level") == 0){
                LevelZero arena = new LevelZero();
                int exitsAmount = configuration.getInt("exitsAmount");
                int initMonstersAmount = configuration.getInt("initialMonstersAmount");
                arena.initFromCfgLocal(exitsAmount, initMonstersAmount);
                // VARIABLES
                String id = configuration.getString("id");
                int maxPlayers = configuration.getInt("maxPlayers");
                int maxTime = configuration.getInt("maxTime");
                Location hubLocation = configuration.getLocation("hubLocation");
                Sound music = Sound.valueOf(configuration.getString("music"));

                List<Integer> floorsY = configuration.getIntegerList("floorsY");
                List<Integer> borders = configuration.getIntegerList("borders");

                arena.initFromCfgAbstract(id, maxPlayers, maxTime, borders, floorsY, hubLocation, music);
                arenas.add(arena);
            }
            else if (configuration.getInt("level") == 1){
                LevelOne arena = new LevelOne();

                // VARIABLES
                String id = configuration.getString("id");
                int maxPlayers = configuration.getInt("maxPlayers");
                int maxTime = configuration.getInt("maxTime");
                Location hubLocation = configuration.getLocation("hubLocation");
                Sound music = Sound.valueOf(configuration.getString("music"));

                List<Integer> floorsY = configuration.getIntegerList("floorsY");
                List<Integer> borders = configuration.getIntegerList("borders");

                arena.initFromCfgAbstract(id, maxPlayers, maxTime, borders, floorsY, hubLocation, music);
                arenas.add(arena);
            }
            else {
                System.out.println("Ошибка в конфигурации арены: invalid level");
                continue;
            }

        }
    }
}