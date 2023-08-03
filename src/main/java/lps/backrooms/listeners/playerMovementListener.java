package lps.backrooms.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class playerMovementListener implements Listener {

    @EventHandler
    public void onPlayerMovementEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSprinting()){
            player.setWalkSpeed(0.4f);
        } else {
            player.setWalkSpeed(0.2f);
        }
    }

    @EventHandler
    public void onPlayerJumpEvent(PlayerJumpEvent event){

        if (event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }

    }
}
