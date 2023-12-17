package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
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
        if (plugin.getGameManager().getWaveManager() == null)
            return;
        @Nullable Mob mob = plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId());
        if (mob == null)
            return;

        // Mobs should only drop loot in case a player killed it
        boolean dropLoot = false;
        if (e.getEntity().getLastDamageCause() != null &&
            e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            if (damageEvent.getDamager().getType() == EntityType.PLAYER)
                dropLoot = true;
        }

        e.getDrops().clear();
        mob.onDeath(dropLoot);
    }
}
