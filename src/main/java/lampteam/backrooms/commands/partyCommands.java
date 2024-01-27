package lampteam.backrooms.commands;

import lampteam.backrooms.Party;
import lampteam.backrooms.Backrooms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class partyCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("party")){
            if (args.length == 0 || sender instanceof ConsoleCommandSender || sender instanceof CommandBlock){
                return false;
            }

            Player p = (Player) sender;

            // HELP PAGE
            if (args[0].equalsIgnoreCase("help")){
                p.sendMessage(ChatColor.YELLOW + "/party invite <ник> - Пригласить игрока в пати");
                p.sendMessage(ChatColor.YELLOW + "/party join <ник> - Присоединиться к пати игрока, если он вас пригласил");
                p.sendMessage(ChatColor.YELLOW + "/party kick <ник> - Выгнать игрока из пати");
                p.sendMessage(ChatColor.YELLOW + "/party leave - Покинуть пати");
                p.sendMessage(ChatColor.YELLOW + "/party changespawn - переключить спавн группой/случайный");
                p.sendMessage(ChatColor.YELLOW + "/party members - посмтореть список участников пати");
                return false;
            }

            if (args.length == 2){

                // JOINING PARTY
                if (args[0].equalsIgnoreCase("join")){

                    for (Party party : Backrooms.getPlugin().getParties()) {
                        if (party.getLeader().getName().equalsIgnoreCase(args[1])) {
                            party.join(p);
                            return false;
                        }
                    }
                }

                // INVITING PLAYERS
                if (args[0].equalsIgnoreCase("invite")){

                    if (Bukkit.getPlayer(args[1]) == null){
                        p.sendMessage(ChatColor.RED + "Игрок не найден");
                        return false;
                    }

                    if (Objects.requireNonNull(Bukkit.getPlayer(args[1])).equals(p)){
                        p.sendMessage(ChatColor.RED + "Нельзя пригласить самого себя");
                        return false;
                    }

                    if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")){
                        Party party = new Party();
                        party.init(p);
                        Backrooms.getPlugin().getParties().add(party);
                    }

                    for (Party party : Backrooms.getPlugin().getParties()){
                        if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase(party.getLeader().getName())){
                            party.invite(Objects.requireNonNull(Bukkit.getPlayer(args[1])));
                            p.sendMessage("Вы пригласили игрока " + Bukkit.getPlayer(args[1]).getName());
                            return false;
                        }
                    }
                }
            }

            // CHECKING FOR PARTY
            if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase("null")){
                p.sendMessage(ChatColor.RED + "У вас нет пати! " + ChatColor.YELLOW + "Пригласите друга, написав" + ChatColor.GREEN + " /party invite <ник>");
                return false;
            }

            Party party = null;

            for (Party nparty : Backrooms.getPlugin().getParties()){
                if (p.getMetadata("br_party").get(0).asString().equalsIgnoreCase(nparty.getLeader().getName())){
                    party = nparty;
                }
            }

            if (party == null){
                System.out.println("Пати не найдено");
                System.out.println(p.getMetadata("br_party").get(0).asString());
                return false;
            }

            if (args.length == 1){

                // LEAVE FROM PARTY
                if (args[0].equalsIgnoreCase("leave")){
                    party.leave(p);
                    return false;
                }

                // SHOW ALL PARTY MEMBERS
                if (args[0].equalsIgnoreCase("members")){
                    p.sendMessage("Участники пати:");
                    p.sendMessage(ChatColor.GOLD + party.getLeader().getName() + " - лидер");
                    for (Player player : party.getPlayers()){
                        if (party.getLeader().equals(player)){
                            continue;
                        }
                        p.sendMessage(player.getName());
                    }
                    return false;
                }

                // CHANGING SPAWN
                if (args[0].equalsIgnoreCase("changespawn")){
                    if (!sender.hasPermission("br.changespawn")){
                        p.sendMessage(ChatColor.RED + "Купи донат ;)");
                        return false;
                    }
                    if (!party.getLeader().equals(p)){
                        p.sendMessage(ChatColor.RED + "Только лидер может менять настройки спавна!");
                        return false;
                    }
                    if (party.getSpawnSettings()){
                        party.getLeader().sendMessage(ChatColor.YELLOW + "Включен спавн на случайных позициях");
                        party.setSpawnSettings(false);
                        return false;
                    }
                    party.setSpawnSettings(true);
                    party.getLeader().sendMessage(ChatColor.YELLOW + "Включен спавн на одной позиции");
                    return false;
                }
            }

            if (args.length == 2){

                // KICKING PLAYER
                if (args[0].equalsIgnoreCase("kick")){

                    party.kick(p, Bukkit.getPlayer(args[1]));
                    return false;

                }
            }

            p.sendMessage(ChatColor.RED + "Неизвестный аргумент! Напиши /party help");
        }
        return false;
    }
}
