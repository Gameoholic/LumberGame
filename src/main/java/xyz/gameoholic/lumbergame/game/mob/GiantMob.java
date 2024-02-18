package xyz.gameoholic.lumbergame.game.mob;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.gameoholic.lumbergame.GiantFootstepsParticle;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.Random;

public class GiantMob extends LumberMob {

    private @Nullable BukkitTask giantTask;
    private @Nullable Location lastLocation;
    private Random rnd = new Random();

    /**
     * Use WaveManager to instantiate, don't use this constructor directly.
     *
     * @param mobType   The Lumber MobType of the mob.
     * @param CR        The challenge rating to spawn the mob with.
     * @param boneBlock Whether the mob should spawn with a bone block.
     * @param guaranteedSingleSpawn Whether the mob was spawned on its own.
     */
    public GiantMob(LumberGamePlugin plugin, MobType mobType, int CR, boolean boneBlock, boolean guaranteedSingleSpawn) {
        super(plugin, mobType, CR, boneBlock, guaranteedSingleSpawn);
    }

    @Override
    public void spawnMob(Location location) {
        super.spawnMob(location);

        lastLocation = mob.getLocation().clone();
        giantTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!lastLocation.equals(mob.getLocation())) {
                    onMove();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void onMove() {
        GiantFootstepsParticle.INSTANCE.getParticle(mob).start();
        // Custom mob behavior - Giant deals damage on move
        if (mob.getType() == EntityType.GIANT) {
            for (Entity nearbyEntity : mob.getLocation().getNearbyEntities(3.0, 5.0, 3.0)) { // Relative to feet
                if (!(nearbyEntity instanceof LivingEntity target) || nearbyEntity.getType() == EntityType.GIANT) // Ignore giants
                    continue;
                double damageMultiplier = (target instanceof Player) ? 0.25 : 1.0; // Deal full damage to mobs, 1/4 to players
                target.damage(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * damageMultiplier);
                Vector velocity = new Vector(rnd.nextDouble(-1.0, 1.0), rnd.nextDouble(-1.0, 1.0), rnd.nextDouble(-1.0, 1.0)).multiply(0.5).setY(0.3);
                target.setVelocity(velocity);
            }
        }
        lastLocation = mob.getLocation();
    }

    /**
     * Called when the mob dies.
     * NOT called when it's removed by the plugin on switching rounds or on game start.
     *
     * @param player The player that killed the mob, or null if it died otherwise.
     */
    @Override
    public void onDeath(@Nullable Player player) {
        super.onDeath(player);
        if (giantTask != null)
            giantTask.cancel();
    }

    /**
     * Removes this mob and unregisters its events.
     * Does NOT result in the mob's death.
     */
    @Override
    public void remove() {
        super.remove();
        if (giantTask != null)
            giantTask.cancel();
    }


}
