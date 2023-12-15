package xyz.gameoholic.lumbergame.game.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftCreeper;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;

import java.util.Objects;

public class CreeperAttackTreeGoal extends AttackTreeGoal {
    private final Creeper creeper = (Creeper) mob;
    private final int swellSpeed;

    /**
     * @param plugin
     * @param mob
     * @param targetLoc
     * @param swellSpeed The speed of swelling. Must be positive. Set to 1.0 for vanilla MC behavior.
     */
    public CreeperAttackTreeGoal(LumberGamePlugin plugin, Creeper mob, int swellSpeed, Vec3 targetLoc) {
        super(plugin, mob, targetLoc);
        this.swellSpeed = swellSpeed;
    }


    @Override
    protected void checkAndPerformAttack(double squaredDistance) {
        // If needs to cancel ignition
        if (squaredDistance > getAttackReachSqr() && creeper.isIgnited()) {
            creeper.setSwellDir(-swellSpeed);
            creeper.swell = 0;
            creeper.setIgnited(false);
            resetAttackCooldown();
        }
        if (squaredDistance <= getAttackReachSqr() && ticksUntilNextAttack <= 0) {
            performAttack();
        }
    }

    @Override
    protected void performAttack() {
        creeper.setIgnited(true);
        creeper.setSwellDir(swellSpeed);

        ticksUntilNextAttack = Integer.MAX_VALUE;
    }
}
