package lps.backrooms;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class Party {

    int partyMaxPlayers = 8;
    ArrayList<Player> invitedPlayers = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    boolean togetherSpawn = false;
    Player partyLeader;


    public boolean getSpawnSettings(){ return togetherSpawn; }

    public void setSpawnSettings(boolean togetherSpawn){ this.togetherSpawn = togetherSpawn; }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public Player getLeader(){
        return partyLeader;
    }

    // Инициализация пати
    public void init(Player partyLeader){

        this.players.add(partyLeader);
        this.partyLeader = partyLeader;
        partyLeader.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), partyLeader.getName()));

    }


    // Приглашение игрока
    public void invite(Player invited) {

        if (invitedPlayers.contains(invited) || players.contains(invited)){
            partyLeader.sendMessage(ChatColor.RED + "Этот игрок уже приглашен в вашу пати!");
            return;
        }

        invited.sendMessage(ChatColor.YELLOW + "Вас пригласил в пати " + partyLeader.getName());
        TextComponent clikable_invite = new TextComponent("Чтобы принять, напишите /party join " + partyLeader.getName() + " или нажмите на это сообщение");
        clikable_invite.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + partyLeader.getName()));
        clikable_invite.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        invited.spigot().sendMessage(clikable_invite);
        invitedPlayers.add(invited);

    }

    // Исключение игрока
    public void kick(Player kicker, Player kicked){

        if (kicker.equals(kicked)){
            kicker.sendMessage(ChatColor.RED + "Нельзя выгнать самого себя!");
            return;
        }

        if (!kicker.equals(partyLeader)){
            kicker.sendMessage(ChatColor.RED + "Только лидер пати может выгонять участников!");
            return;
        }

        if (!players.contains(kicked)){
            kicker.sendMessage(ChatColor.RED + "Игрок " + kicked.getName() + " не состоит в вашей пати!");
            return;
        }

        kicker.sendMessage(ChatColor.GREEN + "Игрок " + kicked.getName() + " был выгнан из пати");
        kicked.sendMessage(ChatColor.RED + "Вас выгнали из пати!");
        this.leave(kicked);

    }


    // Присоединение к пати
    public void join(Player joining_player) {

        if (!invitedPlayers.contains(joining_player) || players.size() == partyMaxPlayers){
            return;
        }

        players.add(joining_player);
        joining_player.sendMessage(ChatColor.GREEN + "Вы присоединились к пати " + partyLeader.getName());
        joining_player.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), partyLeader.getName()));
        invitedPlayers.remove(joining_player);

        for (Player player : players){
            player.sendMessage(joining_player.getName() + "" + ChatColor.GREEN + " Присоединился к пати!");
        }

    }

    // Выход из пати
    public void leave(Player p){

        // Если вышел обычный участник
        if (!partyLeader.equals(p)){
            players.remove(p);
            p.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
            for (Player player : players){
                player.sendMessage(p.getName() + "" + ChatColor.YELLOW + " покинул пати");
            }
            return;
        }

        // Если вышел лидер пати
        if (players.size() != 0){
            for (Player player : players){
                player.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
                player.sendMessage(ChatColor.RED + "Пати расформировано");
            }
        }
        players.clear();
        Backrooms.getPlugin().getParties().remove(this);
    }
}
