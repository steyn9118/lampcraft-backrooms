package lampteam.backrooms.listeners;

import lampteam.backrooms.Levels.Arena;
import lampteam.backrooms.Party;
import lampteam.backrooms.Backrooms;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

@SuppressWarnings("DataFlowIssue")
public class playerJoinListener implements Listener {

    private final Backrooms plugin = Backrooms.getPlugin();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player p = event.getPlayer();
        p.setMetadata("br_party", new FixedMetadataValue(plugin, "null"));
        p.setMetadata("br_arena", new FixedMetadataValue(plugin, "null"));
        p.setMetadata("br_player_state", new FixedMetadataValue(plugin, "null"));
        p.setMetadata("can_pickup_items", new FixedMetadataValue(plugin, true));
        TextComponent clikable_invite = new TextComponent("Если у вас не загрузился ресурспак - нажмите на это сообщение или напишите /pack");
        clikable_invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pack"));
        clikable_invite.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        p.spigot().sendMessage(clikable_invite);
    }

    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event){

        Player p = event.getPlayer();
        p.getInventory().clear();

        // Проверка на наличие пати и выход из неё при наличии
        if (!p.getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")){
            Party LeaveParty = null;
            for (Party party : plugin.getParties()){
                if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase(party.getLeader().getName())){
                    LeaveParty = party;
                }
            }
            LeaveParty.leave(p);
        }

        // Находится ли на арене и выход из неё при наличии
        if (!p.getMetadata("br_arena").get(0).asString().equalsIgnoreCase("null")){
            Arena LeaveArena = null;
            for (Arena arena : plugin.getArenas()){
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
        for (Arena arena : plugin.getArenas()){
            if (arena.getPlayers().contains(p)) {
                arena.death(p);
                event.setCancelled(true);
            }
        }
    }

}
