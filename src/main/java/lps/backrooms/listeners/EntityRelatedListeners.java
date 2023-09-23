package lps.backrooms.listeners;

import lps.backrooms.Backrooms;
import lps.backrooms.Levels.Arena;
import lps.backrooms.Levels.LevelOne;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityRelatedListeners implements Listener {

    // Предотвращает нанесение урона всем неживым игрокам
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){

        // Если бьют не живого игрока
        if (event.getEntity() instanceof Player && !event.getEntity().getMetadata("br_player_state").get(0).asString().equals("alive")){
            event.setCancelled(true);
            return;
        }

        // Если бьёт монстр
        if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)){

            // Проигрывание звуков смерти
            Player p = (Player) event.getEntity();
            Entity damager = event.getDamager();

            // Безликая
            if (damager.getType().equals(EntityType.WITHER_SKELETON)){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.phantom.flap master @a[distance=..20] ~ ~ ~ 100 1");
            }

            // Смайлер
            if (damager.getType().equals(EntityType.ZOMBIE)){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.phantom.hurt master @a[distance=..20] ~ ~ ~ 100 1");
            }

            // Монстр нулевого уронвя
            if (damager.getType().equals(EntityType.HUSK)){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.ender_dragon.growl master @a[distance=..20] ~ ~ ~ 100 1");
            }
            return;
        }

        // Если бьёт игрок-монстр
        if ((event.getEntity() instanceof Player && event.getDamager() instanceof Player && event.getDamager().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster"))){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + event.getEntity().getName() + " run playsound minecraft:entity.ender_dragon.growl master @a[distance=..20] ~ ~ ~ 100 1");
        } else {
            event.setCancelled(true);
        }
    }

    // Для генерератора
    @EventHandler
    public void onPlayerEntityInteractEvent(PlayerInteractEntityEvent event){

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (!entity.getMetadata("time_left").isEmpty()){

            // Заправка генератора
            for (Arena arena : Backrooms.getPlugin().getArenas()){
                if (!player.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
                    continue;
                }
                if (arena instanceof LevelOne){
                    LevelOne temp_arena = (LevelOne) arena;
                    temp_arena.generatorFill(player, entity);
                } else {
                    player.sendMessage(ChatColor.RED + "Генераторы не работают на этом уровне!");
                }
                return;
            }
        }
    }
}
