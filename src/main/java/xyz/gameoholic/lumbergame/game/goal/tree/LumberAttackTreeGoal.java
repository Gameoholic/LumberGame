package xyz.gameoholic.lumbergame.game.goal.tree;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.LumberMob;

import java.util.EnumSet;
import java.util.Objects;

public class LumberAttackTreeGoal extends Goal {
    private LumberGamePlugin plugin;
    protected final PathfinderMob mob;
    protected int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private final Vec3 targetLoc;
    private final int ATTACK_COOLDOWN; // LUMBER - custom attack cooldown

    private static final double SPEED_MODIFIER = 1.0;
    private static final double TREE_BB_WIDTH = 2.0;

    /**
     * @param plugin
     * @param mob
     * @param targetLoc The location of the tree to pathfind to and attack.
     */

    public LumberAttackTreeGoal(LumberGamePlugin plugin, PathfinderMob mob, Vec3 targetLoc, int attackCooldown) {
        this.plugin = plugin;
        this.targetLoc = targetLoc;
        this.mob = mob;
        this.ATTACK_COOLDOWN = attackCooldown;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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
            mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, SPEED_MODIFIER);
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
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, SPEED_MODIFIER);
        ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        ticksUntilNextAttack = Math.max(ticksUntilNextAttack - 1, 0);

        double squaredDistance = mob.distanceToSqr(targetLoc);
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, SPEED_MODIFIER);

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
        ticksUntilNextAttack = adjustedTickDelay(ATTACK_COOLDOWN);
    }

    protected double getAttackReachSqr() {
        return (mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + TREE_BB_WIDTH);
    }
}