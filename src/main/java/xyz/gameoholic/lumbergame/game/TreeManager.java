package xyz.gameoholic.lumbergame.game;

import net.objecthunter.exp4j.ExpressionBuilder;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;

public class TreeManager {
    private LumberGamePlugin plugin;
    int health;
    int maxHealth;
    int level = 1;

    public TreeManager(LumberGamePlugin plugin) {
        this.plugin = plugin;

        maxHealth = (int) new ExpressionBuilder(plugin.getLumberConfig().gameConfig().treeHealthExpression())
            .variables("level")
            .build()
            .setVariable("level", level).evaluate();
        health = maxHealth;
    }

    public void onMobDamage(Mob mob) {

    }
}
