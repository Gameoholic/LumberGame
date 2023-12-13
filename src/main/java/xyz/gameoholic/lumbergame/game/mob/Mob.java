package xyz.gameoholic.lumbergame.game.mob;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;

public class Mob {
    private MobType mobType;
    private int CR; // Challenge Rating
    private org.bukkit.entity.Mob mob;
    public Mob(MobType mobType, int CR, Location location) {
        this.mobType = mobType;
        this.CR = CR;

        mob = (org.bukkit.entity.Mob) location.getWorld().spawnEntity(location, mobType.entityType());
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
            new ExpressionBuilder(mobType.healthExpression())
                .variables("CR")
                .build()
                .setVariable("CR", CR).evaluate()
        );


    }
}
