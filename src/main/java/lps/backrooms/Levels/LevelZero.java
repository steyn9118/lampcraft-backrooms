package lps.backrooms.Levels;

import lps.backrooms.Backrooms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class LevelZero extends Arena {

    // СПЕЦИФИЧНЫЕ ЭТОМУ КЛАССУ
    int exitsAmount;
    int initialMonsterAmount;
    int bottlesAmount;
    int lightbulbsAmount;
    int phonesAmount;

    // ГРУППЫ ИГРОКОВ
    ArrayList<Player> monsters = new ArrayList<>();

    // МЕТОД ИНИЦИАЛИЗАЦИИ ИЗ КОНФИГА
    public void initFromCfgLocal(int exitsAmount, int initialMonsterAmount, int bottlesAmount, int lightbulbsAmount, int phonesAmount){

        this.exitsAmount = exitsAmount;
        this.initialMonsterAmount = initialMonsterAmount;
        this.bottlesAmount = bottlesAmount;
        this.lightbulbsAmount = lightbulbsAmount;
        this.phonesAmount = phonesAmount;

    }

    // ВЫХОД С АРЕНЫ
    @Override
    public void leave(Player p){

        // Выключение маскировки под монстра
        if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission settemp modelengine.* true 3s backrooms");
            BukkitRunnable afterDeathKit = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi sudo " + p.getName() + " meg undisguise");
                }
            };
            afterDeathKit.runTaskLater(plugin, 20);
        }

        super.leave(p);
    }

    // Смерть
    @Override
    public void death(Player p){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.ender_dragon.growl master @a[distance=..20] ~ ~ ~ 50 1");
        super.death(p);
    }

    // ПРЕВРАЩЕНИЕ В МОНСТРА
    public void playerBecameMonster(Player p){

        if (debug){
            System.out.println("Игрок стал монстром");
        }

        p.removePotionEffect(PotionEffectType.SPEED);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tag " + p.getName() + " add monster");
        p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "monster"));
        p.getInventory().clear();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit monster " + p.getName());

        for (Player player : Bukkit.getOnlinePlayers()){
            player.showPlayer(plugin, p);
        }

        ghosts.remove(p);
        monsters.add(p);

    }

    // Превращение в призрака
    @Override
    public void becameGhost(Player p){
        for (Player player : monsters){
            player.hidePlayer(plugin, p);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit monster_item " + p.getName());
        super.becameGhost(p);
    }


    // Спавн игрока
    @Override
    protected void spawnPlayer(Player p, Location pos){
        super.spawnPlayer(p, pos);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit level0_start " + p.getName());
    }

    // Начало игры
    @Override
    public void startGame(){
        for (Player p : players){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment level0_games +1");
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5]");
        spawnThings();
        super.startGame();
    }

    @Override
    public void win(Player p){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi usermeta " + p.getName() + " increment level0_wins +1");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi toast " + p.getName() + " -t:challenge -icon:yellow_wool &aПокинуть нулевой уровень");
        super.win(p);
    }

    // Спавн хуйни
    private void spawnThings(){

        if (debug){
            System.out.println("Спавн хуйни");
        }

        gameActive = true;

        // СПАВН ПРЕДМЕТОВ
        // БУТЫЛКИ
        for (int I = 0; I < bottlesAmount; I++){
            Location loc = getRandomPos(24, 0, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 0 255 0 0 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
        }

        // ЛАМПОЧКИ
        for (int I = 0; I < lightbulbsAmount; I++){
            Location loc = getRandomPos(24, 0, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 1 255 0 1 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
        }

        // ТЕЛЕФОННЫЕ ТРУБКИ
        for (int I = 0; I < phonesAmount; I++){
            Location loc = getRandomPos(24, 0, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "clone 2 255 0 2 255 0 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " minecraft:air destroy");
        }

        // СПАВН МОНСТРОВ
        for (int I = 0; I < players.size() + initialMonsterAmount; I++){
            Location loc = getRandomPos(40, 1, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn ebaka 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }

        // ЗАМЕДЛЕНИЕ МОНСТРОВ В НАЧАЛЕ ИГРЫ
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5] minecraft:slowness 20 5 false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tag @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5] add monster");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join monsters @e[type=minecraft:husk]");


        // СПАВН ЛЕСТНИЦ
        for (int i = 0; i < exitsAmount; i++){
            Location loc = getRandomPos(100, 1, borders, -1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn ladder 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }
    }

    // КОНЕЦ ИГРЫ
    @Override
    protected void stopGame(){

        // КИК ОСТАВШИХСЯ ИГРОКОВ-МОНСТРОВ
        if (monsters.size() > 0){
            int msize = monsters.size();
            for (int i = 0; i < msize; i++){
                leave(monsters.get(i));
            }
        }

        super.stopGame();

    }
}
