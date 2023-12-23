package xyz.gameoholic.lumbergame.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.LumberMob;
import xyz.gameoholic.lumbergame.util.*;

import java.io.File;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;

public class TreeManager {
    private final LumberGamePlugin plugin;
    private int health;
    private int maxHealth;
    private int level = 1;
    private boolean treeDead = false;
    /**
     * The destruction progress of the tree's blocks. Range is 0 - 9, where 9 is the most destroyed. Any other number will reset it back to no destruction.
     */
    private int blockBreakProgress = -1;

    public TreeManager(LumberGamePlugin plugin) {
        this.plugin = plugin;

        updateMaxHealth();
        health = maxHealth;

        pasteTreeSchematic();
    }

    /**
     * Called when a mob attempts to damage the tree.
     */
    public void onMobDamage(LumberMob mob) {
        if (treeDead)
            return;

        int previousHealthRatio = getHealthToMaxHealthRatio();
        int damage = (int) mob.getMob().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
        health = Math.max(health - damage, 0);
        onAnyHealthChanged();
        int currentHealthRatio = getHealthToMaxHealthRatio();

        // For certain tree health thresholds (5%, 10%, 20%, 50%) send message to players
        boolean treeHealthThresholdPassed = (
            (previousHealthRatio > 5 && currentHealthRatio <= 5)
            || (previousHealthRatio > 10 && currentHealthRatio <= 10)
            || (previousHealthRatio > 20 && currentHealthRatio <= 20)
            || (previousHealthRatio > 50 && currentHealthRatio <= 50)
        );

        plugin.getGameManager().getPlayers().forEach(lumberPlayer -> {
            lumberPlayer.playSound(
                plugin.getLumberConfig().soundsConfig().treeDamagedSound(),
                plugin.getLumberConfig().mapConfig().treeLocation()
            );
            lumberPlayer.displayActionBar(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().treeDamagedActionbarMessage(),
                Placeholder.component("tree_health", text(health)),
                Placeholder.component("tree_max_health", text(maxHealth)),
                Placeholder.component("tree_health_percentage", text(getHealthToMaxHealthRatio())),
                Placeholder.component("tree_damage", text(damage)),
                Placeholder.parsed("tree_health_fraction", String.valueOf(getHealthToMaxHealthRatio() / 100.0))
            ));
            if (treeHealthThresholdPassed) {
                lumberPlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getLumberConfig().strings().treeHealthThresholdMessage(),
                    Placeholder.component("tree_health", text(health)),
                    Placeholder.component("tree_max_health", text(maxHealth)),
                    Placeholder.component("tree_health_percentage", text(currentHealthRatio)),
                    Placeholder.component("tree_damage", text(damage)),
                    Placeholder.parsed("tree_health_fraction", String.valueOf(currentHealthRatio / 100.0))
                ));
            }
        });


        if (health == 0)
            onTreeDeath();
    }

    private void onTreeDeath() {
        treeDead = true;

        plugin.getGameManager().getPlayers().forEach(lumberPlayer -> {
            lumberPlayer.playSound(
                plugin.getLumberConfig().soundsConfig().treeDiedSound()
            );
        });
        ParticleUtil.spawnTreeDeadParticle(plugin, plugin.getLumberConfig().mapConfig().treeLocation());
        iterateOverTreeBlocks(block -> block.setType(Material.AIR));

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
            location.getWorld().dropItemNaturally(location, plugin.getItemManager().getWoodItem());
        }
    }

    public void onTreeHealByPlayer(Player player) {
        health = (int) Math.min(health + Math.ceil(maxHealth * 0.1), maxHealth);
        onAnyHealthChanged();
    }

    public void onTreeLevelUpByPlayer(Player player) {
        level++;
        updateMaxHealth();
        onAnyHealthChanged();

        pasteTreeSchematic();
    }

    /**
     * Updates the tree's max health according to the level.
     */
    private void updateMaxHealth() {
        maxHealth = (int) ExpressionUtil.evaluateExpression(
            plugin.getLumberConfig().gameConfig().treeHealthExpression(),
            Map.of("level", (double) level));
    }

    /**
     * Should be called whenever the health, or the maxHealth of the tree changes (or updates but remains the same).
     * Displays clientside cracks on tree and updates scoreboard.
     * Should NOT be called when the health is first set.
     */
    private void onAnyHealthChanged() {
        int healthRatio = getHealthToMaxHealthRatio(); // in %

        // Update tree break progress
        if (health <= 1)
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
        else
            blockBreakProgress = -1;

        displayTreeDestruction();

        plugin.getGameManager().updatePlayerScoreboards(); // Update tree health
    }


    /**
     * Displays the tree's block destruction to all lumber players.
     */
    public void displayTreeDestruction() {
        iterateOverTreeBlocks(block -> NMSUtil.displayBlockDestruction(
            plugin,
            block.getLocation().getBlockX(),
            block.getLocation().getBlockY(),
            block.getLocation().getBlockZ(),
            blockBreakProgress
        ));
    }

    /**
     * Displays the tree's block destruction to a specific player.
     */
    public void displayTreeDestruction(Player player) {
        iterateOverTreeBlocks(block -> NMSUtil.displayBlockDestruction(
            player,
            block.getLocation().getBlockX(),
            block.getLocation().getBlockY(),
            block.getLocation().getBlockZ(),
            blockBreakProgress
        ));
    }

    /**
     * Iterates over every tree block in the radius of the tree.
     *
     * @param func The function to apply on the block.
     */
    private void iterateOverTreeBlocks(FunctionUtil.BlockFunction func) {
        int searchRadius = (int) Math.sqrt(plugin.getLumberConfig().mapConfig().treeRadius()); // Radius provided in config is squared
        Location treeLocation = plugin.getLumberConfig().mapConfig().treeLocation(); // Root location of tree
        // Get all tree blocks in X,Y,Z radius of tree
        for (int x = -searchRadius; x < searchRadius; x++) {
            for (int y = -searchRadius; y < searchRadius; y++) {
                for (int z = -searchRadius; z < searchRadius; z++) {
                    Block block = new Location(
                        treeLocation.getWorld(),
                        treeLocation.getBlockX() + x,
                        treeLocation.getBlockY() + y,
                        treeLocation.getBlockZ() + z
                    ).getBlock();
                    // If block is a tree block

                    if (plugin.getLumberConfig().mapConfig().treeBlockTypes().contains(block.getType())) {
                        func.apply(block);
                    }
                }
            }
        }
    }

    /**
     * @return The health/max health ratio in % (0-100)
     */
    public int getHealthToMaxHealthRatio() {
        return (int) (((double) health / maxHealth) * 100);
    }

    /**
     * Pastes the schematic of the tree correspondent to the current tree level.
     */
    private void pasteTreeSchematic() {
        if (!(plugin.getLumberConfig().mapConfig().treeLevelSchematicsProvided().contains(level)))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {

                WorldEditUtil.loadSchematic(
                    new File(plugin.getDataFolder(), "schematics/tree/level_" + level + ".schem"),
                    plugin.getLumberConfig().mapConfig().treeLocation()
                );

                // We update the tree's break progress for the new blocks we pasted
                iterateOverTreeBlocks(block -> NMSUtil.displayBlockDestruction(
                    plugin,
                    block.getLocation().getBlockX(),
                    block.getLocation().getBlockY(),
                    block.getLocation().getBlockZ(),
                    blockBreakProgress
                ));

            }
        }.runTaskAsynchronously(plugin);

    }


    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

}
