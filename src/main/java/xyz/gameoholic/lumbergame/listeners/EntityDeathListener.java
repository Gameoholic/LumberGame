package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;

import javax.annotation.Nullable;

public class EntityDeathListener implements Listener {
    private LumberGamePlugin plugin;
    public EntityDeathListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        @Nullable Mob mob = plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId());
        Bukkit.broadcastMessage("Mob " + mob + " died");
        if (mob == null)
            return;

        mob.onDeath();
    }
}
