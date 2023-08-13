package lps.backrooms.Levels;

import lps.backrooms.Backrooms;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LevelZero extends Arena {

    // СПЕЦИФИЧНЫЕ ЭТОМУ КЛАССУ
    int exitsAmount;
    int initialMonsterAmount;

    // ГРУППЫ ИГРОКОВ
    ArrayList<Player> monsters = new ArrayList<>();

    // МЕТОД ИНИЦИАЛИЗАЦИИ ИЗ КОНФИГА
    public void initFromCfgLocal(int exitsAmount, int initialMonsterAmount){

        this.exitsAmount = exitsAmount;
        this.initialMonsterAmount = initialMonsterAmount;

    }

    // ВЫХОД С АРЕНЫ
    public void leave(Player p){

        // Выключение маскировки под монстра
        if (p.getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " permission settemp modelengine.* true 2s backrooms");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi sudo " + p.getName() + " meg undisguise v1.5");
        }

        // showPlayer игрокам монстрам


        super.leave(p);
    }

    // Смерть
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
    public void becameGhost(Player p){
        for (Player player : monsters){
            player.hidePlayer(plugin, p);
        }
        super.becameGhost(p);
    }

    // Начало игры
    public void startGame(){
        spawnThings();
        super.startGame();
    }

    // Спавн хуйни
    private void spawnThings(){

        if (debug){
            System.out.println("Спавн хуйни");
        }

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
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tag @e[type=minecraft:husk" +
                ",x=" + borders.get(0).toString() + ",dx=" + (borders.get(1) - borders.get(0)) +
                ",z=" + borders.get(2).toString() + ",dz=" + (borders.get(3) - borders.get(2)) +
                ",y=" + floorsY.get(0) + ",dy=5] add monster");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team join monsters @e[type=minecraft:husk]");


        // СПАВН ЛЕСТНИЦ
        for (int i = 0; i < exitsAmount; i++){
            Location loc = getRandomPos(100, 1, false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm m spawn ladder 1 " + "world" + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ",0,0");
        }
    }

    // КОНЕЦ ИГРЫ
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
