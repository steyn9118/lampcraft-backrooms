package lps.backrooms.commands;

import lps.backrooms.Levels.Arena;
import lps.backrooms.Backrooms;
import lps.backrooms.Levels.LevelOne;
import lps.backrooms.Levels.LevelZero;
import lps.backrooms.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class backroomsCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("br")){
            if (args.length == 0) {
                return false;
            }

            if (args.length == 1){
                // ВЫХОД С АРЕНЫ
                if (args[0].equalsIgnoreCase("leave")){

                    Player p = (Player) sender;

                    if (!p.getMetadata("br_arena").get(0).asString().equalsIgnoreCase("null")){
                        for (Arena arena : Backrooms.getPlugin().getArenas()){
                            if (p.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
                                arena.leave(p);
                                return true;
                            }
                        }
                    }
                }
            }

            // ПРИСОЕДИНЕНИЕ К АРЕНЕ
            if (args[0].equalsIgnoreCase("join")) {

                if (!sender.hasPermission("br.join")){
                    return false;
                }

                // ПРОВЕРКА ЛИДЕРА ПАТИ
                Party Joining_party = null;

                if (Bukkit.getPlayer(args[2]).getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")) {

                    Party party = new Party();
                    party.init(Bukkit.getPlayer(args[2]));
                    Backrooms.getPlugin().getParties().add(party);
                    Joining_party = party;

                } else {

                    // TODO НАДО ПЕРЕПИСАТЬ ПРОВЕРКУ НА ЛИДЕРА (сам хз, когда и зачем я это писал, но видимо что-то тут надо изменить)
                    for (Party party : Backrooms.getPlugin().getParties()) {
                        if (party.getPlayers().contains(Bukkit.getPlayer(args[2]))) {
                            if (party.getLeader().getName().equalsIgnoreCase(args[2])) {

                                Joining_party = party;

                            } else {

                                Bukkit.getPlayer(args[2]).sendMessage(ChatColor.RED + "Только лидер пати может присоединяться!");

                            }
                        }
                    }

                }

                Arena Jarena = null;

                // ПРОВЕРКА ИГРОКОВ ПАТИ, КОТОРЫЕ ЕЩЁ ИГРАЮТ
                for (Player player : Joining_party.getPlayers()){
                    if (!player.getMetadata("br_arena").get(0).asString().equalsIgnoreCase("null")){
                        Joining_party.getLeader().sendMessage(ChatColor.RED + "Игрок " + player.getName() + " ещё играет!");
                        Joining_party.getLeader().sendMessage(ChatColor.YELLOW + "Вы сможете присоединиться, когда все участники будут в лобби");
                        return false;
                    }
                }

                // ПОЛУЧЕНИЕ АРЕНЫ
                for (Arena arena : Backrooms.getPlugin().getArenas()) {
                    if (arena.getId().equalsIgnoreCase(args[1])) {
                        Jarena = arena;
                    }
                }

                if (Jarena == null){
                    System.out.println(ChatColor.RED + "Арена не найдена");
                    return false;
                }

                Jarena.join(Joining_party);
                return true;
            }

            // ПОБЕДА НА АРЕНЕ
            if (args[0].equalsIgnoreCase("win")){

                if (!sender.hasPermission("br.win")){
                    return false;
                }

                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (Objects.requireNonNull(Bukkit.getPlayer(args[1])).getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
                        arena.win(Bukkit.getPlayer(args[1]));
                        return true;
                    }
                }
            }

            // НАБЛЮДЕНИЕ ЗА ИГРОКАМИ
            if (args[0].equalsIgnoreCase("spectate")){

                if (!sender.hasPermission("br.spectate")){
                    return false;
                }

                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (Bukkit.getPlayer(args[1]).getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
                        arena.spectate(Bukkit.getPlayer(args[1]));
                    }
                }
            }

            // ПРЕВРАЩЕНИЕ В МОНСТРА
            if (args[0].equalsIgnoreCase("monster")){

                if (!sender.hasPermission("br.monster")){
                    return false;
                }

                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (Bukkit.getPlayer(args[1]).getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){

                        if (arena instanceof LevelZero){
                            LevelZero temp_arena = (LevelZero) arena;
                            temp_arena.playerBecameMonster(Bukkit.getPlayer(args[1]));
                            return true;
                        }

                        if (arena instanceof LevelOne){
                            Bukkit.getPlayer(args[1]).sendMessage(ChatColor.RED + "На этом уровне нельзя превратиться в монстра!");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
