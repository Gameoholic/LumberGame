package xyz.gameoholic.lumbergame.game.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;

import java.util.EnumSet;

public class AttackTreeGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private Path path;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private final double TREE_BB_WIDTH = 2.0;
    private Vec3 targetLoc;

    public AttackTreeGoal(PathfinderMob mob, double speed, Vec3 targetLoc) {
        this.targetLoc = targetLoc;
        this.mob = mob;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long l = this.mob.level().getGameTime();
        if (l - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = l;
            this.path = this.mob.getNavigation().createPath(-90, 79, 334, 0);
            if (this.path != null) {
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        ticksUntilNextAttack = Math.max(ticksUntilNextAttack - 1, 0);
        ticksUntilNextPathRecalculation = 4 + mob.getRandom().nextInt(7);

        if (!this.mob.getNavigation().moveTo(targetLoc.x, targetLoc.y, targetLoc.z, speedModifier)) {
            this.ticksUntilNextPathRecalculation += 15;
        }
        ticksUntilNextPathRecalculation = adjustedTickDelay(this.ticksUntilNextPathRecalculation);

        double squaredDistance = mob.distanceToSqr(targetLoc);
        checkAndPerformAttack(squaredDistance);
    }

    protected void checkAndPerformAttack(double squaredDistance) {
        Bukkit.broadcastMessage("Distance: " + squaredDistance + ", required attack reach:" + getAttackReachSqr());
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