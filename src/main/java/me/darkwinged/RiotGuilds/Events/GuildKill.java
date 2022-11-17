package me.darkwinged.RiotGuilds.Events;

import me.darkwinged.RiotGuilds.libaries.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GuildKill implements Listener {

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getLastDamageCause() instanceof Player) {
            Player damager = (Player) player.getLastDamageCause();
            if (Utils.inGuild(damager)) {
                if (Utils.inGuild(player)) {
                    if (Utils.getGuild(player) != Utils.getGuild(damager)) {
                        Utils.getGuild(damager).setKills(Utils.getGuild(damager).getKills() + 1);
                    }
                } else {
                    Utils.getGuild(damager).setKills(Utils.getGuild(damager).getKills() + 1);
                }
            }
        }
    }

}
