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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

/**
 * Represents an inventory menu.
 * The class registers its listeners and opens the inventory menu upon initialization.
 * It unregisters them upon closing of the menu (triggered when logging off or switching to another one).
 */
public abstract class Menu implements InventoryHolder, Listener {
    private final UUID playerUUID;
    protected final LumberGamePlugin plugin;
    private final Inventory inventory;

    public Menu(LumberGamePlugin plugin, Player player, Component title, int size) {
        this.plugin = plugin;
        this.playerUUID = player.getUniqueId();

        inventory = Bukkit.createInventory(
            this,
            size,
            title
        );
        registerEvents();
        setInventoryItems();
        player.openInventory(inventory);
    }

    private void registerEvents() {
        Bukkit.broadcastMessage("Registering events");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void unregisterEvents() {
        Bukkit.broadcastMessage("UNRegistering events");

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
            lores.add(MiniMessage.miniMessage().deserialize("<reset><white><currency_icon><cost>",
                Placeholder.component("cost", text(purchasableMenuItem.getCurrencyAmount())),
                Placeholder.component("currency_icon", text(currencyIcon)))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .colorIfAbsent(NamedTextColor.WHITE)
            );
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
        if (e.getWhoClicked().getUniqueId() != playerUUID)
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

        // Return MenuItem/PurchasableMenuItem
        if (currencyId != null && currencyAmount != null)
            handleClick(new PurchasableMenuItem(plugin, itemId, currencyId, currencyAmount), player);
        else
            handleClick(new MenuItem(plugin, itemId), player);
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if (e.getPlayer().getUniqueId() != playerUUID)
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
        }
        else
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
