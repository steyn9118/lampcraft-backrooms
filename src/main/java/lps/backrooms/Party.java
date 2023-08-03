package lps.backrooms;

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

    public void init(Player partyLeader){

        this.players.add(partyLeader);
        this.partyLeader = partyLeader;
        partyLeader.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), partyLeader.getName()));

    }


    // INVITING PLAYER
    public void invite(Player invited) {

        if (invitedPlayers.contains(invited) || players.contains(invited)){
            partyLeader.sendMessage(ChatColor.RED + "Этот игрок уже приглашен в вашу пати!");
            return;
        }
        invited.sendMessage(ChatColor.YELLOW + "Вас пригласил в пати " + partyLeader.getName());
        invited.sendMessage(ChatColor.YELLOW + "Чтобы принять, напишите /party join " + partyLeader.getName());
        invitedPlayers.add(invited);

    }

    // KICKING PLAYER FROM PARTY
    public void kick(Player kicker, Player kicked){

        if (kicker.equals(partyLeader) && players.contains(kicked)){
            kicked.sendMessage(ChatColor.RED + "Вас выгнали из пати!");
            this.leave(kicked);
        }

    }


    // JOINING PARTY
    public void join(Player joiner) {

        if (!invitedPlayers.contains(joiner) || players.size() == partyMaxPlayers){
            return;
        }

        players.add(joiner);
        joiner.sendMessage(ChatColor.GREEN + "Вы присоединились к пати " + partyLeader.getName());
        joiner.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), partyLeader.getName()));
        invitedPlayers.remove(joiner);

        for (Player player : players){
            player.sendMessage(joiner.getName() + "" + ChatColor.GREEN + " Присоединился к пати!");
        }

    }

    // LEAVING PARTY
    public void leave(Player p){

        // IF MEMBER LEAVED
        if (!partyLeader.equals(p)){
            players.remove(p);
            p.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
            for (Player player : players){
                player.sendMessage(p.getName() + "" + ChatColor.YELLOW + " покинул пати");
            }
            return;
        }

        // IF LEADER LEAVED
        if (players.size() == 0){
            return;
        }
        for (Player player : players){
            player.setMetadata("br_party", new FixedMetadataValue(Backrooms.getPlugin(), "null"));
            player.sendMessage(ChatColor.RED + "Пати расформировано");
        }
        players.clear();
        Backrooms.getPlugin().getParties().remove(this);
    }
}
