package xyz.gameoholic.lumbergame.game.mob;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.AttackTreeGoal;
import xyz.gameoholic.lumbergame.game.goal.CreeperAttackTreeGoal;
import net.minecraft.world.entity.monster.Creeper;

public class TreeMob extends Mob {

    public TreeMob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        super(plugin, mobType, CR, location);

        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation();

        net.minecraft.world.entity.Mob NMSMob = ((CraftMob) mob).getHandle();
        NMSMob.goalSelector.removeAllGoals(goal -> true);

        Goal goal;
        if (NMSMob instanceof Creeper) {
            goal = new CreeperAttackTreeGoal(
                plugin,
                (org.bukkit.entity.Creeper) mob,
                (Creeper) NMSMob,
                1.0D,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z())
            );
        }
        else {
            goal = new AttackTreeGoal(
                    plugin,
                    (PathfinderMob) NMSMob,
                    1.0D,
                    new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z())
                );
        }
        NMSMob.goalSelector.addGoal(8, goal);



    }
}
