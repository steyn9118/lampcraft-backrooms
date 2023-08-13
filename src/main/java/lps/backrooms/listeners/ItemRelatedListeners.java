package lps.backrooms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemRelatedListeners implements Listener {

    // Предотвращает выбрасывание предметов игроками, которые находятся в лобби или являются призраками/монстрами
    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event){

        if (event.getPlayer().hasPermission("br.bypass")){
            return;
        }

        if (!event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    // Предотвращает подбирание предметов игроками, которые находятся в лобби или являются призраками/монстрами
    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent event){

        if (event.getPlayer().hasPermission("br.bypass")){
            return;
        }

        if (!event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    // Предотвращает перемещение предметов в инвентаре, если игрок не жив (в игре)
    @EventHandler
    public void itemInventoryDragEvent(InventoryClickEvent event){
        if (event.getWhoClicked().hasPermission("br.bypass")){
            return;
        }
        if (!event.getWhoClicked().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    // Предотвращает нанесение урона всем неживым игрокам
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)){
            return;
        }
        if ((event.getEntity() instanceof Player && event.getDamager().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster"))){
            return;
        } else {
            event.setCancelled(true);
        }
    }

}
