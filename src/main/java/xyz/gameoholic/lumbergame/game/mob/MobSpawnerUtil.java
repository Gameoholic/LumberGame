package xyz.gameoholic.lumbergame.game.mob;

import org.bukkit.Location;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

public class MobSpawnerUtil {

    /**
     * Spawns and returns the Mob.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public static Mob spawnMob(LumberGamePlugin plugin, String mobTypeId, int CR, Location location) {
        MobType mobType = plugin.getLumberConfig().mobTypes()
            .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeId)).findFirst().get();
        if (mobType.isHostile())
            return new Mob(plugin, mobType, CR, location);
        return new TreeMob(plugin, mobType, CR, location);
    }
}
