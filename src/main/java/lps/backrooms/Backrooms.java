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


    // Загрузка арен из конфига
    public static void loadArenasFromConfig() {

        arenas.clear();

        File arenasFolder = new File(Backrooms.getPlugin().getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }
        File[] arenasFiles = arenasFolder.listFiles();

        assert arenasFiles != null;
        for (File file : arenasFiles) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (config.getInt("level") == 0){
                LevelZero arena = new LevelZero();

                // Локальные переменные
                int exitsAmount = config.getInt("exitsAmount");
                int initMonstersAmount = config.getInt("initialMonstersAmount");
                arena.initFromCfgLocal(exitsAmount, initMonstersAmount);

                // Абстрактные переменные
                boolean debug = config.getBoolean("debug");
                String id = config.getString("id");
                int maxPlayers = config.getInt("maxPlayers");
                int maxTime = config.getInt("maxTime");
                Location hubLocation = config.getLocation("hubLocation");
                Sound music = Sound.valueOf(config.getString("music"));
                int musicLenght = config.getInt("ambientMusicLenght");

                List<Integer> floorsY = config.getIntegerList("floorsY");
                List<Integer> borders = config.getIntegerList("borders");

                arena.initFromCfgAbstract(id, maxPlayers, maxTime, borders, floorsY, hubLocation, music, musicLenght, debug);
                arenas.add(arena);
            }
            else if (config.getInt("level") == 1){
                LevelOne arena = new LevelOne();

                // Абстрактные переменные
                boolean debug = config.getBoolean("debug");
                String id = config.getString("id");
                int maxPlayers = config.getInt("maxPlayers");
                int maxTime = config.getInt("maxTime");
                Location hubLocation = config.getLocation("hubLocation");
                Sound music = Sound.valueOf(config.getString("music"));
                int musicLenght = config.getInt("ambientMusicLenght");

                List<Integer> floorsY = config.getIntegerList("floorsY");
                List<Integer> borders = config.getIntegerList("borders");

                arena.initFromCfgAbstract(id, maxPlayers, maxTime, borders, floorsY, hubLocation, music, musicLenght, debug);
                arenas.add(arena);
            }
            else {
                System.out.println("[ERR] Ошибка в конфигурации арены: несуществующее значеие переменной 'level'");
            }

        }
    }
}