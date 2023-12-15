package xyz.gameoholic.lumbergame.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.game.mob.MobType;
import xyz.gameoholic.lumbergame.game.mob.TreeMob;

import javax.annotation.Nullable;

public class MobUtil {

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
