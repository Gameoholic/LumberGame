package xyz.gameoholic.lumbergame.game.mob;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.tree.LumberAttackTreeGoal;
import xyz.gameoholic.lumbergame.game.goal.tree.LumberCreeperAttackTreeGoal;
import net.minecraft.world.entity.monster.Creeper;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;

import java.util.Map;

public class TreeLumberMob extends LumberMob {


    /**
     * Use WaveManager to instantiate, don't use this constructor directly.
     * @param mobType         The Lumber MobType of the mob.
     * @param CR              The Challenge Rating to spawn the mob with.
     * @param boneBlock       Whether the mob has a bone block.
     * @param guaranteedSingleSpawn Whether the mob was a guaranteed spawn.
     */
    public TreeLumberMob(LumberGamePlugin plugin, MobType mobType, int CR, boolean boneBlock, boolean guaranteedSingleSpawn) {
        super(plugin, mobType, CR, boneBlock, guaranteedSingleSpawn);
    }

    @Override
    public void spawnMob(Location location) {
        super.spawnMob(location);

        // Add tree mob green glow, if doesn't already have glow (aka is not in a team)
        if (Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(mob) == null) {
            mob.setGlowing(true);
            plugin.getGameManager().getGreenTeam().addEntity(mob);
        }
    }


    /**
     * Applies NMS Goals on mobs to make them target and attack the tree.
     */
    @Override
    protected void applyGoals() {
        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation();
        net.minecraft.world.entity.Mob NMSMob = ((CraftMob) mob).getHandle();
        NMSMob.goalSelector.removeAllGoals(goal -> true);

        // Attack speed is needed for goal, can't be added via mob attribute.
        int attackCooldown = (int) ExpressionUtil.evaluateExpression(
            getMobType().attackCooldownExpression(), Map.of("CR", (double) getCR()));

        Goal goal;
        if (NMSMob instanceof Creeper) {
            goal = new LumberCreeperAttackTreeGoal(
                plugin,
                (Creeper) NMSMob,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z()),
                attackCooldown
            );
        }
        else {
            goal = new LumberAttackTreeGoal(
                plugin,
                (PathfinderMob) NMSMob,
                new Vec3(treeLocation.x(), treeLocation.y(), treeLocation.z()),
                attackCooldown
            );
        }
        NMSMob.goalSelector.addGoal(0, goal);
    }
}
