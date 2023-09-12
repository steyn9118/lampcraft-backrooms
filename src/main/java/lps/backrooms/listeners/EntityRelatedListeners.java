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
        if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)){
            // Проигрывание звуков смерти
            if (event.getDamager().getType().equals(EntityType.DROWNED)){
                Player p = (Player) event.getEntity();
                //
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.ender_dragon.growl master @a[distance=..20] ~ ~ ~ 50 1");
                return;
            }
            if (event.getDamager().getType().equals(EntityType.ZOMBIE)){
                Player p = (Player) event.getEntity();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " + p.getName() + " run playsound minecraft:entity.ender_dragon.growl master @a[distance=..20] ~ ~ ~ 50 1");
                return;
            }
            return;
        }
        if ((event.getEntity() instanceof Player && event.getDamager().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster"))){
            return;
        } else {
            event.setCancelled(true);
        }
    }

    // Для генерератора
    @EventHandler
    public void onPlayerEntityInteractEvent(PlayerInteractEntityEvent event){

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (!entity.getType().equals(EntityType.PIG)){
            return;
        }

        if (!entity.getName().equalsIgnoreCase("Генератор")){
            return;
        }

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
