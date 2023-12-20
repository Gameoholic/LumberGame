package xyz.gameoholic.lumbergame.game.player;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a player in the Lumber game.
 * Can be offline.
 * Handles displaying scoreboard when logging on/off, updating scoreboards, etc.
 */
public class LumberPlayer implements Listener {
    private final UUID uuid;
    private int wood = 0;
    private int iron = 0;
    private int gold = 0;
    private int boneMeal = 0;

    /**
     * Is null when player is logged off.
     */
    private @Nullable PlayerScoreboardManager scoreboardManager;
    private final LumberGamePlugin plugin;

    public LumberPlayer(LumberGamePlugin plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;
        registerEvents();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    private void unregisterEvents() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryCreativeEvent.getHandlerList().unregister(this);
        EntityPickupItemEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
        EntityRegainHealthEvent.getHandlerList().unregister(this);
        BlockFertilizeEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }
    /**
     * Called after the game fully loads and the first wave starts.
     */
    public void onGameLoad() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;
        scoreboardManager = new PlayerScoreboardManager(plugin, player, this);
        player.getInventory().clear();
        player.getInventory().addItem(plugin.getItemManager().getWoodenSwordItem());
        player.getInventory().addItem(plugin.getItemManager().getWoodenAxeItem());
    }


    public void updateScoreboard() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            scoreboardManager.update(player);
    }

    /**
     * Displays the component on the player's action bar.
     */
    public void displayActionBar(Component component) {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            player.sendActionBar(component);
    }

    /**
     * Plays a sounds at a location for the player.
     */
    public void playSound(Sound sound, Location location) {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            player.playSound(sound, location.x(), location.y(), location.z());
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        scoreboardManager = new PlayerScoreboardManager(plugin, e.getPlayer(), this);
    }
    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        scoreboardManager.delete();
        scoreboardManager = null;
    }
    @EventHandler
    private void onInventoryDragEvent(InventoryDragEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || player.getUniqueId() != uuid)
            return;
        onInventoryChanged(player.getInventory());
    }
    @EventHandler
    private void onInventoryEvent(InventoryCreativeEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || player.getUniqueId() != uuid)
            return;
        onInventoryChanged(player.getInventory());
    }
    @EventHandler
    private void onInventoryEvent(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player) || player.getUniqueId() != uuid)
            return;
        onInventoryChanged(player.getInventory());
    }
    @EventHandler
    private void onInventoryEvent(PlayerDropItemEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        onInventoryChanged(e.getPlayer().getInventory());
    }
    @EventHandler
    private void onHealthChanged(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player) || player.getUniqueId() != uuid)
            return;
        // Health change is delayed by 1 tick to let the event affect the player's health when accessed by scoreboard manager
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().updatePlayerScoreboards(); // Update player health
            }
        }.runTask(plugin);
    }

    @EventHandler
    private void onHealthChanged(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player) || player.getUniqueId() != uuid)
            return;
        // Health change is delayed by 1 tick to let the event affect the player's health when accessed by scoreboard manager
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().updatePlayerScoreboards(); // Update player health
            }
        }.runTask(plugin);
    }

    private void onInventoryChanged(Inventory inventory) {
        // Inventory search is delayed by 1 tick to let the events affect the player's inventory when accessed by scoreboard manager
        new BukkitRunnable() {
            @Override
            public void run() {
                boneMeal = 0;
                wood = 0;
                gold = 0;
                iron = 0;
                inventory.forEach(itemStack -> {
                    if (itemStack != null)
                        switch (itemStack.getType()) {
                            case BONE_MEAL ->  boneMeal += itemStack.getAmount();
                            case OAK_WOOD -> wood += itemStack.getAmount();
                            case GOLD_INGOT -> gold += itemStack.getAmount();
                            case IRON_INGOT -> iron += itemStack.getAmount();
                        }
                });
                plugin.getGameManager().updatePlayerScoreboards(); // Update player item amounts
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onBlockFertilizeEvent(BlockFertilizeEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;
        // Check whether the bone meal that was used is the Lumber bone meal
        boolean hasItem = plugin.getItemManager().removeItemsFromInventory(e.getPlayer(), "BONE_MEAL", 1);
        if (!hasItem)
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeHealByPlayer(e.getPlayer());
    }
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;
        // Check whether the bone meal that was used is the Lumber bone meal
        boolean hasItem = plugin.getItemManager().removeItemsFromInventory(e.getPlayer(), "BONE_BLOCK", 1);
        if (!hasItem)
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeLevelUpByPlayer(e.getPlayer());
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (e.getPlayer().getUniqueId() != uuid)
            return;
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;
        // Must be one of the tree block types
        if (!(plugin.getLumberConfig().mapConfig().treeBlockTypes().contains(e.getBlock().getType())))
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeChopByPlayer(e.getPlayer(), e.getBlock().getLocation());
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWood() {
        return wood;
    }

    public int getIron() {
        return iron;
    }

    public int getGold() {
        return gold;
    }

    public int getBoneMeal() {
        return boneMeal;
    }



}
