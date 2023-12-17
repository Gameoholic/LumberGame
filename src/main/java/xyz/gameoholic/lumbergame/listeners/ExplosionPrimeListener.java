package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;

import javax.annotation.Nullable;

public class ExplosionPrimeListener implements Listener {
    private final LumberGamePlugin plugin;

    public ExplosionPrimeListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    // Creeper does not go through normal death logic cycle when it explodes, and neither does tnt
    @EventHandler
    public void onExplosionPrimeEvent(ExplosionPrimeEvent e) {
        if (plugin.getGameManager().getWaveManager() == null)
            return;
        @Nullable Mob mob = plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId());
        if (mob == null)
            return;

        mob.onDeath(false);
    }
}
