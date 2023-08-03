package lps.backrooms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemRelatedListeners implements Listener {

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event){

        if (event.getPlayer().hasPermission("br.bypass")){
            return;
        }

        if (!event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent event){

        if (event.getPlayer().hasPermission("br.bypass")){
            return;
        }

        if (!event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){

        if (event.getDamager().hasPermission("br.bypass")){
            return;
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            if (!event.getDamager().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("monster")){
                event.setCancelled(true);
            }
        } else if (!(event.getEntity() instanceof Player)){
            event.setCancelled(true
            );
        }
    }

}
