package xyz.gameoholic.lumbergame.util;

import org.bukkit.Location;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.game.mob.TreeMob;

public class MobUtil {

    /**
     * Spawns and returns the Mob.
     * @param mobTypeID The ID of the mob type.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param location The location to spawn the mob at.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public static Mob spawnMob(LumberGamePlugin plugin, String mobTypeID, int CR, Location location) {
        MobType mobType = plugin.getLumberConfig().mobTypes()
            .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get();
        if (mobType.isHostile())
            return new Mob(plugin, mobType, CR, location);
        return new TreeMob(plugin, mobType, CR, location);
    }

    /**
     * Spawns and returns the Mob.
     * @param mobType The Lumber MobType of the mob.
     * @param CR The Challenge Rating to spawn the mob with.
     * @param location The location to spawn the mob at.
     */
    public static Mob spawnMob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        if (mobType.isHostile())
            return new Mob(plugin, mobType, CR, location);
        return new TreeMob(plugin, mobType, CR, location);
    }

    /**
     * Returns the mob.
     * @param mobTypeID The ID of the MobType.
     * @throws java.util.NoSuchElementException if mobTypeId doesn't correspond to a loaded mob type.
     */
    public static MobType getMobType(LumberGamePlugin plugin, String mobTypeID) {
        return plugin.getLumberConfig().mobTypes()
            .stream().filter(filteredMobType -> filteredMobType.id().equals(mobTypeID)).findFirst().get();
    }
}
