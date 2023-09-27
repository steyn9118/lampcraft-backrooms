package lampteam.backrooms.commands;

import lampteam.backrooms.ArenaSelector;
import lampteam.backrooms.Levels.Arena;
import lampteam.backrooms.Levels.LevelOne;
import lampteam.backrooms.Levels.LevelZero;
import lampteam.backrooms.Party;
import lampteam.backrooms.Backrooms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class backroomsCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("br")){
            if (args.length == 0) {
                sender.sendMessage("br leave");
                sender.sendMessage("br join <player> <arena id>");
                sender.sendMessage("br win <player>");
                sender.sendMessage("br spectate <player>");
                sender.sendMessage("br monster <player>");
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

                return false;

            }

            // Игрок, вызвавший команду
            Player argplayer = Bukkit.getPlayer(args[1]);
            if (argplayer == null){
                sender.sendMessage("Необхоимо указать игрока");
                return false;
            }

            // Открытие меню с аренами уровня
            if (args[0].equalsIgnoreCase("select")){

                ArenaSelector menu = new ArenaSelector();
                menu.init(Integer.parseInt(args[2]), argplayer);

            }

            // ПРИСОЕДИНЕНИЕ К АРЕНЕ
            if (args[0].equalsIgnoreCase("join")) {

                if (!sender.hasPermission("br.join")){
                    return false;
                }

                // ПРОВЕРКА ЛИДЕРА ПАТИ
                Party Joining_party = null;

                // Если игрок без пати
                if (argplayer.getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")) {
                    Party party = new Party();
                    party.init(argplayer);
                    Backrooms.getPlugin().getParties().add(party);
                    Joining_party = party;

                } else {
                    for (Party party : Backrooms.getPlugin().getParties()) {
                        if (!party.getPlayers().contains(argplayer)) {
                            continue;
                        }
                        if (party.getLeader().equals(argplayer)) {
                            Joining_party = party;
                        } else {
                            argplayer.sendMessage(ChatColor.RED + "Только лидер пати может присоединяться!");
                            return false;
                        }
                    }
                }

                if (Joining_party == null){
                    sender.sendMessage("Укажите реальную пати");
                    return false;
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
                    if (arena.getId().equalsIgnoreCase(args[2])) {
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
                    if (argplayer.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
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
                    if (argplayer.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){
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
                    if (argplayer.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){

                        if (arena instanceof LevelZero){
                            LevelZero temp_arena = (LevelZero) arena;
                            temp_arena.playerBecameMonster(argplayer);
                            return true;
                        }

                        if (arena instanceof LevelOne){
                            argplayer.sendMessage(ChatColor.RED + "На этом уровне нельзя превратиться в монстра!");
                            return false;
                        }
                    }
                }
            }

            // Использование таблетки
            if (args[0].equalsIgnoreCase("methuse")){

                if (!sender.hasPermission("br.methuse")){
                    return false;
                }

                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (argplayer.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){

                        if (arena instanceof LevelOne){
                            LevelOne temp_arena = (LevelOne) arena;
                            temp_arena.methUse(argplayer);
                            return true;
                        }

                        if (arena instanceof LevelZero){
                            argplayer.sendMessage(ChatColor.RED + "Этот предмет здесь не работает!");
                            return false;
                        }
                    }
                }
            }

            // Использование гаечного ключа
            if (args[0].equalsIgnoreCase("wrenchuse")){

                if (!sender.hasPermission("br.wrenchuse")){
                    return false;
                }

                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    if (argplayer.getMetadata("br_arena").get(0).asString().equalsIgnoreCase(arena.getId())){

                        if (arena instanceof LevelOne){
                            LevelOne temp_arena = (LevelOne) arena;
                            temp_arena.wrenchUse(argplayer);
                            return true;
                        }

                        if (arena instanceof LevelZero){
                            argplayer.sendMessage(ChatColor.RED + "Этот предмет здесь не работает!");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
