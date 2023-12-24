package xyz.gameoholic.lumbergame.game.goal.hostile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

/**
 * Represents a melee attack goal for mobs where they track a player and attack them when close.
 * Differences to vanilla melee attack goal:
 * Never stops targeting player, recalculates path every tick, ignores whether obstructed or not seen,
 * and doesn't stop locking on to player.
 */
public class LumberMeleeAttackGoal extends MeleeAttackGoal {
    private int ticksUntilNextAttack;
    private final int attackCooldown; // LUMBER - custom attack cooldown

    public LumberMeleeAttackGoal(PathfinderMob mob, int attackCooldown) {
        super(mob, 1.0, false);
        this.attackCooldown = attackCooldown;
    }


    @Override
    public boolean canContinueToUse() {
        // Lumber - follow target even if not seen or has restricted access
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else {
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
        }
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        // Lumber - remove path recalculation logic. Recalculate path every tick. //todo: in the futue maybe reintroduce it to make it less jittery?
        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            double d = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(livingEntity);

            this.mob.getNavigation().moveTo(livingEntity, 1.0);

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingEntity, d);
        }
    }


    // Lumber - reset attack cooldown to what we want
    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackCooldown);
    }

}
