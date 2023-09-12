package lps.backrooms.listeners;

import lps.backrooms.Backrooms;
import lps.backrooms.Levels.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings({"deprecation", "DataFlowIssue"})
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

        if (!event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive") || !event.getPlayer().getMetadata("can_pickup_items").get(0).asBoolean()){
            event.setCancelled(true);
        }
    }

    // Предотвращает перемещение предметов в инвентаре, если игрок не жив (в игре)
    @EventHandler
    public void itemInventoryClickEvent(InventoryClickEvent event){
        if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Статус: " + ChatColor.GREEN + "Арена свободна")){
            String id = event.getCurrentItem().getI18NDisplayName();
            for (Arena arena : Backrooms.getPlugin().getArenas()){
                if (arena.getId().equalsIgnoreCase(id)){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "br join " + event.getWhoClicked().getName() + " " + id);
                    event.setCancelled(true);
                    break;
                }
            }
        }
        if (event.getWhoClicked().hasPermission("br.bypass")){
            return;
        }
        if (!event.getWhoClicked().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void menuCloseEvent(InventoryCloseEvent event){
        if (!event.getView().getTitle().equalsIgnoreCase("Уровень 0. Выбор арены")){
            return;
        }
        if (event.getPlayer().getMetadata("br_state").get(0).asString().equalsIgnoreCase("null")){
            event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 258.0, 4.5, 0, 180));
        }
    }

}
