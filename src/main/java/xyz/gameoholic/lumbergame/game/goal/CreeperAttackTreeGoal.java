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
    private Creeper creeper = (Creeper) mob;
    private org.bukkit.entity.Creeper bukkitCreeper;

    public CreeperAttackTreeGoal(LumberGamePlugin plugin, org.bukkit.entity.Creeper bukkitCreeper, Creeper mob, double speed, Vec3 targetLoc) {
        super(plugin, mob, speed, targetLoc);
        this.bukkitCreeper = bukkitCreeper;
    }


    @Override
    protected void checkAndPerformAttack(double squaredDistance) {
        // If needs to cancel ignition
        if (squaredDistance > getAttackReachSqr() && creeper.isIgnited()) {
            creeper.setSwellDir(-1);
            creeper.swell = 0;
            creeper.setIgnited(false);
            Bukkit.broadcastMessage(bukkitCreeper.isIgnited() + "");
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
