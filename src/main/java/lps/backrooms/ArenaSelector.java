package lps.backrooms;

import lps.backrooms.Levels.Arena;
import lps.backrooms.Levels.LevelOne;
import lps.backrooms.Levels.LevelZero;
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

        Inventory menu = Bukkit.createInventory(null, InventoryType.CHEST, "Уровень " + level + ". Выбор арены");

        // Создание фона из панелей
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++){
            menu.setItem(i, filler);
        }
        for (int i = 18; i < 27; i++){
            menu.setItem(i, filler);
        }

        // Создание иконок арен
        int place = 9;
        switch (level){
            case (0):
                place = 10;
            case (1):
                place = 11;
        }
        for (Arena arena : Backrooms.getPlugin().getArenas()){

            if (level == 1 && !(arena instanceof LevelOne)){
                continue;
            }
            if (level == 0 && !(arena instanceof LevelZero)){
                continue;
            }

            ItemStack arenaIcon = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta arenaDescription = arenaIcon.getItemMeta();
            arenaDescription.setDisplayName(arena.getId());
            List<String> lore = new ArrayList<>();

            if (arena.isGameActive()){
                arenaIcon.setType(Material.LIME_STAINED_GLASS_PANE);
                lore.add(ChatColor.GRAY + "Статус: " + ChatColor.RED + "Идёт игра");
                lore.add(ChatColor.GRAY + "Живых ироков: " + ChatColor.YELLOW + arena.getPlayers().size());
                arenaDescription.setLore(lore);
                arenaIcon.setItemMeta(arenaDescription);
            } else {
                lore.add(ChatColor.GRAY + "Статус: " + ChatColor.GREEN + "Арена свободна");
                lore.add(ChatColor.GRAY + "Максимум игроков: " + arena.getMaxPlayers());
                arenaDescription.setLore(lore);
                arenaIcon.setItemMeta(arenaDescription);
            }

            menu.setItem(place, arenaIcon);
            place++;

        }

        player.openInventory(menu);

        // Возможно стоит сделать таймер для обновления информации об аренах
    }

}
