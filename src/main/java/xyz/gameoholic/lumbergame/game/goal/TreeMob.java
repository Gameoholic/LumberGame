package xyz.gameoholic.lumbergame.game.goal;


import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TreeMob extends Zombie {

    public TreeMob(Level world) {
        super(world);
//        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal(this, Pig.class, true));
        this.setPos(-95, 80, 344);

        this.goalSelector.removeAllGoals(goal -> true);


        this.goalSelector.addGoal(8, new AttackTreeGoal(this, 1.0D, new Vec3(-91.5, 79, 329.5)));


    }




}
