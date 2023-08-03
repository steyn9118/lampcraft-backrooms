package lps.backrooms.Levels;

import lps.backrooms.Backrooms;
import lps.backrooms.Party;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LevelZero extends Arena {

    // СПЕЦИФИЧНЫЕ ЭТОМУ КЛАССУ
    int exitsAmount;
    int initialMonsterAmount;

    // СЛУЖЕБНЫЕ ПЕРЕМЕННЫЕ
    public int currentTime;
    boolean gameActive = false;
    Random random = new Random();
    int[] previousLocationXZ = new int[2];
    List<Location> randomLocsCache = new ArrayList<>();

    // ГРУППЫ ИГРОКОВ
    ArrayList<Player> monsters = new ArrayList<>();

    // МЕТОД ИНИЦИАЛИЗАЦИИ ИЗ КОНФИГА
    public void initFromCfgLocal(int exitsAmount, int initialMonsterAmount){

        this.exitsAmount = exitsAmount;
        this.initialMonsterAmount = initialMonsterAmount;

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
            p.setMetadata("br_arena", new FixedMetadataValue(Backrooms.getPlugin(), this.id));
            p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "alive"));

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
    public void leave(Player p, Boolean isWining){

        if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill " + p.getName());
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team leave " + p.getName());
        p.setMetadata("br_arena", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
        p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "null"));

        p.getInventory().clear();
        p.teleport(hubLocation);
        p.stopSound(Sound.MUSIC_CREDITS);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit lobby " + p.getName());

        // ЕСЛИ ИГРОК ПОБЕДИЛ
        if (isWining) {

            p.sendMessage(ChatColor.GREEN + "Вы победили");
            players.remove(p);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi toast " + p.getName() + " -t:challenge -icon:yellow_wool &aПокинуть первый уровень");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment wins +1");

        }

        // ЕСЛИ ПРОИГРАЛ
        else {

            p.sendMessage(ChatColor.RED + "Вы проиграли");

            if (players.contains(p)) {
                players.remove(p);
            } else {
                ghosts.remove(p);
            }
        }
    }

    // СМЕРТЬ ИГРОКА
    public void death(Player p){

        // ЕСЛИ ИГРОК БЫЛ ПОСЛЕДНИМ
        if (players.size() == 1){
            leave(p, false);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join monsters " + p.getName());
        p.sendTitle(ChatColor.RED + "Вы стали призраком", "Игроки и монстры не видят вас");
        p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "dead"));

        players.remove(p);
        ghosts.add(p);

        // ТЕЛЕПОРТАЦИЯ НА МЕСТО СМЕРТИ
        Location deathLoc = p.getLocation();
        BukkitRunnable afterDeathTeleport = new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("null")){
                    return;
                }
                p.teleport(deathLoc);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit spectate " + p.getName());
            }
        };
        afterDeathTeleport.runTaskLater(Backrooms.getPlugin(), 2 * 20);
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

    // ПРЕВРАЩЕНИЕ В МОНСТРА
    public void playerBecameMonster(Player p){

        p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "monster"));
        p.getInventory().clear();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit monster " + p.getName());

        ghosts.remove(p);
        monsters.add(p);

    }

    private void startGame(){

        gameActive = true;

        // СПАВН ПРЕДМЕТОВ
        for (int I = 0; I < 24; I++){
            Location loc = getRandomPos(24, 0, false);

            // БУТЫЛКИ
            if (I < 12){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 0 255 0 0 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
            }

            // ЛАМПОЧКИ
            if (I > 11 && I < 20){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 1 255 0 1 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
            }

            // ТЕЛЕФОННЫЕ ТРУБКИ
            if (I > 19){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 2 255 0 2 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
            }
        }

        // СПАВН МОНСТРОВ
        for (int I = 0; I < players.size() + 5; I++){
            Location loc = getRandomPos(40, 1, false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn ebaka 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }

        // ЗАМЕДЛЕНИЕ МОНСТРОВ В НАЧАЛЕ ИГРЫ
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5] minecraft:slowness 20 5 false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join monsters @e[type=minecraft:husk]");

        // СПАВН ЛЕСТНИЦ
        for (int i = 0; i < exitsAmount; i++){
            Location loc = getRandomPos(64, 1, false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn ladder 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }


        // НАЧАЛО ОТСЧЁТА
        currentTime = -1;
        BukkitRunnable game = new BukkitRunnable() {
            @Override
            public void run() {

                currentTime += 1;

                // ЭМБИЕНТ
                if (currentTime % 210 == 0 || currentTime == 0){
                    for (Player p : players){
                        p.playSound(p.getLocation(), Sound.MUSIC_CREDITS, SoundCategory.MASTER, 100f, 1f);
                    }
                }

                // ПРОВЕРКА НА ЗАВЕРШЕНИЕ ИГРЫ
                if (currentTime == maxTime || players.size() == 0){
                    stopGame();
                    this.cancel();
                }
            }
        };
        game.runTaskTimer(Backrooms.getPlugin(), 0, 20);
    }

    // КОНЕЦ ИГРЫ
    private void stopGame(){

        gameActive = false;
        spectatingCounters.clear();

        // УБИЙСТВО МОНСТРОВ
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5]");

        // УБИЙСТВО ПРЕДМЕТОВ
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:item" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5]");

        // УБИЙСТВО ВЫХОДОВ
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:wither_skeleton" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5]");

        // КИК ОСТАВШИХСЯ ИГРОКОВ
        if (players.size() > 0){
            int psize = players.size();
            for (int i = 0; i < psize; i++){
                leave(players.get(0), false);
            }
        }

        // КИК ОСТАВШИХСЯ НАБЛЮДАТЕЛЕЙ
        if (ghosts.size() > 0){
            int gsize = ghosts.size();
            for (int i = 0; i < gsize; i++){
                leave(ghosts.get(0), false);
            }
        }

        // КИК ОСТАВШИХСЯ ИГРОКОВ-МОНСТРОВ
        if (monsters.size() > 0){
            int msize = monsters.size();
            for (int i = 0; i < msize; i++){
                leave(monsters.get(i), false);
            }
        }
    }
}
