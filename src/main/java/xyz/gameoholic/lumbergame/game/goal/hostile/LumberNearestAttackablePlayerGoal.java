package xyz.gameoholic.lumbergame.game.goal.hostile;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;


/**
 * Goal that makes the mob locate and target the nearest player, regardless of line of sight and obstruction.
 * Range must still be given to the mob using Attribute.GENERIC_FOLLOW_RANGE.
 */
public class LumberNearestAttackablePlayerGoal extends NearestAttackableTargetGoal {

    public LumberNearestAttackablePlayerGoal(Mob mob) {
        super(mob, Player.class, 10, false, false, null); // Lumber - Disable visibility and navigation checks
        targetConditions = TargetingConditions.forCombat().selector(null).ignoreLineOfSight(); // Lumber - IGNORE LINE OF SIGHT
    }


}
