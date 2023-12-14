package xyz.gameoholic.lumbergame.game.temp;


import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xyz.gameoholic.lumbergame.game.goal.AttackTreeGoal;

public class TreeMobTest extends Zombie {

    public TreeMobTest(Level world) {
        super(world);
//        this.goalSelector.addGoal(0, new NearestAttackableTargetGoal(this, Pig.class, true));
        this.setPos(-95, 80, 344);

        this.goalSelector.removeAllGoals(goal -> true);
        this.goalSelector.addGoal(8, new AttackTreeGoal(this, 1.0D, new Vec3(-91.5, 79, 329.5)));

        //this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));


    }




}
