package lps.backrooms;

import lps.backrooms.Levels.Arena;
import lps.backrooms.Levels.LevelOne;
import lps.backrooms.Levels.LevelZero;
import lps.backrooms.blockfilling.BlockFillingQueue;
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

@SuppressWarnings("DataFlowIssue")
public final class Backrooms extends JavaPlugin {

    private static Backrooms plugin;
    private final BlockFillingQueue blockFillingQueue = new BlockFillingQueue();
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public ArrayList<Party> parties = new ArrayList<>();

    public ArrayList<Party> getParties(){
        return this.parties;
    }

    public ArrayList<Arena> getArenas(){return arenas;}

    public static Backrooms getPlugin(){
        return plugin;
    }

    public BlockFillingQueue getBlockFillingQueue() {
        return blockFillingQueue;
    }

    @Override
    public void onEnable() {

        plugin = this;
        blockFillingQueue.init();


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
                int bottlesAmount = config.getInt("bottlesAmount");
                int lightbulbsAmount = config.getInt("lightbulbsAmount");
                int phonesAmount = config.getInt("phonesAmount");
                arena.initFromCfgLocal(exitsAmount, initMonstersAmount, bottlesAmount, lightbulbsAmount, phonesAmount);

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

                // Локальные переменные
                int initialMonsterAmount = config.getInt("initialMonsterAmount");
                int gasStationsAmount = config.getInt("gasStationsAmount");
                int generatorsAmount = config.getInt("generatorsAmount");
                int whrenchAmount = config.getInt("whrenchAmount");
                int methAmount = config.getInt("methAmount");
                int genFillingAmount = config.getInt("genFillingAmount");
                int lightsOutDuration = config.getInt("lightsOutDuration");
                int generatorsRequired = config.getInt("generatorRequired");
                Integer[] lightsFillPos1 = config.getIntegerList("lightsFillPos1").toArray(new Integer[3]);
                Integer[] lightsFillPos2 = config.getIntegerList("lightsFillPos2").toArray(new Integer[3]);
                arena.initFromCfgLocal(initialMonsterAmount,gasStationsAmount, generatorsAmount, whrenchAmount, methAmount, genFillingAmount, lightsOutDuration, generatorsRequired, lightsFillPos1, lightsFillPos2);

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