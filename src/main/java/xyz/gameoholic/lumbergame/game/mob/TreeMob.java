package xyz.gameoholic.lumbergame.game.mob;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.tree.AttackTreeGoal;
import xyz.gameoholic.lumbergame.game.goal.tree.CreeperAttackTreeGoal;
import net.minecraft.world.entity.monster.Creeper;
import xyz.gameoholic.lumbergame.game.mob.MobType.MobType;

public class TreeMob extends Mob {


    /**
     * Use WaveManager to instantiate, don't use this constructor directly.
     * @param mobType The Lumber MobType of the mob.
     * @param CR The challenge rating to spawn the mob with.
     * @param boneBlock Whether the mob should spawn with a bone block.
     */
    public TreeMob(LumberGamePlugin plugin, MobType mobType, int CR, boolean boneBlock) {
        super(plugin, mobType, CR, boneBlock);
    }

    @Override
    public void spawnMob(Location location) {
        super.spawnMob(location);
    }


    /**
     * Applies NMS Goals on mobs to make them target and attack the tree.
     */
    @Override
    protected void applyGoals() {
        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation();
        net.minecraft.world.entity.Mob NMSMob = ((CraftMob) mob).getHandle();
        NMSMob.goalSelector.removeAllGoals(goal -> true);

        Goal goal;
        if (NMSMob instanceof Creeper) {
            goal = new CreeperAttackTreeGoal(
                plugin,
                (Creeper) NMSMob,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z())
            );
        }
        else {
            goal = new AttackTreeGoal(
                plugin,
                (PathfinderMob) NMSMob,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z())
            );
        }
        NMSMob.goalSelector.addGoal(0, goal);
    }
}
