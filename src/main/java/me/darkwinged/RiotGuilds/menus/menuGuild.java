package me.darkwinged.RiotGuilds.menus;

import me.darkwinged.RiotGuilds.Main;
import me.darkwinged.RiotGuilds.libaries.Guild;
import me.darkwinged.RiotGuilds.libaries.Menu;
import me.darkwinged.RiotGuilds.libaries.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class menuGuild implements Menu {

    private final Main plugin = Main.getInstance;
    private final Player player;
    private final Inventory inventory;

    public menuGuild(Player player) {
        this.player = player;

        if (Utils.UIPage.isEmpty() || !Utils.UIPage.containsKey(player.getUniqueId())) {
            Utils.UIPage.put(player.getUniqueId(), 1);
        }

        this.inventory = Bukkit.createInventory(this, plugin.getConfig().getInt("guild-inventory.size"),
                Utils.chatColor(plugin.getConfig().getString("guild-inventory.title") + " - " + Utils.UIPage.get(player.getUniqueId())));

        int pos = 0;
        for (String key : plugin.getConfig().getConfigurationSection("guild-inventory.contents").getKeys(false)) {
            if (!plugin.getConfig().contains("guild-inventory.contents." + key + ".item") && plugin.getConfig().getString("guild-inventory.contents." + key + ".type").equalsIgnoreCase("guild")) {
                if (!Utils.filteredGuilds.isEmpty() && Utils.filteredGuilds.containsKey(player.getUniqueId())) {
                    for (Guild guild : Utils.filteredGuilds.get(player.getUniqueId())) {
                        guild.refreshWorth();
                        if (Utils.filteredGuilds.get(player.getUniqueId()).size() == pos) {
                            ItemStack item = new ItemStack(Material.AIR);
                            if (plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") == -1) {
                                for (int i = 0; i < plugin.getConfig().getInt("guild-inventory.size"); i++) {
                                    inventory.setItem(i, item);
                                }
                            } else {
                                inventory.setItem(plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") - 1, item);
                            }
                        } else {
                            ItemStack item = new ItemStack(guild.getIcon());
                            if (plugin.getConfig().contains("guild-inventory.guild-gui-item.amount")) {
                                item.setAmount(plugin.getConfig().getInt("guild-inventory.guild-gui-item.amount"));
                            }

                            ItemMeta meta = item.getItemMeta();

                            meta.setDisplayName(Utils.chatColor(plugin.getConfig().getString("guild-inventory.guild-gui-item.name")
                                    .replaceAll("%guild_name%", guild.getName())));


                            if (item.getType().equals(Material.PLAYER_HEAD)) {
                                SkullMeta skullMeta = (SkullMeta) meta;
                                skullMeta.setOwningPlayer(Bukkit.getPlayer(guild.getOwnerID()));
                            }

                            if (plugin.getConfig().contains("guild-inventory.guild-gui-item.lore")) {
                                List<String> lore = new ArrayList<>();
                                for (String line : plugin.getConfig().getStringList("guild-inventory.guild-gui-item.lore")) {

                                    lore.add(Utils.chatColor(line, guild.getWorth())
                                            .replaceAll("%owner%", guild.getOwner())
                                            .replaceAll("%members%", "" + guild.getMembers().size())
                                            .replaceAll("%kills%", "" + guild.getKills())
                                            .replaceAll("%open%", Utils.chatColor(guild.isOpen() ? "&aOpen" : "&cLocked"))
                                    );
                                }

                                meta.setLore(lore);
                            }

                            if (plugin.getConfig().contains("guild-inventory.guild-gui-item.item-flags")) {
                                for (String flag : plugin.getConfig().getStringList("guild-inventory.guild-gui-item.item-flags")) {
                                    meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                                }
                            }

                            item.setItemMeta(meta);

                            if (plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") == -1) {
                                for (int i = 0; i < plugin.getConfig().getInt("guild-inventory.size"); i++) {
                                    inventory.setItem(i, item);
                                }
                            } else {
                                inventory.setItem(plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") - 1, item);
                            }
                            pos++;
                        }
                    }
                    continue;
                }
                if (!Utils.UIPage.isEmpty() && Utils.UIPage.containsKey(player.getUniqueId())) {
                    if (Utils.UIPage.get(player.getUniqueId()) > 1) {
                        pos = 28 * (Utils.UIPage.get(player.getUniqueId()) - 1);
                        if (pos > Utils.guilds.size()) {
                            Utils.UIPage.put(player.getUniqueId(), Utils.UIPage.get(player.getUniqueId()) - 1);
                            pos = 28 * (Utils.UIPage.get(player.getUniqueId()) - 1);
                            player.openInventory(this.getInventory());
                        }
                    }

                    if (Utils.guilds.isEmpty() || pos >= Utils.guilds.size() - 1) {
                        ItemStack item = new ItemStack(Material.AIR);
                        if (plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") == -1) {
                            for (int i = 0; i < plugin.getConfig().getInt("guild-inventory.size"); i++) {
                                inventory.setItem(i, item);
                            }
                        } else {
                            inventory.setItem(plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") - 1, item);
                        }
                    } else {
                        Guild guild = Utils.guilds.get(pos);
                        ItemStack item = new ItemStack(guild.getIcon());
                        if (plugin.getConfig().contains("guild-inventory.guild-gui-item.amount")) {
                            item.setAmount(plugin.getConfig().getInt("guild-inventory.guild-gui-item.amount"));
                        }

                        ItemMeta meta = item.getItemMeta();
                        guild.refreshWorth();

                        meta.setDisplayName(Utils.chatColor(plugin.getConfig().getString("guild-inventory.guild-gui-item.name")
                                .replaceAll("%guild_name%", guild.getName())));

                        if (item.getType().equals(Material.PLAYER_HEAD)) {
                            SkullMeta skullMeta = (SkullMeta) meta;
                            skullMeta.setOwningPlayer(Bukkit.getPlayer(guild.getOwnerID()));
                        }

                        if (plugin.getConfig().contains("guild-inventory.guild-gui-item.lore")) {
                            List<String> lore = new ArrayList<>();
                            for (String line : plugin.getConfig().getStringList("guild-inventory.guild-gui-item.lore")) {

                                lore.add(Utils.chatColor(line, guild.getWorth())
                                        .replaceAll("%owner%", guild.getOwner())
                                        .replaceAll("%members%", "" + guild.getMembers().size())
                                        .replaceAll("%kills%", "" + guild.getKills())
                                        .replaceAll("%open%", Utils.chatColor(guild.isOpen() ? "&aOpen" : "&cLocked"))
                                );
                            }

                            meta.setLore(lore);
                        }

                        if (plugin.getConfig().contains("guild-inventory.guild-gui-item.item-flags")) {
                            for (String flag : plugin.getConfig().getStringList("guild-inventory.guild-gui-item.item-flags")) {
                                meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                            }
                        }

                        item.setItemMeta(meta);

                        if (plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") == -1) {
                            for (int i = 0; i < plugin.getConfig().getInt("guild-inventory.size"); i++) {
                                inventory.setItem(i, item);
                            }
                        } else {
                            inventory.setItem(plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") - 1, item);
                        }
                    }
                    pos++;
                }
            } else {
                ItemStack item = new ItemStack(Material.getMaterial(plugin.getConfig().getString("guild-inventory.contents." + key + ".item").toUpperCase()));
                if (plugin.getConfig().contains("guild-inventory.contents." + key + ".amount")) {
                    item.setAmount(plugin.getConfig().getInt("guild-inventory.contents." + key + ".amount"));
                }

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Utils.chatColor(plugin.getConfig().getString("guild-inventory.contents." + key + ".name")));
                if (plugin.getConfig().contains("guild-inventory.contents." + key + ".lore")) {
                    meta.setLore(Utils.getConvertedLore(plugin.getConfig(), "guild-inventory.contents." + key));
                }

                if (plugin.getConfig().contains("guild-inventory.contents." + key + ".item-flags")) {
                    for (String flag : plugin.getConfig().getStringList("guild-inventory.contents." + key + ".item-flags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }

                if (plugin.getConfig().contains("guild-inventory.contents." + key + ".type")) {
                    if (plugin.getConfig().contains("guild-inventory.guild-gui-item.lore") && plugin.getConfig().getString("guild-inventory.contents." + key + ".type").equalsIgnoreCase("information")) {
                        List<String> lore = new ArrayList<>();
                        if (Utils.inGuild(player)) {
                            for (String line : plugin.getConfig().getStringList("guild-inventory.guild-gui-item.lore")) {
                                Guild guild = Utils.getGuild(player);

                                if (line.contains("&eClick to join!")) {
                                    lore.remove(lore.size() - 1);
                                } else {
                                    lore.add(Utils.chatColor(line, guild.getWorth())
                                            .replaceAll("%owner%", guild.getOwner())
                                            .replaceAll("%members%", "" + guild.getMembers().size())
                                            .replaceAll("%kills%", "" + guild.getKills())
                                            .replaceAll("%open%", Utils.chatColor(guild.isOpen() ? "&aOpen" : "&cPrivate    "))
                                    );
                                }

                            }
                        } else {
                            lore.add(Utils.chatColor("&fGuild not found"));
                        }

                        meta.setLore(lore);
                    } else if (plugin.getConfig().getString("guild-inventory.contents." + key + ".type").equalsIgnoreCase("GoBack")) {
                        if (Utils.UIPage.get(player.getUniqueId()) == 1) {
                            continue;
                        }
                    } else if (plugin.getConfig().getString("guild-inventory.contents." + key + ".type").equalsIgnoreCase("GoForward")) {
                        if (!(Utils.guilds.size() > 28)) {
                            continue;
                        }
                    }
                }

                item.setItemMeta(meta);

                if (plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") == -1) {
                    for (int i = 0; i < plugin.getConfig().getInt("guild-inventory.size"); i++) {
                        inventory.setItem(i, item);
                    }
                } else {
                    inventory.setItem(plugin.getConfig().getInt("guild-inventory.contents." + key + ".slot") - 1, item);
                }
            }
        }
    }

    public void onClick(Main plugin, Player player, int slot, ClickType type) {
    }

    public void onOpen(Main plugin, Player player) {
    }

    public void onClose(Main plugin, Player player) {
        if (!Utils.filteredGuilds.isEmpty() && Utils.filteredGuilds.containsKey(player.getUniqueId())) {
            Utils.filteredGuilds.remove(player.getUniqueId());
        }

//        if (!Utils.UIPage.isEmpty() && Utils.UIPage.containsKey(player.getUniqueId())) {
//            Utils.UIPage.put(player.getUniqueId(), 1);
//        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

}
