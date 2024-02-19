package xyz.gameoholic.lumbergame.game.item.firestaff;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.gameoholic.lumbergame.FireRingParticle;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.partigon.particle.PartigonParticle;

import javax.annotation.Nullable;
import java.util.UUID;

public class FireRing {
    private final LumberGamePlugin plugin;
    int aliveTime = 0;
    private final static int MAX_ALIVE_TIME = 80;
    private final static int SOUND_COOLDOWN = 10;
    private int soundRemainingCooldown = 0;
    private int hurtSoundRemainingCooldown = 0;
    final double dps;
    private final Vector direction;
    private final Marker fireRingMarker;
    private final BukkitTask task;
    private final PartigonParticle particle;
    private final UUID itemOwnerUUID;

    public FireRing(LumberGamePlugin plugin, Location playerLocation, Vector direction, double dps, UUID itemOwnerUUID) {
        this.plugin = plugin;
        this.direction = direction.multiply(0.5);
        this.dps = dps;
        this.itemOwnerUUID = itemOwnerUUID;
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
        if (aliveTime == MAX_ALIVE_TIME) {
            destroy();
            return;
        }
        advanceFireRing();
        aliveTime++;
    }

    private void advanceFireRing() {
        fireRingMarker.teleport(fireRingMarker.getLocation().add(direction));
        // Play passive sound
        if (soundRemainingCooldown == 0) {
            for (LumberPlayer lumberPlayer : plugin.getGameManager().getPlayers())
                lumberPlayer.playSound(plugin.getLumberConfig().soundsConfig().fireWandSound(), fireRingMarker.getLocation());
            soundRemainingCooldown = SOUND_COOLDOWN + 1;
        }
        // Damage logic
        for (Entity nearbyEntity : fireRingMarker.getNearbyEntities(1.0, 1.0, 1.0)) {
            if (!(nearbyEntity instanceof LivingEntity livingEntity) || (nearbyEntity instanceof Player))
                continue;
            livingEntity.damage(dps / 2.0);
            livingEntity.setNoDamageTicks(10); // Deal damage every 10 ticks - more satisfying
            // Play hurt sound
            if (hurtSoundRemainingCooldown == 0) {
                // Play sound at mob for every player except for the item owner
                for (LumberPlayer lumberPlayer : plugin.getGameManager().getPlayers().stream().filter(it -> !it.getUuid().equals(itemOwnerUUID)).toList())
                    lumberPlayer.playSound(plugin.getLumberConfig().soundsConfig().fireWandHurtSound(), livingEntity.getLocation());
                // Play sound for item owner regardless of location
                @Nullable Player itemOwner = Bukkit.getPlayer(itemOwnerUUID);
                if (itemOwner != null)
                    itemOwner.playSound(plugin.getLumberConfig().soundsConfig().fireWandHurtSound());
                hurtSoundRemainingCooldown = SOUND_COOLDOWN + 1;
            }
        }
        soundRemainingCooldown = Math.max(soundRemainingCooldown - 1, 0);
        hurtSoundRemainingCooldown = Math.max(hurtSoundRemainingCooldown - 1, 0);
    }

    private void destroy() {
        task.cancel();
        particle.stop();
    }
}
