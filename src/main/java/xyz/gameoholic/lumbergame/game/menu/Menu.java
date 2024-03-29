package xyz.gameoholic.lumbergame.game.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
import xyz.gameoholic.lumbergame.game.player.perk.TeamPerk;
import xyz.gameoholic.lumbergame.util.ItemUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static xyz.gameoholic.lumbergame.util.OtherUtil.addLore;
import static xyz.gameoholic.lumbergame.util.OtherUtil.intToRoman;

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
                    .set(new NamespacedKey(plugin, "purchasable_item_cost"),
                            PersistentDataType.STRING, serializeCostMap(purchasableMenuItem.getCost()));
            itemMeta.getPersistentDataContainer()
                    .set(new NamespacedKey(plugin, "purchasable_item_amount"),
                            PersistentDataType.INTEGER, purchasableMenuItem.getAmount());
            itemStack.setAmount(purchasableMenuItem.getAmount());

            List<Component> lores = itemMeta.lore();
            for (Map.Entry<String, Integer> costEntry : purchasableMenuItem.getCost().entrySet()) {
                // Convert currency_icon to equivalent icon of currency
                Character currencyIcon = switch (costEntry.getKey()) {
                    case "IRON" -> plugin.getLumberConfig().strings().ironIcon();
                    case "GOLD" -> plugin.getLumberConfig().strings().goldIcon();
                    case "WOOD" -> plugin.getLumberConfig().strings().woodIcon();
                    default -> '-';
                };
                addLore(lores, "<currency_icon><cost>",
                        Placeholder.component("cost", text(costEntry.getValue())),
                        Placeholder.component("currency_icon", text(currencyIcon))
                );
            }

            itemMeta.lore(lores);
        }

        // If item is purchasable perk menu item, store the currency id and amount in the PDC and add additional lore
        if (menuItem instanceof PurchasablePerkMenuItem purchasablePerkMenuItem) {
            itemMeta.getPersistentDataContainer()
                    .set(new NamespacedKey(plugin, "purchasable_perk"),
                            PersistentDataType.STRING, purchasablePerkMenuItem.getType().toString());

            LumberPlayer player = plugin.getGameManager().getPlayers().stream()
                    .filter(lumberPlayer -> lumberPlayer.getUuid() == playerUUID).findFirst().get();
            Perk perk = Perk.getPerk(player, purchasablePerkMenuItem.getType(), plugin);

            // Convert currency_icon to equivalent icon of currency
            Character currencyIcon = switch (perk.getCurrencyId()) {
                case "IRON" -> plugin.getLumberConfig().strings().ironIcon();
                case "GOLD" -> plugin.getLumberConfig().strings().goldIcon();
                case "WOOD" -> plugin.getLumberConfig().strings().woodIcon();
                default -> '-';
            };

            List<Component> lores = itemMeta.lore();
            // Perk description lore
            addLore(lores, perk.getDescription());
            // Cost lore
            int perkCostMultiplier = (perk instanceof TeamPerk) ? plugin.getGameManager().getPlayers().size() : 1; // If perk is team perk, multiply cost by player count
            if (perk.getLevel() < perk.getMaxLevel()) {
                addLore(lores, "<currency_icon><cost>",
                        Placeholder.component("cost", text(perk.getCost() * perkCostMultiplier)),
                        Placeholder.component("currency_icon", text(currencyIcon))
                );
            }
            // Team/solo perk lore
            if (perk instanceof TeamPerk) {
                addLore(lores, "<red><bold>TEAM PERK");
            }
            // Level lore
            if (perk.getLevel() == 0) { // If perk isn't purchased yet
                addLore(lores, "<red>Max Level: <max_level><br><green><bold>CLICK TO PURCHASE",
                        Placeholder.component("max_level", text(perk.getMaxLevel()))
                );
            } else if (perk.getLevel() == perk.getMaxLevel()) {
                addLore(lores, "<red><bold>MAX LEVEL");
            } else {
                addLore(lores, "<gold>Level <red><level><gold>/</gold><max_level><br><green><bold>CLICK TO UPGRADE",
                        Placeholder.component("level", text(perk.getLevel())),
                        Placeholder.component("max_level", text(perk.getMaxLevel()))
                );
            }

            itemMeta.lore(lores);
        }

        itemStack.setItemMeta(itemMeta);
        inventory.setItem(index, itemStack);
    }

    /**
     * Serializes a map consisting of currency amounts mapped to currency item ID's into a string
     */
    private String serializeCostMap(Map<String, Integer> map) {
        String str = "";
        for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
            str += mapEntry.getKey() + ":" + mapEntry.getValue() + ",";
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }

    /**
     * Deserializes a String back into a map consisting of currency amounts mapped to currency item ID's
     */
    private Map<String, Integer> deserializeCostString(String str) {
        Map<String, Integer> map = new HashMap<>();
        String[] costs = str.split(",");
        for (String cost : costs) {
            String costCurrencyId = cost.split(":")[0];
            int costCurrencyAmount = Integer.parseInt(cost.split(":")[1]);
            map.put(costCurrencyId, costCurrencyAmount);
        }
        return map;
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
        if (processPurchase(menuItem.getCost())) {
            ItemStack item = menuItem.item;
            item.setAmount(menuItem.getAmount());
            return item;
        }
        return null;
    }

    /**
     * Attempts to purchase the item for the player.
     *
     * @param purchasablePerkMenuItem The purchasable perk menu item to attempt to purchase.
     * @return The ItemStack if the purchase was successful, null otherwise.
     */
    protected boolean purchasePerk(PurchasablePerkMenuItem purchasablePerkMenuItem) {
        LumberPlayer lumberPlayer = plugin.getGameManager().getPlayers().stream()
                .filter(filteredLumberPlayer -> filteredLumberPlayer.getUuid() == playerUUID).findFirst().get();

        boolean playerHasPerk = lumberPlayer.getPerks().stream()
                .filter(filteredPerk -> filteredPerk.getType() == purchasablePerkMenuItem.getType()).findFirst().isPresent();

        Perk perk = Perk.getPerk(lumberPlayer, purchasablePerkMenuItem.getType(), plugin);

        if (perk.getLevel() == perk.getMaxLevel()) // Can't level up beyond max level
            return false;

        int perkCostMultiplier = (perk instanceof TeamPerk) ? (int) plugin.getGameManager().getWaveCRMultiplier() : 1; // If perk is team perk, multiply cost by CR multiplier
        if (!processPurchase(Map.of(perk.getCurrencyId(), perk.getCost() * perkCostMultiplier)))
            return false;

        // If purchase was successful:
        if (perk instanceof TeamPerk) {
            // Apply perk to all players
            plugin.getGameManager().getPlayers().forEach(teamLumberPlayer -> {
                if (!playerHasPerk) // If player doesn't have team perk, nobody has it
                    teamLumberPlayer.getPerks().add(Perk.getPerk(teamLumberPlayer, purchasablePerkMenuItem.getType(), plugin)); // Get new perk specific to this player

                @Nullable Player teamPlayer = Bukkit.getPlayer(teamLumberPlayer.getUuid());
                TeamPerk teamPerk = (TeamPerk) teamLumberPlayer.getPerks().stream()
                        .filter(filteredPerk -> filteredPerk.getType() == purchasablePerkMenuItem.getType()).findFirst().get();
                teamPerk.incrementLevel();

                if (teamPlayer != null) {
                    teamPerk.activate(teamPlayer);
                    teamPlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                            plugin.getLumberConfig().strings().teamPerkBuyMessage(),
                            Placeholder.component("player", Bukkit.getPlayer(playerUUID).name()),
                            Placeholder.component("perk", teamPerk.getName()),
                            Placeholder.component("level", text(intToRoman(teamPerk.getLevel())))
                    ));
                }
                // todo: refresh inventory items for every player not just the one who purchased it. with setInventoryItems()
            });
        } else {
            // Apply perk only to player who bought it
            if (!playerHasPerk) {  // If player doesn't have perk, add it
                lumberPlayer.getPerks().add(perk);
            }
            perk.incrementLevel(); // Increment level. If perk was just added it'd be at level 0 anyway so we make it level 1
            perk.activate(Bukkit.getPlayer(playerUUID));
        }
        setInventoryItems(); // Refresh the items with the new perk data
        return true;
    }

    /**
     * Attempts to remove the items from the player's inventory.
     *
     * @param cost Currency amounts mapped to currency item ID's.
     * @return Whether the purchase was processed successfully (if the player had enough items).
     */
    private boolean processPurchase(Map<String, Integer> cost) {
        return ItemUtil.removeItemsFromInventory(plugin, Bukkit.getPlayer(playerUUID), cost);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().getHolder().getClass() != menu.getClass()) // Make sure the event corresponds to this menu
            return;
        if (e.getWhoClicked().getUniqueId() != playerUUID)  // Make sure player corresponds to this menu
            return;

        @Nullable Inventory clickedInv = e.getClickedInventory();

        // In case player clicked outside of window or not on our inventory or number-dragged, cancel the click
        if (clickedInv == null || !(clickedInv.getHolder() instanceof Menu) || e.getClick() == ClickType.NUMBER_KEY) {
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
        @Nullable String cost = itemStack.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "purchasable_item_cost"), PersistentDataType.STRING);
        @Nullable Integer amount = itemStack.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "purchasable_item_amount"), PersistentDataType.INTEGER);

        // Check if item is purchasable perk
        @Nullable String perkTypeString = itemStack.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "purchasable_perk"), PersistentDataType.STRING);
        @Nullable PerkType perkType = perkTypeString != null ? PerkType.valueOf(perkTypeString) : null;

        // Return MenuItem/PurchasableMenuItem
        if (cost != null && amount != null)
            handleClick(new PurchasableMenuItem(plugin, itemId, deserializeCostString(cost), amount), player);
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
