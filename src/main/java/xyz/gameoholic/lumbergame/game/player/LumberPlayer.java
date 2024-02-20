package xyz.gameoholic.lumbergame.game.player;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.item.firestaff.FireStaffItem;
import xyz.gameoholic.lumbergame.game.item.SpecialItem;
import xyz.gameoholic.lumbergame.game.player.npc.ShopNPC;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;
import xyz.gameoholic.lumbergame.util.ItemUtil;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static xyz.gameoholic.lumbergame.util.OtherUtil.equalsNotNull;

/**
 * Represents a player in the Lumber game.
 * Can be offline.
 * Handles displaying scoreboard when logging on/off, updating scoreboards, etc.
 */
public class LumberPlayer implements Listener {
    private final LumberGamePlugin plugin;
    private final UUID uuid;
    private List<Perk> perks = new ArrayList<>();
    /**
     * Task that sends out block destruction packets for the tree every 15 seconds, otherwise they disappear.
     */
    private BukkitTask treeDestructionTask;

    /**
     * Is null when player is logged off.
     */
    private @Nullable PlayerScoreboardManager scoreboardManager;
    /**
     * Currently item currently held by the player in their main hand.
     */
    private @Nullable SpecialItem specialItem;

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
    public void destroy() {
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
        PlayerRespawnEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        PlayerItemHeldEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);

        if (specialItem != null)
            specialItem.destroy(); // Disable special item
        specialItem = null; // Doesn't hurt to set to null, even though technically we don't have to as everything else is unregistered at this point
        perks.forEach(perk -> perk.onGameEnd());
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
        player.clearActivePotionEffects();
        player.getInventory().addItem(ItemUtil.getWoodenSwordItem(plugin));
        player.getInventory().addItem(ItemUtil.getStoneAxeItem(plugin));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.teleport(plugin.getLumberConfig().mapConfig().playerSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
        plugin.getPlayerNPCManager().addNPC(player, // Spawn shop NPC
            new ShopNPC(plugin, player.getUniqueId(), plugin.getLumberConfig().mapConfig().shopNPCLocation(),
                MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().shopNPCDisplayname())));

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
        ItemUtil.removeItemsFromInventory(plugin, player, Map.of("IRON", ItemUtil.countItemsInInventory(plugin, player, "IRON") / 2));
        ItemUtil.removeItemsFromInventory(plugin, player, Map.of("GOLD", ItemUtil.countItemsInInventory(plugin, player, "GOLD") / 2));
        ItemUtil.removeItemsFromInventory(plugin, player, Map.of("WOOD", ItemUtil.countItemsInInventory(plugin, player, "WOOD") / 2));
        ItemUtil.removeItemsFromInventory(plugin, player, Map.of("BONE_MEAL", ItemUtil.countItemsInInventory(plugin, player, "BONE_MEAL") / 2));

        plugin.getGameManager().getPlayers().forEach(lumberPlayer -> lumberPlayer.sendMessage(MiniMessage.miniMessage()
            .deserialize(
                plugin.getLumberConfig().strings().playerDeathMessage(),
                Placeholder.component("player", player.name()),
                Placeholder.component("iron", text(ItemUtil.countItemsInInventory(plugin, player, "IRON") / 2)),
                Placeholder.component("gold_amount", text(ItemUtil.countItemsInInventory(plugin, player, "GOLD") / 2)),
                Placeholder.component("wood", text(ItemUtil.countItemsInInventory(plugin, player, "WOOD") / 2)),
                Placeholder.component("bone_meal", text(ItemUtil.countItemsInInventory(plugin, player, "BONE_MEAL") / 2))
            )
        ));
        plugin.getGameManager().updatePlayerScoreboards(); // Update items
        plugin.getPlayerDataManager().getCachedPlayerData(uuid).incDeaths(1);
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

        plugin.getGameManager().updatePlayerScoreboards(); // Update the fact that the player is online
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        scoreboardManager.delete();
        scoreboardManager = null;
        new BukkitRunnable() { // Delay by 1 tick so player fully quits by the time the scoreboard's updated
            @Override
            public void run() {
                plugin.getGameManager().updatePlayerScoreboards(); // Update the fact that the player is offline
            }
        }.runTask(plugin);
        specialItem.destroy();
        specialItem = null;
    }

    @EventHandler
    private void onPlayerItemHeldEventEvent(PlayerItemHeldEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        onAnyInventoryChanged(e.getPlayer());
    }
    @EventHandler
    private void onInventoryClickEvent(InventoryClickEvent e) {
        if (!e.getWhoClicked().getUniqueId().equals(uuid))
            return;
        onAnyInventoryChanged((Player) e.getWhoClicked());
    }

    @EventHandler
    private void onInventoryDragEvent(InventoryDragEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged((Player) e.getWhoClicked());
    }

    @EventHandler
    private void onInventoryEvent(InventoryCreativeEvent e) {
        if (!(e.getViewers().get(0) instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged((Player) e.getWhoClicked());
    }

    @EventHandler
    private void onInventoryEvent(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;
        onInventoryChanged(player);
    }

    @EventHandler
    private void onInventoryEvent(PlayerDropItemEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        onInventoryChanged(e.getPlayer());
    }

    @EventHandler
    private void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player) || !player.getUniqueId().equals(uuid))
            return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            // Disable fall damage
            e.setCancelled(true);
            return;
        }
        if (e instanceof EntityDamageByEntityEvent byEntityEvent) {
            // Cancel friendly fire
            if (byEntityEvent.getDamager() instanceof Player) {
                e.setCancelled(true);
                return;
            }
            // Creeper explosion damage should match the explosion's damage attribute, otherwise vanilla explosion damage is applied
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof Creeper creeper) {
                e.setDamage((e.getDamage() / 43.0) * creeper.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()); // After testing, 43.0 is max damage of a default creeper. We let it do all the calculating for us, and get the % of the maxdamage done, then multiply it by the damage we want.
            }
            // TNT explosion damage should match the mob's damage attribute, otherwise vanilla explosion damage is applied
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && byEntityEvent.getDamager() instanceof TNTPrimed tnt) {
                @Nullable Double tntDamage = tnt.getPersistentDataContainer().get(
                    new NamespacedKey(plugin, "tnt_damage"), PersistentDataType.DOUBLE);
                if (tntDamage == null) // If wasn't launched by LumberMob
                    return;


                e.setDamage((e.getDamage() / 56.0) * tntDamage); // After testing, 56.0 is max damage of default TNT. We let it do all the calculating for us, and get the % of the maxdamage done, then multiply it by the damage we want.
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
    private void onEntityDeathEvent(EntityRegainHealthEvent e) {
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
    private void onEntityDeathEvent(PlayerDeathEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        onDeath(e.getPlayer());
    }

    @EventHandler
    private void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        e.getPlayer().setGameMode(GameMode.SPECTATOR);

        new BukkitRunnable() {
            int secondsPassed = 0;

            @Override
            public void run() {
                // Respawning in... message
                e.getPlayer().sendTitlePart(TitlePart.TITLE,
                    MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().respawnCooldownMessage(),
                        Placeholder.component("seconds", text(plugin.getLumberConfig().gameConfig().respawnCooldown() - secondsPassed))
                    )
                );
                e.getPlayer().sendTitlePart(TitlePart.TIMES, Title.Times.times(
                    Duration.ofMillis(0),
                    Duration.ofMillis(2000),
                    Duration.ofMillis(50)
                ));

                // Respawn mechanic
                if (secondsPassed >= plugin.getLumberConfig().gameConfig().respawnCooldown()) {
                    respawnPlayer(e.getPlayer());
                    cancel();
                }
                secondsPassed++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * Respawn the player after a cooldown.
     *
     * @param player The player.
     */
    private void respawnPlayer(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(plugin.getLumberConfig().mapConfig().playerSpawnLocation());
        player.sendTitlePart(TitlePart.TITLE,
            MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().respawnedMessage())
        );
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(
            Duration.ofMillis(100),
            Duration.ofMillis(1000),
            Duration.ofMillis(200)
        ));
        perks.forEach(perk -> perk.onRespawn(player));
    }

    /**
     * Called when the inventory items themselves have changed (eg. new items/changed count, etc.)
     */
    private void onInventoryChanged(Player player) {
        onAnyInventoryChanged(player);
        // Scoreboard update isis delayed by 1 tick to let the events affect the player's inventory when accessed by scoreboard manager's inventory searcher
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().updatePlayerScoreboards(); // Update player item amounts
            }
        }.runTask(plugin);
    }

    /**
     * Called when any change has been made to the inventory (eg. item index, selected item, hotbar, etc.)
     */
    private void onAnyInventoryChanged(Player player) {
        // Run later to let inventory update
        new BukkitRunnable() {
            @Override
            public void run() {
                if (specialItem != null && !specialItem.isStillUsed()) {
                    specialItem = null;
                }
                else if (specialItem == null && equalsNotNull(ItemUtil.getLumberItemId(plugin, player.getInventory().getItemInMainHand()), "FIRE_STAFF")) {
                    specialItem = new FireStaffItem(plugin, player); // todo not just firestaff :) have like a switch() or smthn idk
                }
            }
        }.runTask(plugin);
    }
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        if (specialItem != null) {
            specialItem.onAttemptUse();
        }
    }


    @EventHandler
    public void onBlockFertilizeEvent(BlockFertilizeEvent e) {
        if (!e.getPlayer().getUniqueId().equals(uuid))
            return;
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;

        // If tree is max health, don't let the tree heal
        if (plugin.getGameManager().getTreeManager().getHealthToMaxHealthRatio() == 100) {
            e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getLumberConfig().strings().treeHealMaxHealthMessage())
            );
            e.setCancelled(true);
            return;
        }

        // Check whether the bone meal that was used is the Lumber bone meal
        boolean hasItem = ItemUtil.removeItemsFromInventory(plugin, e.getPlayer(), Map.of("BONE_MEAL", 1));
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
        boolean hasItem = ItemUtil.removeItemsFromInventory(plugin, e.getPlayer(), Map.of("BONE_BLOCK", 1));
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

    @EventHandler
    public void onBlockBreakEvent(CraftItemEvent e) {
        if (!e.getWhoClicked().getUniqueId().equals(uuid))
            return;
        e.setCancelled(true);
    }




    public UUID getUuid() {
        return uuid;
    }

    public List<Perk> getPerks() {
        return perks;
    }


}
