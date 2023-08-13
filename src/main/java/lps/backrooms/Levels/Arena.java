package lps.backrooms.Levels;

import lps.backrooms.Backrooms;
import lps.backrooms.Party;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
Абстрактный класс, используется только как родитель при наследовании классами уровней

Возможные состояния игрока ("br_player_state"): alive, ghost, (monster только на LevelZero)
*/

public class Arena {

    // ПЕРЕМЕННЫЕ ИЗ КОНФИГА
    String id;
    int maxPlayers;
    int maxTime;
    List<Integer> borders;
    List<Integer> floorsY;
    Location hubLocation;
    Sound backgroundMusic;
    int backgroundMusicLenght; // 210 на нулевом уровне


    // СЛУЖЕБНЫЕ ПЕРЕМЕННЫЕ
    private final Backrooms plugin = Backrooms.getPlugin();
    public int currentTime;
    boolean gameActive = false;
    Random random = new Random();
    int[] previousLocationXZ = new int[2];
    // TODO кэш случайных локаций (низкий приоритет)
    //List<Location> randomLocsCache = new ArrayList<>();


    // ГРУППЫ ИГРОКОВ
    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Player> ghosts = new ArrayList<>();
    Map<Player, Integer> spectatingCounters = new HashMap<>();


    // СТАТУС-МЕТОДЫ
    public boolean isGameActive(){ return this.gameActive; }
    public String getId(){
        return this.id;
    }
    public ArrayList<Player> getPlayers(){
        return this.players;
    }


    // ОбЩИЙ МЕТОД ИНИЦИАЛИЗАЦИИ ИЗ КОНФИГА
    public void initFromCfgAbstract(String id, int maxPlayers, int maxTime, List<Integer> borders, List<Integer> floorsY, Location hubLocation, Sound music, int backgroundMusicLenght){

        this.id = id;
        this.maxPlayers = maxPlayers;
        this.maxTime = maxTime;

        this.borders = borders;
        this.floorsY = floorsY;
        this.hubLocation = hubLocation;

        this.backgroundMusic = music;
        this.backgroundMusicLenght = backgroundMusicLenght;

    }

    // ПОЛУЧЕНИЕ СЛУЧАЙНОЙ ПОЗИЦИИ
    protected Location getRandomPos(int minDistanceFromPrev, int emptyBlocksAroundRadius, boolean clearCache){

        /*
        Кэш локаций в данный момент не используется
        if (clearCache){
            randomLocsCache.clear();
        }
        */

        int x;
        int z;
        boolean repeat;
        int floorY = floorsY.get(0);

        do {

            /*
            // ПРОВЕРКА ДЛЯ УРОВНЕЙ С НЕСКОЛЬКИМИ ЭТАЖАМИ
            // НА ДАННЫЙ МОМЕНТ НЕ ЗАКОНЧЕНА, ТАК КАК НЕ ИСПОЛЬЗУЕТСЯ

            if (floorsY.size() != 1) {
                floorY = floorsY.get(random.nextInt(floorsY.size()-1));
            }
            */

            x = borders.get(0) + random.nextInt(borders.get(1) - borders.get(0));
            z = borders.get(2) + random.nextInt(borders.get(3) - borders.get(2));

            // ПРОВЕРКА НА РАССТОЯНИЕ ОТ ПРЕДЫДУЩЕГО СПАВНА
            if(minDistanceFromPrev > 0){
                if (( (x - previousLocationXZ[0])^2 + (z - previousLocationXZ[1])^2 ) < minDistanceFromPrev*minDistanceFromPrev){
                    continue;
                }
            }

            // НЕ ПУСТОЙ БЛОК НА ЭТИХ КООРДИНАТАХ
            if (!(new Location(Bukkit.getWorld("world"),  x, floorY, z).getBlock().isEmpty())){
                    continue;
            }

            // ПРОВЕРКА НА ДОСТАТОЧНОЕ КОЛИЧЕСТВА ПУСТЫХ БЛОКОВ ВОКРУГ
            if (emptyBlocksAroundRadius > 0){
                repeat = false;
                for (int dx = -emptyBlocksAroundRadius; dx <= emptyBlocksAroundRadius; dx += 1){
                    for (int dz = -emptyBlocksAroundRadius; dz <= emptyBlocksAroundRadius; dz += 1){
                        if (!(new Location(Bukkit.getWorld("world"), x + dx, floorY, z + dz).getBlock().isEmpty())){
                            repeat = true;
                            break;
                        }
                    }
                }

                if (repeat){continue;}

            }

            previousLocationXZ[1] = z;
            previousLocationXZ[0] = x;
            return new Location(Bukkit.getWorld("world"), x, floorY, z);

        } while (true);

    }

    // ПРИСОЕДИНЕНИЕ ПАТИ К АРЕНЕ
    public void join(Party party){

        if (party.getPlayers().size() > maxPlayers || gameActive){
            party.getLeader().sendMessage(ChatColor.RED + "Эта арена занята, попробуйте выбрать другую");
            return;
        }

        // СПАВН ИГРОКОВ
        Location loc = getRandomPos(32, 0, false);

        for (Player p : party.getPlayers()) {

            players.add(p);
            spectatingCounters.put(p, 0);
            p.getInventory().clear();
            p.setMetadata("br_arena", new FixedMetadataValue(plugin, this.id));
            p.setMetadata("br_player_state", new FixedMetadataValue(plugin, "alive"));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit gameStart " + p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment games +1");
            p.sendMessage(ChatColor.GREEN + "Вы присоединились к арене");

            // ПРОВЕРКА НА СПАВН ВСЕХ ИГРОКОВ В ОДНОМ МЕСТЕ
            if (party.getSpawnSettings()){
                p.teleport(loc);
            } else {
                p.teleport(getRandomPos(32, 0, false));
            }
        }

        startGame();
    }

    // ВЫХОД С АРЕНЫ
    public void leave(Player p){
        if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            for (Player ghost : ghosts){
                p.showPlayer(ghost);
            }
        } else if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("ghost")){
            for (Player player : players){
                player.showPlayer(p);
            }
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team leave " + p.getName());
        p.setMetadata("br_arena", new FixedMetadataValue(plugin, "null"));
        p.setMetadata("br_player_state", new FixedMetadataValue(plugin, "null"));
        p.getInventory().clear();
        p.teleport(hubLocation);
        p.stopSound(backgroundMusic);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit lobby " + p.getName());
    }

    // Победа
    public void win(Player p){

        if (!p.getMetadata("br_player_state").get(0).asString().equals("alive")){
            p.sendMessage(ChatColor.RED + "Вы уже не можете победить!");
            return;
        }

        p.sendMessage(ChatColor.GREEN + "Вы победили");
        players.remove(p);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi toast " + p.getName() + " -t:challenge -icon:yellow_wool &aПокинуть первый уровень");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment wins +1");

        if (players.size() > 0){
            becameGhost(p);
        }
    }

    // Превращение в призрака
    protected void becameGhost(Player p){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join ghosts " + p.getName());
        p.sendTitle(ChatColor.RED + "Вы стали призраком", "Игроки и монстры не видят вас");
        p.setMetadata("br_player_state", new FixedMetadataValue(plugin, "ghost"));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999*20, 1, false, false, false));
        for (Player player : players){
            player.hidePlayer(plugin, p);
        }
        for (Player player : ghosts){
            p.showPlayer(plugin, player);
        }
        ghosts.add(p);
    }

    // СМЕРТЬ ИГРОКА
    public void death(Player p){

        p.sendMessage(ChatColor.RED + "Вы проиграли");
        players.remove(p);

        // ЕСЛИ ИГРОК БЫЛ ПОСЛЕДНИМ
        if (players.size() == 0){
            return;
        }

        becameGhost(p);

        // ТЕЛЕПОРТАЦИЯ НА МЕСТО СМЕРТИ
        Location deathLoc = p.getLocation();
        BukkitRunnable afterDeathTeleport = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("ghost")){
                    return;
                }
                p.teleport(deathLoc);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit spectate " + p.getName());
            }
        };
        afterDeathTeleport.runTaskLater(plugin, 2 * 20);
    }

    // НАБЛЮДЕНИЕ ЗА ИГРОКАМИ
    public void spectate(Player p){

        // ЕСЛИ ПОПАДАЕТ НА САМОГО СЕБЯ
        if (players.get(spectatingCounters.get(p)).equals(p)){
            spectatingCounters.put(p, spectatingCounters.get(p) + 1);
        }

        // ЕСЛИ ВЫХОДИТ ЗА ГРАНИЦУ КОЛИЧЕСТВА ИГРОКОВ
        if (spectatingCounters.get(p) >= players.size()){
            spectatingCounters.put(p, 0);
        }

        p.teleport(players.get(spectatingCounters.get(p)));
        p.sendActionBar(ChatColor.GREEN + "Вы наблюдаете за игроком: " + ChatColor.YELLOW + players.get(spectatingCounters.get(p)).getName());
    }

    // Старт игры
    protected void startGame(){

        gameActive = true;

        // НАЧАЛО ОТСЧЁТА
        currentTime = -1;
        BukkitRunnable game = new BukkitRunnable() {
            @Override
            public void run() {

                currentTime += 1;

                // ЭМБИЕНТ
                if (currentTime % backgroundMusicLenght == 0 || currentTime == 0){
                    for (Player p : players){
                        p.playSound(p.getLocation(), backgroundMusic, SoundCategory.AMBIENT, 100f, 1f);
                    }
                }

                // ПРОВЕРКА НА ЗАВЕРШЕНИЕ ИГРЫ
                if (currentTime == maxTime || players.size() == 0){
                    stopGame();
                    this.cancel();
                }
            }
        };
        game.runTaskTimer(plugin, 0, 20);
    }

    // КОНЕЦ ИГРЫ
    protected void stopGame(){

        gameActive = false;
        spectatingCounters.clear();

        // КИК ОСТАВШИХСЯ ИГРОКОВ
        if (players.size() > 0){
            int psize = players.size();
            for (int i = 0; i < psize; i++){
                leave(players.get(0));
            }
        }

        // КИК ОСТАВШИХСЯ НАБЛЮДАТЕЛЕЙ
        if (ghosts.size() > 0){
            int gsize = ghosts.size();
            for (int i = 0; i < gsize; i++){
                leave(ghosts.get(0));
            }
        }

        players.clear();
        ghosts.clear();

        // Убийство предметов/сущностей
        for (int floorY : floorsY){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=!minecraft:player" +
                    ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                    ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                    ",y=" + floorY + ",dy=5]");
        }
    }
}
