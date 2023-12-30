package xyz.gameoholic.lumbergame.game.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static xyz.gameoholic.lumbergame.util.OtherUtil.addLore;

/**
 * Represents an inventory menu.
 * The class registers its listeners and opens the inventory menu upon initialization.
 * It unregisters them upon closing of the menu (triggered when logging off or switching to another one).
 */
public abstract class Menu implements InventoryHolder, Listener {
    private UUID playerUUID;
    protected final LumberGamePlugin plugin;
    private Inventory inventory;
    /**
     * Used for inventory holder to differentiate different Menu implementations.
     */
    private Menu menu;
    private final Component title;
    private final int size;

    public Menu(LumberGamePlugin plugin, Component title, int size) {
        this.plugin = plugin;
        this.title = title;
        this.size = size;
    }

    // The reason why we don't do everything in the constructor, is because we need to pass Menu and create the inventory with it.
    // That is not possible until the parent Menu class is constructed, therefore we must construct it first,
    // then construct the implementation and pass it.

    /**
     * Creates the inventory, registers events and opens it for the player. Must be ran immediately.
     *
     * @param player The player to open the inventory for.
     * @param menu   The menu instance implementing this Menu.
     */
    protected void createInventory(Player player, Menu menu) {
        this.menu = menu;
        this.playerUUID = player.getUniqueId();

        inventory = Bukkit.createInventory(
            menu,
            size,
            title
        );
        registerEvents();
        setInventoryItems();
        player.openInventory(inventory);
    }


    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void unregisterEvents() {
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    /**
     * Use this method to set the inventory items.
     */
    protected abstract void setInventoryItems();

    /**
     * Adds this item to the inventory menus and adds all required PDC data.
     *
     * @param index    The index to put this item in.
     * @param menuItem The item to add.
     */
    protected void setItem(int index, MenuItem menuItem) {
        ItemStack itemStack = menuItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer()
            .set(new NamespacedKey(plugin, "menu_item_id"), PersistentDataType.STRING, menuItem.getId());
        // If item is purchasable menu item, store the currency id and amount in the PDC and add additional lore
        if (menuItem instanceof PurchasableMenuItem purchasableMenuItem) {
            itemMeta.getPersistentDataContainer()
                .set(new NamespacedKey(plugin, "purchasable_item_currency_id"),
                    PersistentDataType.STRING, purchasableMenuItem.getCurrencyItemId());
            itemMeta.getPersistentDataContainer()
                .set(new NamespacedKey(plugin, "purchasable_item_currency_amount"),
                    PersistentDataType.INTEGER, purchasableMenuItem.getCurrencyAmount());

            // Convert currency_icon to equivalent icon of currency
            Character currencyIcon = switch (purchasableMenuItem.getCurrencyItemId()) {
                case "IRON" -> plugin.getLumberConfig().strings().ironIcon();
                case "GOLD" -> plugin.getLumberConfig().strings().goldIcon();
                case "WOOD" -> plugin.getLumberConfig().strings().woodIcon();
                default -> '-';
            };
            List<Component> lores = itemMeta.lore();
            addLore(lores, MiniMessage.miniMessage().deserialize("<currency_icon><cost>",
                Placeholder.component("cost", text(purchasableMenuItem.getCurrencyAmount())),
                Placeholder.component("currency_icon", text(currencyIcon))
            ));
            itemMeta.lore(lores);
        }

        // If item is purchasable perk menu item, store the currency id and amount in the PDC and add additional lore
        if (menuItem instanceof PurchasablePerkMenuItem purchasablePerkMenuItem) {
            itemMeta.getPersistentDataContainer()
                .set(new NamespacedKey(plugin, "purchasable_perk"),
                    PersistentDataType.STRING, purchasablePerkMenuItem.getType().toString());

            LumberPlayer player = plugin.getGameManager().getPlayers().stream()
                .filter(lumberPlayer -> lumberPlayer.getUuid() == playerUUID).findFirst().get();
            Perk perk = Perk.getPerk(player, purchasablePerkMenuItem.getType());

            // Convert currency_icon to equivalent icon of currency
            Character currencyIcon = switch (perk.getCurrencyId()) {
                case "IRON" -> plugin.getLumberConfig().strings().ironIcon();
                case "GOLD" -> plugin.getLumberConfig().strings().goldIcon();
                case "WOOD" -> plugin.getLumberConfig().strings().woodIcon();
                default -> '-';
            };

            List<Component> lores = itemMeta.lore();
            // Perk description lore
            addLore(lores, MiniMessage.miniMessage().deserialize(perk.getDescription())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE));
            // Cost lore
            if (perk.getLevel() < perk.getMaxLevel()) {
                addLore(lores, MiniMessage.miniMessage().deserialize("<currency_icon><cost>",
                    Placeholder.component("cost", text(perk.getCost())),
                    Placeholder.component("currency_icon", text(currencyIcon))
                ));
            }
            // Level lore
            if (perk.getLevel() == 0) { // If perk isn't purchased yet
                addLore(lores, MiniMessage.miniMessage().deserialize("<green><bold>CLICK TO PURCHASE."));
            } else if (perk.getLevel() == perk.getMaxLevel()) {
                addLore(lores, MiniMessage.miniMessage().deserialize("<red><bold>MAX LEVEL"));
            } else {
                addLore(lores, MiniMessage.miniMessage().deserialize("<gold>Level <red><level><gold>/</gold><max_level><br><green><bold>CLICK TO UPGRADE",
                    Placeholder.component("level", text(perk.getLevel())),
                    Placeholder.component("max_level", text(perk.getMaxLevel()))
                ));
            }
            itemMeta.lore(lores);
        }

        itemStack.setItemMeta(itemMeta);
        inventory.setItem(index, itemStack);
    }

    protected void setItems(Map<Integer, MenuItem> items) {
        items.forEach((index, menuItem) -> setItem(index, menuItem));
    }

    /**
     * Attempts to purchase the item for the player.
     *
     * @param menuItem The purchasable menu item to attempt to purchase.
     * @return The ItemStack if the purchase was successful, null otherwise.
     */
    protected @Nullable ItemStack purchaseItem(PurchasableMenuItem menuItem) {
        if (processPurchase(menuItem.getCurrencyItemId(), menuItem.getCurrencyAmount()))
            return menuItem.item;
        return null;
    }

    /**
     * Attempts to purchase the item for the player.
     *
     * @param purchasablePerkMenuItem The purchasable perk menu item to attempt to purchase.
     * @return The ItemStack if the purchase was successful, null otherwise.
     */
    protected boolean purchasePerk(PurchasablePerkMenuItem purchasablePerkMenuItem) {
        LumberPlayer player = plugin.getGameManager().getPlayers().stream()
            .filter(lumberPlayer -> lumberPlayer.getUuid() == playerUUID).findFirst().get();

        boolean playerHasPerk = player.getPerks().stream()
            .filter(filteredPerk -> filteredPerk.getType() == purchasablePerkMenuItem.getType()).findFirst().isPresent();

        Perk perk = Perk.getPerk(player, purchasablePerkMenuItem.getType());

        if (perk.getLevel() == perk.getMaxLevel()) // Can't level up beyond max level
            return false;

        if (!processPurchase(perk.getCurrencyId(), perk.getCost()))
            return false;

        // If purchase was successful:
        if (!playerHasPerk)
            player.getPerks().add(perk); // If player doesn't have perk, add it
        perk.incrementLevel(); // Increment level. If perk was just added it'd be at level 0 anyway so we make it level 1
        perk.activate(Bukkit.getPlayer(playerUUID));

        setInventoryItems(); // Refresh the items with the new perk data
        return true;
    }

    /**
     * Attempts to remove the items from the player's inventory.
     *
     * @param itemId The Lumber item ID used for this purchase (currency).
     * @param amount The amount (cost) of this item.
     * @return Whether the purchase was processed successfully (if the player had enough items).
     */
    private boolean processPurchase(String itemId, int amount) {
        return plugin.getItemManager().removeItemsFromInventory(Bukkit.getPlayer(playerUUID), itemId, amount);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().getHolder().getClass() != menu.getClass()) // Make sure the event corresponds to this menu
            return;
        if (e.getWhoClicked().getUniqueId() != playerUUID)  // Make sure player corresponds to this menu
            return;

        @Nullable Inventory clickedInv = e.getClickedInventory();
        // In case player clicked outside of window or not on our inventory, cancel the click
        if (clickedInv == null || !(clickedInv.getHolder() instanceof Menu)) {
            e.setCancelled(true);
            return;
        }

        @Nullable ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null)
            return;

        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        @Nullable String itemId = itemStack.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "menu_item_id"), PersistentDataType.STRING);
        if (itemId == null)
            return;

        // Check if item is purchasable item
        @Nullable String currencyId = itemStack.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "purchasable_item_currency_id"), PersistentDataType.STRING);
        @Nullable Integer currencyAmount = itemStack.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "purchasable_item_currency_amount"), PersistentDataType.INTEGER);

        // Check if item is purchasable perk
        @Nullable String perkTypeString = itemStack.getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "purchasable_perk"), PersistentDataType.STRING);
        @Nullable PerkType perkType = perkTypeString != null ? PerkType.valueOf(perkTypeString) : null;

        // Return MenuItem/PurchasableMenuItem
        if (currencyId != null && currencyAmount != null)
            handleClick(new PurchasableMenuItem(plugin, itemId, currencyId, currencyAmount), player);
        else if (perkType != null)
            handleClick(new PurchasablePerkMenuItem(plugin, itemId, perkType), player);
        else
            handleClick(new MenuItem(plugin, itemId), player);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if (e.getInventory().getHolder().getClass() != menu.getClass()) // Make sure the event corresponds to this menu
            return;
        if (e.getPlayer().getUniqueId() != playerUUID) // Make sure player corresponds to this menu
            return;

        unregisterEvents();
    }

    /**
     * On inventory click. Handles purchasable items.
     *
     * @param menuItem The menu item that was clicked.
     * @param player   The player that clicked it.
     */
    private void handleClick(MenuItem menuItem, Player player) {
        if (menuItem instanceof PurchasableMenuItem purchasableMenuItem) {
            @Nullable ItemStack purchasedItem = purchaseItem(purchasableMenuItem);
            if (purchasedItem != null)
                player.getInventory().addItem(purchasedItem);
        } else if (menuItem instanceof PurchasablePerkMenuItem purchasablePerkMenuItem) {
            purchasePerk(purchasablePerkMenuItem);
        } else
            onUnhandledClick(menuItem, player);
    }

    /**
     * Use to implement whatever behavior is not handled.
     */
    protected abstract void onUnhandledClick(MenuItem menuItem, Player player);

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
