package xyz.gameoholic.lumbergame.game.goal.hostile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.Random;

public class SkeletonTNTAttackGoal<T extends Monster & RangedAttackMob> extends RangedBowAttackGoal {
    private final T mob;
    private final float attackRadiusSqr;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    private final int attackCooldown; // Lumber - attack cooldown in ticks
    private int ticksUntilNextAttack; // Lumber - ticks until next attack
    private boolean usingTNT = false; // Lumber - usingItem() equivalent
    /**
     * Adds this value to the velocity's Y value (before last multiplication)
     */
    private static final double TNT_Y_VELOCITY_ADDITION = 0.3;
    /**
     * Multiplies the TNT velocity by random value MIN to MAX
     */
    private static final double TNT_VELOCITY_MULTIPLIER_MIN = 0.9;
    private static final double TNT_VELOCITY_MULTIPLIER_MAX = 1.1;
    private static final Random RND = new Random();
    private final double tntDamage; // Lumber - tnt damage
    private final LumberGamePlugin plugin;
    private static final float RANGE = 15f; // Lumber - attack range

    public SkeletonTNTAttackGoal(LumberGamePlugin plugin, T actor, int attackCooldown, double tntDamage) {
        super(actor, 1.0, attackCooldown, RANGE);
        this.plugin = plugin;
        this.mob = actor;
        this.attackRadiusSqr = RANGE * RANGE;
        this.attackCooldown = attackCooldown;
        this.ticksUntilNextAttack = attackCooldown;
        this.tntDamage = tntDamage;
    }


    @Override
    protected boolean isHoldingBow() {
        // Lumber - use tnt instead of bow for check
        return this.mob.isHolding(Items.TNT);
    }


    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            double d = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean bl = this.mob.getSensing().hasLineOfSight(livingEntity);
            boolean bl2 = this.seeTime > 0;
            if (bl != bl2) {
                this.seeTime = 0;
            }

            if (bl) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(livingEntity, 1.0);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d > (double) (this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d < (double) (this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                Entity var7 = this.mob.getControlledVehicle();
                if (var7 instanceof Mob) {
                    Mob mob = (Mob) var7;
                    mob.lookAt(livingEntity, 30.0F, 30.0F);
                }

                this.mob.lookAt(livingEntity, 30.0F, 30.0F);
            } else {
                this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            }



            // Lumber: Use custom attack cooldown and attack predicate (usingTNT)
            if (usingTNT) {
                if (!bl && this.seeTime < -60) {
                    usingTNT = false;
                } else if (bl) {
                    if (ticksUntilNextAttack == 0) {
                        // Lumber - launch tnt
                        Location mobLocation = new Location(mob.level().getWorld(), mob.getX(), mob.getY(), mob.getZ());
                        Location targetLocation = new Location(
                            livingEntity.level().getWorld(),
                            livingEntity.getX(),
                            livingEntity.getY(),
                            livingEntity.getZ()
                        );
                        TNTPrimed tnt = (TNTPrimed) mobLocation.getWorld().spawnEntity(mobLocation, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(40); // max fuse ticks is 80 - will detonate after 40 ticks (2 seconds)
                        tnt.getPersistentDataContainer().set(new NamespacedKey(plugin, "tnt_damage"),
                            PersistentDataType.DOUBLE, tntDamage); // Set custom tnt damage
                        Vector velocity = targetLocation.clone().subtract(mobLocation).toVector().normalize();

                        // Adjust TNT velocity based on distance
                        if (d >= 100) //d = squared distance to target
                            velocity = velocity.multiply(1.0);
                        else if (d >= 64)
                            velocity = velocity.multiply(0.75);
                        else if (d >= 25)
                            velocity = velocity.multiply(0.5);
                        else if (d >= 10)
                            velocity = velocity.multiply(0.25);
                        else
                            velocity = velocity.multiply(0.1);

                        velocity.setY(velocity.getY() + TNT_Y_VELOCITY_ADDITION); // Add Y velocity offset
                        velocity = velocity.multiply(RND.nextDouble(TNT_VELOCITY_MULTIPLIER_MIN, TNT_VELOCITY_MULTIPLIER_MAX)); // Randomize TNT velocity

                        tnt.setVelocity(velocity);
                        ticksUntilNextAttack = attackCooldown;
                        usingTNT = false;
                    }
                }
            } else if (--this.ticksUntilNextAttack <= 0 && this.seeTime >= -60) {
                usingTNT = true;
            }

        }
    }
}
