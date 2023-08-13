package lps.backrooms.listeners;

import lps.backrooms.Levels.Arena;
import lps.backrooms.Backrooms;
import lps.backrooms.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class playerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player p = event.getPlayer();
        p.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
        p.setMetadata("br_arena", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
        p.setMetadata("br_player_state", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
    }

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event){

        Player p = event.getPlayer();
        p.getInventory().clear();


        // Проверка на наличие пати и выход из неё при наличии
        if (!p.getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")){
            Party LeaveParty = null;
            for (Party party : Backrooms.getPlugin().getParties()){
                if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase(party.getLeader().getName())){
                    LeaveParty = party;
                }
            }
            LeaveParty.leave(p);
        }

        // Находится ли на арене и выход из неё при наличии
        if (!p.getMetadata("br_arena").get(0).asString().equalsIgnoreCase("null")){
            Arena LeaveArena = null;
            for (Arena arena : Backrooms.getPlugin().getArenas()){
                if (!arena.isGameActive()){
                    continue;
                }
                if (p.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
                    LeaveArena = arena;
                    break;
                }
            }
            LeaveArena.leave(p);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player p = event.getEntity();
        for (Arena arena : Backrooms.getPlugin().getArenas()){
            if (arena.getPlayers().contains(p)){
                arena.death(p);
                return;
            }
        }
    }


}
