package xyz.gameoholic.lumbergame.util;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.TreeDeadParticle;
import xyz.gameoholic.lumbergame.TreeLevelUpParticle;
import xyz.gameoholic.lumbergame.TreePassiveParticle;
import xyz.gameoholic.partigon.particle.PartigonParticle;

public class ParticleManager {

    private final LumberGamePlugin plugin;
    private PartigonParticle treePassiveParticle;
    public ParticleManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Stops all active particle animations.
     */
    public void stopParticles() {
        treePassiveParticle.stop();
    }

    public void spawnTreeLevelUpParticle() {
        TreeLevelUpParticle.INSTANCE.getParticle(plugin.getLumberConfig().mapConfig().treeLocation()).start();
    }
    public void spawnTreePassiveParticle() {
        Location loc = plugin.getLumberConfig().mapConfig().treeLocation();
        loc.setY(loc.getY() + 0.1); // Particle needs to be offsetted upwards
        treePassiveParticle = TreePassiveParticle.INSTANCE.getParticle(loc);
        treePassiveParticle.start();
    }

    public void spawnTreeDeadParticle() {
        TreeDeadParticle.INSTANCE.getParticle(plugin.getLumberConfig().mapConfig().treeLocation()).start();
    }
}
