package xyz.gameoholic.lumbergame.game.goal.hostile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

/**
 * Goial for custom ignition/unignition when in range of a player.
 */
public class LumberCreeperAttackGoal extends LumberMeleeAttackGoal {
    private final Creeper creeper = (Creeper) mob;
    /**
     * Range (squared) needed for creeper to ignite/unignite.
     */
    private static final double CREEPER_IGNITION_SQUARED_RANGE = 9.0;

    public LumberCreeperAttackGoal(PathfinderMob mob, int attackCooldown) {
        super(mob, attackCooldown);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        double squaredDistance = target.distanceToSqr(mob); // Lumber - custom distance calculation equivalent to 1.20.1 NMS
        // If needs to cancel ignition
        if (creeper.isIgnited() && (squaredDistance > CREEPER_IGNITION_SQUARED_RANGE ||
            mob.getTarget().isSpectator() ||
            (mob.getTarget() instanceof Player player && player.isCreative())
        )) {
            creeper.setSwellDir(-1);
            creeper.swell = 0;
            creeper.setIgnited(false);
            resetAttackCooldown();
        }
        if (squaredDistance <= CREEPER_IGNITION_SQUARED_RANGE && ticksUntilNextAttack <= 0) {
            performAttack();
        }
    }

    private void performAttack() {
        creeper.setIgnited(true);

        ticksUntilNextAttack = Integer.MAX_VALUE;
    }
}
