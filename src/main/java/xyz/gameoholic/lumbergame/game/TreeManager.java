package xyz.gameoholic.lumbergame.game;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
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

    /**
     * Called when a mob attempts to damage the tree.
     */
    public void onMobDamage(Mob mob) {
        int damage = (int) mob.getMob().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        Bukkit.broadcastMessage("Tree damaged by " + mob.getMobType().displayName() + " for " + damage + " HP");
        health = Math.max(health - damage, 0);
        Bukkit.broadcastMessage("Tree at " + health + "/" + maxHealth + " health.");
        if (health < 0) {
            onTreeDeath();
        }
    }

    private void onTreeDeath() {
        plugin.getGameManager().onGameEnd();
    }
}
