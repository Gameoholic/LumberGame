package xyz.gameoholic.lumbergame.game.item.firestaff;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.gameoholic.lumbergame.FireRingParticle;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.partigon.particle.PartigonParticle;

public class FireRing {
    int aliveTime = 0;
    final int maxAliveTime = 100;
    final double dps;
    private final Vector direction;
    private final Marker fireRingMarker;
    private final BukkitTask task;
    private final PartigonParticle particle;

    public FireRing(LumberGamePlugin plugin, Location playerLocation, Vector direction, double dps) {
        this.direction = direction.multiply(0.5);
        this.dps = dps;
        Location fireRingSpawnLoc = playerLocation.clone();
        fireRingSpawnLoc.add(0, 2, 0); // Spawn at head, not feet
        fireRingSpawnLoc.add(direction); // Spawn in front of player
        fireRingMarker = (Marker) fireRingSpawnLoc.getWorld().spawnEntity(fireRingSpawnLoc, EntityType.MARKER);
        particle = FireRingParticle.INSTANCE.getParticle(fireRingMarker, playerLocation.getYaw());
        particle.start();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void onTick() {
        if (aliveTime == maxAliveTime) {
            destroy();
            return;
        }
        advanceFireRing();
        aliveTime++;
    }

    private void advanceFireRing() {
        fireRingMarker.teleport(fireRingMarker.getLocation().add(direction));
        for (Entity nearbyEntity : fireRingMarker.getNearbyEntities(1.0, 1.0, 1.0)) {
            if (!(nearbyEntity instanceof LivingEntity livingEntity) || (nearbyEntity instanceof Player))
                continue;
            livingEntity.damage(dps / 2.0);
            livingEntity.setNoDamageTicks(10); // Deal damage every 10 ticks - more satisfying
        }
    }

    private void destroy() {
        task.cancel();
        particle.stop();
    }
}
