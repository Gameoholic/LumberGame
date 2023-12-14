package xyz.gameoholic.lumbergame.game.mob;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.AttackTreeGoal;

public class TreeMob extends Mob {

    public TreeMob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        super(plugin, mobType, CR, location);

        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation();

        ((CraftMob) mob).getHandle().goalSelector.removeAllGoals(goal -> true);
        ((CraftMob) mob).getHandle().goalSelector.addGoal(8,
            new AttackTreeGoal(
                plugin,
                (PathfinderMob) ((CraftMob) mob).getHandle(),
                1.0D,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z())
            ));
    }
}
