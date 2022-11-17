package me.darkwinged.RiotGuilds.commands;

import me.darkwinged.RiotGuilds.Main;
import me.darkwinged.RiotGuilds.libaries.Guild;
import me.darkwinged.RiotGuilds.libaries.Utils;
import me.darkwinged.RiotGuilds.menus.menuGuild;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class cmdGuild implements CommandExecutor {

    private final Main plugin = Main.getInstance;
    private Map<UUID, UUID> requests = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (cmd.getName().equalsIgnoreCase("guild")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("console")));
                return true;
            }
            Player player = (Player) sender;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length == 2) {
                        if (!Utils.isJustLetters(args[1])) {
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("name-must-be-string")));
                            return true;
                        }
                        if (Utils.inGuild(player)) {
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("already-in-guild")));
                            return true;
                        }

                        List<UUID> membersID = new ArrayList<>();
                        membersID.add(player.getUniqueId());

                        List<String> members = new ArrayList<>();
                        members.add(player.getName());

                        Guild guild = new Guild(player.getName(), player.getUniqueId(), args[1], members, membersID, false, true, plugin.getEconomy().getBalance(player), 0,
                                Material.getMaterial(plugin.getConfig().getString("guild-inventory.guild-gui-item.item").toUpperCase()));
                        Utils.guilds.add(guild);
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("guild-created")));

                    } else {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("specify-name")));
                    }
                } else if (args[0].equalsIgnoreCase("disband")) {
                    if (!Utils.inGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("not-in-guild")));
                        return true;
                    }
                    if (!Utils.isOwnerGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-owner-guild")));
                        return true;
                    }
                    Guild guild = Utils.getGuild(player);
                    File file = new File(plugin.getDataFolder() + "/Guilds", guild.getName() + ".yml");

                    Utils.guilds.remove(guild);
                    if (file.exists()) {
                        file.delete();
                    }

                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("guild-disbanded")));

                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (!Utils.inGuild(player)) {
                        sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("not-in-guild")));
                        return true;
                    }
                    if (Utils.isOwnerGuild(player)) {
                        player.performCommand("guild disband");
                        return true;
                    }
                    Guild guild = Utils.getGuild(player);
                    guild.getMembers().remove(player.getName());
                    guild.getMembersID().remove(player.getUniqueId());
                    guild.refreshWorth();

                    Utils.removePlayerFromClaims(Utils.getPlayerClaim(guild.getOwnerID()), player);

                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("guild-left")));

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (guild.getMembers().contains(online.getName())) {
                            online.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-left-members")).replaceAll("%player%", player.getName()));
                        }
                    }

                } else if (args[0].equalsIgnoreCase("invite")) {
                    if (!Utils.inGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("not-in-guild")));
                        return true;
                    }
                    if (!Utils.isOwnerGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-owner-guild")));
                        return true;
                    }
                    if (args.length != 2) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("specify-player")));
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-player-found")
                                .replaceAll("%player%", target.getName())
                                .replaceAll("%target%", target.getName())));
                        return true;
                    }
                    if (Utils.inGuild(target)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("user-in-guild")));
                        return true;
                    }

                    requests.put(target.getUniqueId(), player.getUniqueId());

                    // --------------------------------------------------

                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-invite-sent"))
                            .replaceAll("%player%", target.getName())
                            .replaceAll("%target%", target.getName()));

                    // --------------------------------------------------

                    TextComponent yesComponent = new TextComponent(Utils.chatColor("&a&l[ ACCEPT ] "));
                    TextComponent noComponent = new TextComponent(Utils.chatColor(" &C&l[ DENY ]"));

                    yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild bypass DmXAyXqAb3 join"));
                    noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild bypass DmXAyXqAb3 deny"));

                    target.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-invite-sent-member"))
                            .replaceAll("%player%", player.getName())
                            .replaceAll("%target%", player.getName()));
                    target.spigot().sendMessage(yesComponent, noComponent);

                    // --------------------------------------------------

                } else if (args[0].equalsIgnoreCase("join")) {
                    if (Utils.inGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("already-in-guild")));
                        return true;
                    }

                    if (args.length != 2) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("specify-player")));
                        return true;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-player-found")
                                .replaceAll("%player%", target.getName())
                                .replaceAll("%target%", target.getName())));
                        return true;
                    }
                    if (!Utils.inGuild(target)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("user-not-in-guild")));
                        return true;
                    }

                    Guild guild = Utils.getGuild(target);

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

                } else if (args[0].equalsIgnoreCase("bypass")) {
                    if (requests.isEmpty() || !requests.containsKey(player.getUniqueId())) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-pending-invitation")));
                        return true;
                    }
                    // ---- [ DmXAyXqAb3 - for security reasons ] ----
                    if (args[1].equals("DmXAyXqAb3")) {
                        if (args[2].equalsIgnoreCase("join")) {
                            for (Guild guild : Utils.guilds) {
                                if (guild.getOwnerID().equals(requests.get(player.getUniqueId()))) {
                                    Utils.addPlayerToClaims(Utils.getPlayerClaim(guild.getOwnerID()), player);
                                    List<UUID> membersID = guild.getMembersID();
                                    List<String> members = guild.getMembers();

                                    members.add(player.getName());
                                    membersID.add(player.getUniqueId());
                                    guild.refreshWorth();

                                    requests.remove(player.getUniqueId());
                                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                            plugin.fileMessage.getConfig().getString("guild-invite-accepted")));
                                    return true;
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("deny")) {
                            requests.remove(player.getUniqueId());
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-invite-denied")));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (!Utils.inGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("not-in-guild")));
                        return true;
                    }
                    if (!Utils.isOwnerGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-owner-guild")));
                        return true;
                    }
                    if (args.length != 2) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("specify-player")));
                        return true;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-player-found")
                                .replaceAll("%player%", target.getName())
                                .replaceAll("%target%", target.getName())));
                        return true;
                    }

                    Guild guild = Utils.getGuild(target);
                    guild.getMembers().remove(target.getName());
                    guild.getMembersID().remove(target.getUniqueId());
                    guild.refreshWorth();
                    Utils.removePlayerFromClaims(Utils.getPlayerClaim(guild.getOwnerID()), target);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (guild.getMembers().contains(online.getName())) {
                            online.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-left-members")).replaceAll("%player%", target.getName()).replaceAll("%guild%", guild.getName()));
                        }
                    }

                    player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("guild-kicked-member")).replaceAll("%player%", target.getName()));

                    target.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                            plugin.fileMessage.getConfig().getString("guild-kicked")));

                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length != 2) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("option-to-set")));
                        return true;
                    }
                    if (!Utils.inGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("not-in-guild")));
                        return true;
                    }
                    if (!Utils.isOwnerGuild(player)) {
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("no-owner-guild")));
                        return true;
                    }
                    Guild guild = Utils.getGuild(player);

                    if (args[1].equalsIgnoreCase("icon")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType().equals(Material.AIR)) return true;

                        guild.setIcon(item.getType());
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("guild-icon-changed")));
                    } else if (args[1].equalsIgnoreCase("open")) {
                        if (guild.isOpen()) {
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("guild-already-open")));
                            return true;
                        }

                        guild.setOpen(true);
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("guild-open")));

                    } else if (args[1].equalsIgnoreCase("close")) {
                        if (!guild.isOpen()) {
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("guild-already-closed")));
                            return true;
                        }

                        guild.setOpen(true);
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("guild-closed")));
                    } else if (args[1].equalsIgnoreCase("ff") || args[1].equalsIgnoreCase("friendlyfire")) {
                        if (!guild.isFriendlyFire()) {
                            guild.setFriendlyFire(true);
                            player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                    plugin.fileMessage.getConfig().getString("guild-friendly-fire-enabled")));
                            return true;
                        }

                        guild.setFriendlyFire(false);
                        player.sendMessage(Utils.chatColor(plugin.fileMessage.getConfig().getString("prefix") +
                                plugin.fileMessage.getConfig().getString("guild-friendly-fire-disabled")));
                    }

                } else {
                    menuGuild menu = new menuGuild(player);
                    player.openInventory(menu.getInventory());
                }
            } else {
                menuGuild menu = new menuGuild(player);
                player.openInventory(menu.getInventory());
            }
        }
        return false;
    }

}
