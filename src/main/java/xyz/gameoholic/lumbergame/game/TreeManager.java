package xyz.gameoholic.lumbergame.game;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;
import xyz.gameoholic.lumbergame.util.ItemUtil;
import xyz.gameoholic.lumbergame.util.NMSUtil;

import java.util.HashMap;
import java.util.Map;

public class TreeManager {
    private final LumberGamePlugin plugin;
    private int health;
    private int maxHealth;
    private int level = 1;
    private boolean treeDead = false;

    public TreeManager(LumberGamePlugin plugin) {
        this.plugin = plugin;

        updateMaxHealth();
        health = maxHealth;
        onAnyHealthChanged();
    }

    /**
     * Called when a mob attempts to damage the tree.
     */
    public void onMobDamage(Mob mob) {
        int damage = (int) mob.getMob().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        Bukkit.broadcastMessage("Tree damaged by " + mob.getMobType().displayName() + " for " + damage + " HP");
        health = Math.max(health - damage, 0);
        onAnyHealthChanged();
        Bukkit.broadcastMessage("Tree at " + health + "/" + maxHealth + " health.");
        if (health == 0 && !treeDead) {
            onTreeDeath();
        }
    }

    private void onTreeDeath() {
        treeDead = true;
        plugin.getGameManager().onGameEnd();
    }

    /**
     * Ran when a player chops the tree.
     *
     * @param player   The player who chopped it.
     * @param location Location of the specific block broken.
     */
    public void onTreeChopByPlayer(Player player, Location location) {
        int newHealth = (int) Math.max(health - Math.ceil(maxHealth * 0.05), 1);
        int healthChopped = health - newHealth;
        if (healthChopped == 0)
            return;

        health = newHealth;
        onAnyHealthChanged();

        for (int i = 0; i < healthChopped; i++) {
            location.getWorld().dropItemNaturally(location, ItemUtil.getWoodItemStack(plugin));
        }
    }

    public void onTreeHealByPlayer(Player player) {
        health = (int) Math.min(health + Math.ceil(maxHealth * 0.05), maxHealth);
        onAnyHealthChanged();
    }

    public void onTreeLevelUpByPlayer(Player player) {
        level++;
        updateMaxHealth();
    }

    /**
     * Updates the tree's max health according to the level.
     */
    private void updateMaxHealth() {
        maxHealth = (int) ExpressionUtil.evaluateExpression(
            plugin.getLumberConfig().gameConfig().treeHealthExpression(),
            Map.of("level", (double) level));
        onAnyHealthChanged();
    }

    /**
     * Should be called whenever the health, or the maxHealth of the tree changes (or updates but remains the same).
     * Displays clientside cracks on tree.
     */
    private void onAnyHealthChanged() {
        int blockBreakProgress = -1; // -1 is no block break, range is 0 - 9, where 9 is the most broken.

        int healthRatio = (int) (((double) health / maxHealth) * 100); // in %
        if (health == 1)
            blockBreakProgress = 9;
        else if (healthRatio <= 5)
            blockBreakProgress = 8;
        else if (healthRatio <= 10)
            blockBreakProgress = 7;
        else if (healthRatio <= 20)
            blockBreakProgress = 6;
        else if (healthRatio <= 30)
            blockBreakProgress = 5;
        else if (healthRatio <= 40)
            blockBreakProgress = 4;
        else if (healthRatio <= 50)
            blockBreakProgress = 3;
        else if (healthRatio <= 65)
            blockBreakProgress = 2;
        else if (healthRatio <= 80)
            blockBreakProgress = 1;
        else if (healthRatio <= 90)
            blockBreakProgress = 0;

        int searchRadius = (int) Math.sqrt(plugin.getLumberConfig().mapConfig().treeRadius()); // Radius provided in config is squared
        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation(); // Root location of tree

        // Get all tree blocks in X,Y,Z radius of tree
        for (int x = -searchRadius; x < searchRadius; x++) {
            for (int y = -searchRadius; y < searchRadius; y++) {
                for (int z = -searchRadius; z < searchRadius; z++) {
                    Location blockLocation = new Location(
                        treeLocation.getWorld(),
                        treeLocation.getBlockX() + x,
                        treeLocation.getBlockY() + y,
                        treeLocation.getBlockZ() + z
                    );
                    // If block is a tree block, send destruction packet
                    if (plugin.getLumberConfig().mapConfig().treeBlockTypes().contains(blockLocation.getBlock().getType())) {
                        NMSUtil.displayBlockDestruction(
                            blockLocation.getBlockX(),
                            blockLocation.getBlockY(),
                            blockLocation.getBlockZ(),
                            blockBreakProgress
                        );
                    }
                }
            }
        }


    }
}
