package me.darkwinged.RiotGuilds.Events;

import me.darkwinged.RiotGuilds.libaries.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FriendlyFire implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Utils.inGuild(player)) {
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    if (Utils.inGuild(damager)) {
                        if (Utils.getGuild(player) == Utils.getGuild(damager)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

}
