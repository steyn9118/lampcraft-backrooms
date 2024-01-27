package lampteam.backrooms;

import lampteam.backrooms.Levels.Arena;
import lampteam.backrooms.Levels.LevelOne;
import lampteam.backrooms.Levels.LevelZero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")

public class ArenaSelector {

    // Инициализация
    public void init(int level, Player player){

        Inventory menu;

        if (level == 1){
            menu = Bukkit.createInventory(null, InventoryType.CHEST, "Уровень 1 - Выбор арены");
        } else if (level == 0){
            menu = Bukkit.createInventory(null, InventoryType.CHEST, "Уровень 0 - Выбор арены");
        } else {
            menu = Bukkit.createInventory(null, InventoryType.CHEST, "Уровень ? - Что-то пошло не так...");
        }

        // Создание фона из панелей
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++){
            menu.setItem(i, filler);
        }
        for (int i = 18; i < 27; i++){
            menu.setItem(i, filler);
        }

        // Создание иконок арен
        int place = switch (level) {
            case (0) -> 11;
            case (1) -> 12;
            default -> 9;
        };
        for (Arena arena : Backrooms.getPlugin().getArenas()){

            if (level == 1 && !(arena instanceof LevelOne)){
                continue;
            }
            if (level == 0 && !(arena instanceof LevelZero)){
                continue;
            }

            ItemStack arenaIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta arenaDescription = arenaIcon.getItemMeta();
            arenaDescription.setDisplayName(ChatColor.RESET + arena.getId());
            List<String> lore = new ArrayList<>();

            if (arena.isGameActive()){
                lore.add(ChatColor.GRAY + "Статус: " + ChatColor.RED + "Идёт игра");
                lore.add(ChatColor.GRAY + "Живых ироков: " + ChatColor.YELLOW + arena.getPlayers().size());
            } else {
                arenaIcon.setType(Material.LIME_STAINED_GLASS_PANE);
                lore.add(ChatColor.GRAY + "Статус: " + ChatColor.GREEN + "Арена свободна");
                lore.add(ChatColor.GRAY + "Максимум игроков: " + arena.getMaxPlayers());
            }
            arenaDescription.setLore(lore);
            arenaIcon.setItemMeta(arenaDescription);

            menu.setItem(place, arenaIcon);
            place++;

        }

        player.openInventory(menu);

        // Возможно стоит сделать таймер для обновления информации об аренах
    }

}
