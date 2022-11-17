package me.darkwinged.RiotGuilds.Events;

import me.darkwinged.RiotGuilds.Main;
import me.darkwinged.RiotGuilds.libaries.Guild;
import me.darkwinged.RiotGuilds.libaries.Menu;
import me.darkwinged.RiotGuilds.libaries.Utils;
import me.darkwinged.RiotGuilds.menus.menuGuild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class menuEvents implements Listener {

    private final Main plugin = Main.getInstance;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu)) return;
        ((Menu) holder).onClick(plugin, (Player) event.getWhoClicked(), event.getSlot(), event.getClick());

        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        String inventoryName = event.getView().getTitle();

        if (item == null || item.getType() == Material.AIR) return;

        if (inventoryName.equals(Utils.chatColor(plugin.getConfig().getString("guild-inventory.title")) + " - " + Utils.UIPage.get(player.getUniqueId()))) {
            for (String key : plugin.getConfig().getConfigurationSection("guild-inventory.contents").getKeys(false)) {
                if (plugin.getConfig().contains("guild-inventory.contents." + key + ".item")) {
                    if (item.getType().equals(Material.getMaterial(plugin.getConfig().getString("guild-inventory.contents." + key + ".item").toUpperCase())) &&
                            item.getItemMeta().getDisplayName().equals(Utils.chatColor(plugin.getConfig().getString("guild-inventory.contents." + key + ".name")))) {
                        if (plugin.getConfig().contains("guild-inventory.contents." + key + ".type")) {
                            switch (plugin.getConfig().getString("guild-inventory.contents." + key + ".type").toLowerCase()) {
                                case "search":
                                    List<String> lines = new ArrayList<>();
                                    lines.add("");
                                    lines.add("^^^^^^^^^^^^^^^");
                                    lines.add("Enter a guilds");
                                    lines.add("name.");

                                    Utils.openSign(player, lines);
                                    event.setCancelled(true);
                                    return;
                                case "goback":
                                    if (!Utils.UIPage.isEmpty() && Utils.UIPage.get(player.getUniqueId()) != 1) {
                                        Utils.UIPage.put(player.getUniqueId(), Utils.UIPage.get(player.getUniqueId()) - 1);
                                        player.openInventory(new menuGuild(player).getInventory());
                                        event.setCancelled(true);
                                    }
                                    return;
                                case "goforward":
                                    if (!Utils.UIPage.isEmpty() && Utils.UIPage.containsKey(player.getUniqueId())) {
                                        Utils.UIPage.put(player.getUniqueId(), Utils.UIPage.get(player.getUniqueId()) + 1);
                                        player.openInventory(new menuGuild(player).getInventory());
                                        event.setCancelled(true);
                                    }
                                    return;
                                case "close":
                                    player.closeInventory();
                                    Utils.UIPage.remove(player.getUniqueId());
                                    event.setCancelled(true);
                                    return;
                                case "information":
                                    event.setCancelled(true);
                                    return;
                            }
                        } else {
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    if (plugin.getConfig().contains("guild-inventory.contents." + key + ".type")) {
                        if (item.getType().toString().equals(plugin.getConfig().getString("guild-inventory.contents." + key + ".type").toUpperCase()))
                            switch (plugin.getConfig().getString("guild-inventory.contents." + key + ".type").toLowerCase()) {
                                case "guild":
                                    if (Utils.guilds.isEmpty()) continue;
                                    Guild guild = Utils.guilds.get(Utils.slotToArrayIndex(event.getSlot(), player));
                                    if (Utils.inGuild(player)) {
                                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("already-in-guild")));
                                        event.setCancelled(true);
                                        continue;
                                    }

                                    if (guild.isOpen()) {
                                        Utils.addPlayerToClaims(Utils.getPlayerClaim(guild.getOwnerID()), player);
                                        List<UUID> membersID = guild.getMembersID();
                                        List<String> members = guild.getMembers();

                                        members.add(player.getName());
                                        membersID.add(player.getUniqueId());
                                        guild.refreshWorth();

                                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                                plugin.fileMessage.getConfig().getString("guild-join")).replaceAll("%guild%", guild.getName()));

                                        for (Player online : Bukkit.getOnlinePlayers()) {
                                            if (guild.getMembers().contains(online.getName())) {
                                                if (online == player) continue;
                                                online.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                                        plugin.fileMessage.getConfig().getString("guild-join-members")).replaceAll("%player%", player.getName()));
                                            }
                                        }
                                    } else {
                                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                                plugin.fileMessage.getConfig().getString("guild-privatised")));
                                    }

                                    event.setCancelled(true);
                            }
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu)
            ((Menu) holder).onOpen(plugin, (Player) event.getPlayer());
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu)
            ((Menu) holder).onClose(plugin, (Player) event.getPlayer());
    }

}