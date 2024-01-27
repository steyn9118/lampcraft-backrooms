package lampteam.backrooms.listeners;

import lampteam.backrooms.Backrooms;
import lampteam.backrooms.Levels.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"deprecation"})
public class ItemRelatedListeners implements Listener {

    // Предотвращает выбрасывание предметов игроками, которые находятся в лобби или являются призраками/монстрами
    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event){

        if (event.getPlayer().hasPermission("br.bypass")){
            return;
        }

        if (event.getItemDrop().getItemStack().getType().equals(Material.COAL)){
            event.setCancelled(true);
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
        if (event.getCurrentItem() == null){
            return;
        }

        // Подключение к арене через меню
        if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getLore() != null && event.getCurrentItem().getItemMeta().hasDisplayName()){
            if (event.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GRAY + "Статус: " + ChatColor.GREEN + "Арена свободна")){
                String id = event.getCurrentItem().getItemMeta().getDisplayName();
                System.out.println(id);
                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (arena.getId().equalsIgnoreCase(id.toLowerCase())){

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "br join " + event.getWhoClicked().getName() + " " + id);
                        break;
                    }
                }
                event.setCancelled(true);
            }
        }

        if (event.getWhoClicked().hasPermission("br.bypass")){
            return;
        }

        if (!event.getWhoClicked().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("alive")){
            event.setCancelled(true);
        }
    }

    // Закрытие меню
    @EventHandler
    public void menuCloseEvent(InventoryCloseEvent event){
        if (event.getView().getTitle().equals("Уровень 0 - Выбор арены") && event.getPlayer().getMetadata("br_player_state").get(0).asString().equalsIgnoreCase("null")){
            // Телепорт игрока обратно (из ямы), если была закрыта меню выбора арены первого уровня (без задержки - крашится)
            BukkitRunnable teleportDelay = new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 258.0, 4.5, -180, 0));
                }
            };
            teleportDelay.runTaskLater(Backrooms.getPlugin(), 5);
        }
    }

}
