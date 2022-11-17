package me.darkwinged.RiotGuilds.libaries;

import me.darkwinged.RiotGuilds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class Guild {

    private Main plugin = Main.getInstance;
    private String owner;
    private UUID ownerID;
    private List<String> members;
    private List<UUID> membersID;
    private String name;
    private boolean open;
    private boolean friendlyFire;
    private double worth;
    private int kills;
    private Material icon;

    public Guild() {}

    public Guild(String owner, UUID ownerID, String name, List<String> members, List<UUID> membersID, boolean open, boolean friendlyFire, double worth, int kills, Material icon) {
        this.owner = owner;
        this.ownerID = ownerID;
        this.name = name;
        this.members = members;
        this.membersID = membersID;
        this.open = open;
        this.friendlyFire = friendlyFire;
        this.worth = worth;
        this.kills = kills;
        this.icon = icon;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(UUID ownerID) {
        this.ownerID = ownerID;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<UUID> getMembersID() {
        return membersID;
    }

    public void setMembersID(List<UUID> membersID) {
        this.membersID = membersID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public double getWorth() {
        return worth;
    }

    public void refreshWorth() {
        double amount = 0;
        for (UUID uuid : membersID) {
            amount += plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid));
        }
        setWorth(amount);
    }

    public void setWorth(double worth) {
        this.worth = worth;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

}
