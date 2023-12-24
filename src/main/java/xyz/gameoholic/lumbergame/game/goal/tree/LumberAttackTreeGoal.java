package xyz.gameoholic.lumbergame.game.goal.tree;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.LumberMob;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Goal that makes the mob locate, target and attack the TREE, regardless of line and sight.
 */
public class LumberAttackTreeGoal extends MeleeAttackGoal {
    private final LumberGamePlugin plugin;
    protected int ticksUntilNextAttack; // Lumber - ticks until next attack, min 0.
    private final Vec3 targetLoc; // LUMBER - target location (tree)
    private final int attackCooldown; // LUMBER - custom attack cooldown
    private static final double TREE_BB_WIDTH = 1.0; // Lumber - custom tree hitbox
    private long lastCanUseCheck;


    /**
     * @param plugin
     * @param mob
     * @param targetLoc The location of the tree to pathfind to and attack.
     */

    public LumberAttackTreeGoal(LumberGamePlugin plugin, PathfinderMob mob, Vec3 targetLoc, int attackCooldown) {
        super(mob, 1.0, false);
        this.plugin = plugin;
        this.targetLoc = targetLoc;
        this.attackCooldown = attackCooldown;
    }

    @Override
    public boolean canUse() {
        if (mob.isNoAi())
            return false;
        long l = mob.level().getGameTime();
        if (l - lastCanUseCheck < 20L) {
            return false;
        } else {
            lastCanUseCheck = l;
            mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, 1.0);
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (mob.isNoAi())
            return false;
        return true;
    }

    @Override
    public void start() {
        // Lumber - navigate to tree's coordinates
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, 1.0);
        ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }


    @Override
    public void tick() {
        // Lumber - remove path recalculation logic. Recalculate path every tick.
        ticksUntilNextAttack = Math.max(ticksUntilNextAttack - 1, 0);

        double squaredDistance = mob.distanceToSqr(targetLoc);
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, 1.0);

        checkAndPerformAttack(squaredDistance);
    }

    protected void checkAndPerformAttack(double squaredDistance) {
        if (squaredDistance <= getAttackReachSqr() && ticksUntilNextAttack <= 0) {
            performAttack();
        }
    }
    
    protected void performAttack() {
        resetAttackCooldown();
        mob.swing(InteractionHand.MAIN_HAND);
        LumberMob lumberMob = Objects.requireNonNull(
            plugin.getGameManager().getWaveManager().getMob(mob.getUUID())); // Should never be null, but doesn't hurt to be safe
        plugin.getGameManager().getTreeManager().onMobDamage(lumberMob);
    }

    protected void resetAttackCooldown() {
        ticksUntilNextAttack = adjustedTickDelay(attackCooldown);
    }

    protected double getAttackReachSqr() {
        return (mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + TREE_BB_WIDTH);
    }
}