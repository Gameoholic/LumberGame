package xyz.gameoholic.lumbergame.util;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.TreeDeadParticle;
import xyz.gameoholic.partigon.particle.PartigonParticle;

public class ParticleUtil {
    public static void spawnTreeDeadParticle(LumberGamePlugin plugin, Location location) {
        PartigonParticle particle = TreeDeadParticle.INSTANCE.getParticle(location);
        particle.start();
        particle.stop();
    }
}
