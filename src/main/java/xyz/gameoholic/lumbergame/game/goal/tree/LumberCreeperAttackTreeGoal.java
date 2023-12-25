package xyz.gameoholic.lumbergame.game.goal.tree;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

/**
 * Goal that makes the mocreeper locate, target and attack the TREE, regardless of line and sight. Ignites when near tree,
 * unignites if exits range.
 */
public class LumberCreeperAttackTreeGoal extends LumberAttackTreeGoal {
    private final Creeper creeper = (Creeper) mob;
    /**
     * Range (squared) needed for creeper to ignite/unignite.
     */
    private static final double CREEPER_IGNITION_SQUARED_RANGE = 9.0;

    /**
     * @param plugin
     * @param mob
     * @param targetLoc Location of the tree to pathfind to and ignite at.
     */
    public LumberCreeperAttackTreeGoal(LumberGamePlugin plugin, Creeper mob, Vec3 targetLoc) {
        super(plugin, mob, targetLoc, 20); // We provide 20 as attack cooldown because creepers don't really have that..
    }


    @Override
    protected void checkAndPerformAttack(double squaredDistance) {
        // If needs to cancel ignition
        if (squaredDistance > CREEPER_IGNITION_SQUARED_RANGE && creeper.isIgnited()) {
            creeper.setSwellDir(-1);
            creeper.swell = 0;
            creeper.setIgnited(false);
            resetAttackCooldown();
        }
        if (squaredDistance <= CREEPER_IGNITION_SQUARED_RANGE && ticksUntilNextAttack <= 0) {
            performAttack();
        }
    }

    @Override
    protected void performAttack() {
        creeper.setIgnited(true);

        ticksUntilNextAttack = Integer.MAX_VALUE;
    }
}
