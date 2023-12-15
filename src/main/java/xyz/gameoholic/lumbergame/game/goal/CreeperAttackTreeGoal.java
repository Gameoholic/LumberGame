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
    /**
     * @param plugin
     * @param mob
     * @param targetLoc Location of the tree to pathfind to and ignite at.
     */
    public CreeperAttackTreeGoal(LumberGamePlugin plugin, Creeper mob, Vec3 targetLoc) {
        super(plugin, mob, targetLoc);
    }


    @Override
    protected void checkAndPerformAttack(double squaredDistance) {
        // If needs to cancel ignition
        if (squaredDistance > getAttackReachSqr() && creeper.isIgnited()) {
            creeper.setSwellDir(-1);
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

        ticksUntilNextAttack = Integer.MAX_VALUE;
    }
}
