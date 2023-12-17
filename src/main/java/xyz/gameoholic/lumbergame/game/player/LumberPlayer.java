package xyz.gameoholic.lumbergame.game.player;

import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
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

    /**
     * Is null when player is logged off.
     */
    private @Nullable PlayerScoreboardManager scoreboardManager;
    private final LumberGamePlugin plugin;

    public LumberPlayer(LumberGamePlugin plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;
    }

    /**
     * Called after the game fully loads and the first wave starts.
     */
    public void onGameLoad() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            scoreboardManager = new PlayerScoreboardManager(plugin, player, this);
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        scoreboardManager = new PlayerScoreboardManager(plugin, e.getPlayer(), this);
    }
    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
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
                wood = 0;
                gold = 0;
                iron = 0;
                inventory.forEach(itemStack -> {
                    if (itemStack != null)
                        switch (itemStack.getType()) {
                            case OAK_WOOD -> wood += itemStack.getAmount();
                            case GOLD_INGOT -> gold += itemStack.getAmount();
                            case IRON_INGOT -> iron += itemStack.getAmount();
                        }
                });
                plugin.getGameManager().updatePlayerScoreboards(); // Update player item amounts
            }
        }.runTask(plugin);
    }

    public void updateScoreboard() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            scoreboardManager.update(player);
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

}
