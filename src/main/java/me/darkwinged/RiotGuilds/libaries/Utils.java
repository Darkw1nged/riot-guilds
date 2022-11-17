package me.darkwinged.RiotGuilds.libaries;

import me.darkwinged.RiotGuilds.Main;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public class Utils {

    private static final Main plugin = Main.getInstance;

    // ---- [ Managing chat color within the plugin ] ----
    public static String chatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // ---- [ Managing chat color within the plugin | Supports Amount ] ----
    public static String chatColor(String s, Double amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String converted = nf.format(amount);
        return ChatColor.translateAlternateColorCodes('&', s)
                .replaceAll("%amount%", converted);
    }

    // ---- [ Converting a lore to include colors ] ----
    public static List<String> getConvertedLore(FileConfiguration config, String path) {
        if (config == null) return null;
        List<String> oldList = config.getStringList(path + ".lore");
        List<String> newList = new ArrayList<>();
        for (String a : oldList)
            newList.add(ChatColor.translateAlternateColorCodes('&', a));
        return newList;
    }

    // ---- [ Available space ] ----
    public static boolean hasSpace(Player player, ItemStack targetItem) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == targetItem.getType()) {
                if (item.getAmount() != item.getMaxStackSize()) {
                    item.setAmount(item.getAmount() + 1);
                    return true;
                }
            }
        }
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(targetItem);
            return true;
        }
        return false;
    }

    // ---- [ Removes all color codes from string ] ----
    public static String replaceAllColorCodes(String string) {
        return string.replaceAll("&a", "")
                .replaceAll("&b", "")
                .replaceAll("&c", "")
                .replaceAll("&d", "")
                .replaceAll("&e", "")
                .replaceAll("&f", "")
                .replaceAll("&1", "")
                .replaceAll("&2", "")
                .replaceAll("&3", "")
                .replaceAll("&4", "")
                .replaceAll("&5", "")
                .replaceAll("&6", "")
                .replaceAll("&7", "")
                .replaceAll("&8", "")
                .replaceAll("&9", "")
                .replaceAll("&k", "")
                .replaceAll("&l", "")
                .replaceAll("&m", "")
                .replaceAll("&n", "")
                .replaceAll("&o", "")
                .replaceAll("&r", "");

    }

    // ---- [ Check if string is just letters ] ----
    public static boolean isJustLetters(String string) {
        char[] chars = string.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    // ---- [ Cached Items ] ----
    public static List<Guild> guilds = new ArrayList<>();
    public static Map<UUID, List<Guild>> filteredGuilds = new HashMap<>();
    public static Map<UUID, Integer> UIPage = new HashMap<>();

    // ---- [ Get player claims ] ----
    public static List<Claim> getPlayerClaim(UUID uuid) {
        return GriefPrevention.instance.dataStore.getPlayerData(uuid).getClaims();
    }

    // ---- [ Add player to claim ] ----
    public static void addPlayerToClaims(List<Claim> claims, Player player) {
        for (Claim claim : claims) {
            GriefPrevention.instance.dataStore.getClaim(claim.getID()).setPermission(player.getUniqueId().toString(), ClaimPermission.Access);
            GriefPrevention.instance.dataStore.getClaim(claim.getID()).setPermission(player.getUniqueId().toString(), ClaimPermission.Build);
            GriefPrevention.instance.dataStore.getClaim(claim.getID()).setPermission(player.getUniqueId().toString(), ClaimPermission.Inventory);
        }
    }

    // ---- [ Remove player from claim ] ----
    public static void removePlayerFromClaims(List<Claim> claims, Player player) {
        for (Claim claim : claims) {
            GriefPrevention.instance.dataStore.getClaim(claim.getID()).dropPermission(player.getUniqueId().toString());
        }
    }

    // ---- [ Get player guild ] ----
    public static Guild getGuild(Player player) {
        for (Guild guild : guilds) {
            if (guild.getOwnerID().toString().equals(player.getUniqueId().toString())) {
                return guild;
            } else if (guild.getMembersID().contains(player.getUniqueId())) {
                return guild;
            }
        }
        return new Guild();
    }

    // ---- [ get if player is in guild ] ----
    public static boolean inGuild(Player player) {
        for (Guild guild : guilds) {
            if (guild.getOwnerID().toString().equals(player.getUniqueId().toString())) {
                return true;
            } else if (guild.getMembersID().contains(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    // ---- [ Get if player is owner of a guild ] ----
    public static boolean isOwnerGuild(Player player) {
        for (Guild guild : guilds) {
            if (guild.getOwnerID().toString().equals(player.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    // ---- [ Load guilds ] ----
    public static void loadGuilds() {
        File folder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Guilds");
        if (!folder.exists() || folder.listFiles().length == 0) return;

        for (File file : new File(String.valueOf(folder)).listFiles()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            List<UUID> membersID = new ArrayList<>();
            for (String uuid : configuration.getStringList("MembersID")) {
                membersID.add(UUID.fromString(uuid));
            }

            List<String> members = new ArrayList<>(configuration.getStringList("Members"));

            guilds.add(new Guild(
                    configuration.getString("Owner"),
                    UUID.fromString(configuration.getString("OwnerID")),
                    configuration.getString("Name"),
                    members,
                    membersID,
                    configuration.getBoolean("isOpen"),
                    configuration.getBoolean("friendlyFire"),
                    configuration.getDouble("Worth"),
                    configuration.getInt("Kills"),
                    Material.getMaterial(configuration.getString("Icon"))
            ));
        }
    }

    // ---- [ Save guilds ] ----
    public static void saveGuilds() {
        for (Guild guild : guilds) {
            CustomConfig fileGuild = new CustomConfig(plugin, guild.getName(), "Guilds");
            fileGuild.getConfig().set("Name", guild.getName());
            fileGuild.getConfig().set("Owner", guild.getOwner());
            fileGuild.getConfig().set("OwnerID", guild.getOwnerID().toString());
            fileGuild.getConfig().set("isOpen", guild.isOpen());
            fileGuild.getConfig().set("friendlyFire", guild.isOpen());
            fileGuild.getConfig().set("Icon", guild.getIcon().name().toUpperCase());

            List<String> list = new ArrayList<>();
            for (String name : guild.getMembers()) {
                list.add(name);
            }
            fileGuild.getConfig().set("Members", list);

            list.clear();
            for (UUID uuid : guild.getMembersID()) {
                list.add(uuid.toString());
            }
            fileGuild.getConfig().set("MembersID", list);

            fileGuild.saveConfig();
        }
    }

    // ---- [ Open Sign Editor ] ----
    public static void openSign(Player target, List<String> lines) {
        int result = 0;
        List<Player> isComplete = new ArrayList<>();
        SignFactory.Menu menu = plugin.signFactory.newMenu(lines)
                .reopenIfFail(true)
                .response((player, strings) -> {
                    if (strings[result].equals("") || strings[result].equals("0")) return true;

                    if (!filteredGuilds.isEmpty() && filteredGuilds.containsKey(player.getUniqueId())) {
                        filteredGuilds.remove(player.getUniqueId());
                    }
                    List<Guild> temp = new ArrayList<>();
                    for (Guild guild : guilds) {
                        if (guild.getName().toLowerCase().contains(strings[result].toLowerCase())) {
                            temp.add(guild);
                        }
                    }
                    filteredGuilds.put(player.getUniqueId(), temp);
                    isComplete.add(player);
                    return true;
                });
        menu.open(target);

        new RepeatingTask(plugin, 2, 2) {
            public void run() {
                if (isComplete.contains(target)) {
                    target.performCommand("guilds");
                    isComplete.remove(target);
                    cancel();
                }
            }
        };
    }

    // ---- [ Method for working out position of poll in array ] ----
    public static int slotToArrayIndex(int slot, Player player) {
        if (!UIPage.isEmpty() && UIPage.get(player.getUniqueId()) != 1) {
            switch (slot) {
                case 10:
                    return 28 + UIPage.get(player.getUniqueId());
                case 11:
                    return 28 + UIPage.get(player.getUniqueId()) + 1;
                case 12:
                    return 28 + UIPage.get(player.getUniqueId()) + 2;
                case 13:
                    return 28 + UIPage.get(player.getUniqueId()) + 3;
                case 14:
                    return 28 + UIPage.get(player.getUniqueId()) + 4;
                case 15:
                    return 28 + UIPage.get(player.getUniqueId()) + 5;
                case 16:
                    return 28 + UIPage.get(player.getUniqueId()) + 6;
                case 19:
                    return 28 + UIPage.get(player.getUniqueId()) + 8;
                case 20:
                    return 28 + UIPage.get(player.getUniqueId()) + 9;
                case 21:
                    return 28 + UIPage.get(player.getUniqueId()) + 10;
                case 22:
                    return 28 + UIPage.get(player.getUniqueId()) + 11;
                case 23:
                    return 28 + UIPage.get(player.getUniqueId()) + 12;
                case 24:
                    return 28 + UIPage.get(player.getUniqueId()) + 13;
                case 25:
                    return 28 + UIPage.get(player.getUniqueId()) + 14;
                case 28:
                    return 28 + UIPage.get(player.getUniqueId()) + 15;
                case 29:
                    return 28 + UIPage.get(player.getUniqueId()) + 16;
                case 30:
                    return 28 + UIPage.get(player.getUniqueId()) + 17;
                case 31:
                    return 28 + UIPage.get(player.getUniqueId()) + 18;
                case 32:
                    return 28 + UIPage.get(player.getUniqueId()) + 19;
                case 33:
                    return 28 + UIPage.get(player.getUniqueId()) + 20;
                case 34:
                    return 28 + UIPage.get(player.getUniqueId()) + 21;
                case 37:
                    return 28 + UIPage.get(player.getUniqueId()) + 22;
                case 38:
                    return 28 + UIPage.get(player.getUniqueId()) + 23;
                case 39:
                    return 28 + UIPage.get(player.getUniqueId()) + 24;
                case 40:
                    return 28 + UIPage.get(player.getUniqueId()) + 25;
                case 41:
                    return 28 + UIPage.get(player.getUniqueId()) + 26;
                case 42:
                    return 28 + UIPage.get(player.getUniqueId()) + 27;
                case 43:
                    return 28 + UIPage.get(player.getUniqueId()) + 28;
            }
        } else {
            switch (slot) {
                case 10:
                    return 0;
                case 11:
                    return 1;
                case 12:
                    return 2;
                case 13:
                    return 3;
                case 14:
                    return 4;
                case 15:
                    return 5;
                case 16:
                    return 6;
                case 19:
                    return 8;
                case 20:
                    return 9;
                case 21:
                    return 10;
                case 22:
                    return 11;
                case 23:
                    return 12;
                case 24:
                    return 13;
                case 25:
                    return 14;
                case 28:
                    return 15;
                case 29:
                    return 16;
                case 30:
                    return 17;
                case 31:
                    return 18;
                case 32:
                    return 19;
                case 33:
                    return 20;
                case 34:
                    return 21;
                case 37:
                    return 22;
                case 38:
                    return 23;
                case 39:
                    return 24;
                case 40:
                    return 25;
                case 41:
                    return 26;
                case 42:
                    return 27;
                case 43:
                    return 28;
            }
        }
        return 0;
    }

    // ---- [ Class for managing repeating tasks ] ----
    public abstract static class RepeatingTask implements Runnable {

        private final int task;

        public RepeatingTask(Plugin plugin, int initial, int delay) {
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, initial, delay);
        }

        public void cancel() {
            Bukkit.getScheduler().cancelTask(task);
        }

    }

}
