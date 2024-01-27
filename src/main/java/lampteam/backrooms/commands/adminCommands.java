package lampteam.backrooms.commands;

import lampteam.backrooms.Levels.Arena;
import lampteam.backrooms.Backrooms;
import lampteam.backrooms.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class adminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("bradmin")){

            if (!sender.hasPermission("bradmin")){
                return false;
            }

            Player p = (Player) sender;

            if (args[0].equalsIgnoreCase("reload")){
                Backrooms.loadArenasFromConfig();
                p.sendMessage("Перезагружаю");
                return false;
            }

            if (args[0].equalsIgnoreCase("help")){
                p.sendMessage("/bradmin reload");
                p.sendMessage("/bradmin parties");
                p.sendMessage("/bradmin arenas");
                p.sendMessage("/bradmin debug arena <id>");
                p.sendMessage("/bradmin debug player <name>");
                p.sendMessage("/bradmin debug party  <leader>");
                return false;
            }

            if (args[0].equalsIgnoreCase("parties")){
                for (Party party : Backrooms.getPlugin().getParties()){
                    p.sendMessage(party.getLeader().displayName());
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("arenas")){
                for (Arena arena : Backrooms.getPlugin().getArenas()){
                    p.sendMessage(arena.getId());
                }
                return false;
            }

            if (args[0].equalsIgnoreCase("debug")){
                if (args[1].equalsIgnoreCase("arena")){
                    for (Arena arena : Backrooms.getPlugin().getArenas()){
                        if (arena.getId().equalsIgnoreCase(args[2])){
                            p.sendMessage("Игроки: " + arena.getPlayers().toString());
                            p.sendMessage("Активна: " + arena.isGameActive());
                            p.sendMessage("Время: " + arena.currentTime);
                        }
                    }
                    return false;
                }
                if (args[1].equalsIgnoreCase("player")){
                    p.sendMessage("Арена: " + Bukkit.getPlayer(args[2]).getMetadata("br_arena").get(0).asString());
                    p.sendMessage("Состояние: " + Bukkit.getPlayer(args[2]).getMetadata("br_player_state").get(0).asString());
                    p.sendMessage("Пати: " + Bukkit.getPlayer(args[2]).getMetadata("br_party").get(0).asString());
                    return false;
                }
                if (args[1].equalsIgnoreCase("party")){
                    for (Party party : Backrooms.getPlugin().getParties()){
                        if (party.getLeader().getName().equalsIgnoreCase(args[2])){
                            p.sendMessage("Лидер: " + party.getLeader());
                            p.sendMessage("Спавн вместе: " + party.getSpawnSettings());
                            p.sendMessage("Игроки: " + party.getPlayers());
                        }
                    }
                    return false;
                }

            }
        }

        return false;
    }
}
