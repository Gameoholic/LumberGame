package xyz.gameoholic.lumbergame.game.player;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.npc.ShopNPC;

import javax.annotation.Nullable;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

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
     * Task that sends out block destruction packets for the tree every 15 seconds, otherwise they disappear.
     */
    private BukkitTask treeDestructionTask;

    /**
     * Is null when player is logged off.
     */
    private @Nullable PlayerScoreboardManager scoreboardManager;
    private final LumberGamePlugin plugin;

    public LumberPlayer(LumberGamePlugin plugin, UUID uuid) {
        this.uuid = uuid;
        this.plugin = plugin;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Unregisters all events and cancels tasks.
     */
    public void unregisterEvents() {
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
        FoodLevelChangeEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);

        treeDestructionTask.cancel();
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
        player.getInventory().addItem(plugin.getItemManager().getStoneAxeItem());
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.teleport(plugin.getLumberConfig().mapConfig().playerSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2000000000, 1, false, false));
        plugin.getPlayerNPCManager().addNPC(player, // Spawn shop NPC
            new ShopNPC(plugin, player.getUniqueId(), plugin.getLumberConfig().mapConfig().shopNPCLocation()));

        registerEvents();

        // Sends out block destruction packets for the tree every 15 seconds, otherwise they disappear after 20 seconds
        treeDestructionTask = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().getTreeManager().displayTreeDestruction();
            }
        }.runTaskTimer(plugin, 0L, 300L); // Every 15 seconds
    }


    /**
     * Runs when the player's died.
     *
     * @param player The Bukkit Player instance of the player.
     */
    private void onDeath(Player player) {
        // Remove half of all player's resources
        plugin.getItemManager().removeItemsFromInventory(player, "IRON", iron / 2);
        plugin.getItemManager().removeItemsFromInventory(player, "GOLD", gold / 2);
        plugin.getItemManager().removeItemsFromInventory(player, "WOOD", wood / 2);
        plugin.getItemManager().removeItemsFromInventory(player, "BONE_MEAL", boneMeal / 2);

        plugin.getGameManager().getPlayers().forEach(lumberPlayer -> lumberPlayer.sendMessage(MiniMessage.miniMessage()
            .deserialize(
                plugin.getLumberConfig().strings().playerDeathMessage(),
                Placeholder.component("player", player.name()),
                Placeholder.component("iron", text(iron / 2)),
                Placeholder.component("gold_amount", text(gold / 2)),
                Placeholder.component("wood", text(wood / 2)),
                Placeholder.component("bone_meal", text(boneMeal / 2))
            )
        ));
        onInventoryChanged(player.getInventory()); // todo: remove this when rework resource information
        plugin.getGameManager().updatePlayerScoreboards(); // Update items
    }

    public void updateScoreboard() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && scoreboardManager != null)
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

    /**
     * Plays a sound for the player that follows it.
     */
    public void playSound(Sound sound) {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            player.playSound(sound, Sound.Emitter.self());
    }

    /**
     * Sends a message to the player.
     */
    public void sendMessage(Component message) {
        @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            player.sendMessage(message);
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        // Resend tree destruction packet that player did not get while offline
        scoreboardManager = new PlayerScoreboardManager(plugin, e.getPlayer(), this);
        plugin.getGameManager().getTreeManager().displayTreeDestruction(e.getPlayer());
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        scoreboardManager.delete();
        scoreboardManager = null;
    }

    @EventHandler
    private void onInventoryDragEvent(InventoryDragEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged(player.getInventory());
    }

    @EventHandler
    private void onInventoryEvent(InventoryCreativeEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged(player.getInventory());
    }

    @EventHandler
    private void onInventoryEvent(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged(player.getInventory());
    }

    @EventHandler
    private void onInventoryEvent(PlayerDropItemEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        onInventoryChanged(e.getPlayer().getInventory());
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;

        if (e instanceof EntityDamageByEntityEvent byEntityEvent) {
            // Creeper explosion damage should match the explosion's damage attribute, otherwise vanilla explosion damage is applied
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof Creeper) {
                e.setDamage(((Creeper) byEntityEvent.getDamager()).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
            }
            // Arrow damage should match the skeleton's damage attribute, otherwise vanilla arrow damage is applied
            if (byEntityEvent.getDamager() instanceof Arrow) {
                @Nullable Double arrowDamage = byEntityEvent.getDamager().getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "arrow_damage"), PersistentDataType.DOUBLE);
                if (arrowDamage == null) // If wasn't shot by LumberMob
                    return;
                e.setDamage(arrowDamage);
            }
        }

        // Health change is delayed by 1 tick to let the event affect the player's health when accessed by scoreboard manager
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().updatePlayerScoreboards(); // Update player health
            }
        }.runTask(plugin);
    }

    @EventHandler
    private void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player) || !player.getUniqueId().equals(uuid))
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
    private void onEntityRegainHealthEvent(PlayerDeathEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        onDeath(e.getPlayer());
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
                            case BONE_MEAL -> boneMeal += itemStack.getAmount();
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
        if (!e.getPlayer().getUniqueId().equals(uuid))
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
        if (!e.getPlayer().getUniqueId().equals(uuid))
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
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
        // We don't want hunger for our game
        if (!e.getEntity().getUniqueId().equals(uuid))
            return;
        e.setCancelled(true);
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
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
