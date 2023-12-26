package xyz.gameoholic.lumbergame.util;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.TreeDeadParticle;
import xyz.gameoholic.lumbergame.TreePassiveParticle;
import xyz.gameoholic.partigon.particle.PartigonParticle;

public class ParticleManager {

    private final LumberGamePlugin plugin;
    public ParticleManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    public void spawnTreePassiveParticle(Location location) {
        PartigonParticle particle = TreePassiveParticle.INSTANCE.getParticle(location);
        particle.start();
    }

    public void spawnTreeDeadParticle(LumberGamePlugin plugin, Location location) {
        PartigonParticle particle = TreeDeadParticle.INSTANCE.getParticle(location);
        particle.start();

        new BukkitRunnable() {
            @Override
            public void run() {
                particle.stop();
            }
        }.runTaskLater(plugin, 1L);
    }
}
