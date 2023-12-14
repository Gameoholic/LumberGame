package xyz.gameoholic.lumbergame.game.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.EnumSet;

public class AttackTreeGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private final double TREE_BB_WIDTH = 2.0;
    private Vec3 targetLoc;

    public AttackTreeGoal(PathfinderMob mob, double speed, Vec3 targetLoc) {
        this.targetLoc = targetLoc;
        this.mob = mob;
        speedModifier = speed;
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long l = mob.level().getGameTime();
        if (l - lastCanUseCheck < 20L) {
            return false;
        } else {
            lastCanUseCheck = l;
            mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, speedModifier);
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void start() {
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, speedModifier);
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
        mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, speedModifier);

        checkAndPerformAttack(squaredDistance);
    }

    protected void checkAndPerformAttack(double squaredDistance) {
        if (squaredDistance <= getAttackReachSqr() && ticksUntilNextAttack <= 0) {
            resetAttackCooldown();
            mob.swing(InteractionHand.MAIN_HAND);
        }
    }

    protected void resetAttackCooldown() {
        ticksUntilNextAttack = adjustedTickDelay(20);
    }

    protected double getAttackReachSqr() {
        return (mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + TREE_BB_WIDTH);
    }
}