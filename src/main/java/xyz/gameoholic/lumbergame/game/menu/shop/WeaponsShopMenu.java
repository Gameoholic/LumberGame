package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;


import static net.kyori.adventure.text.Component.text;

public class WeaponsShopMenu extends Menu {

    public WeaponsShopMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Weapons"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(2, new PurchasableMenuItem(plugin, "STONE_SWORD", "IRON", 32));
        setItem(4, new PurchasableMenuItem(plugin, "IRON_SWORD", "IRON", 96));
        setItem(6, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", "IRON", 256));
        setItem(8, new PurchasableMenuItem(plugin, "BOW", "IRON", 32));
        setItem(9, new PurchasableMenuItem(plugin, "ARROW", "IRON", 1));

//        setItem(11, new PurchasableMenuItem(plugin, "LEATHER_BOOTS", "IRON", 32));
//        setItem(12, new PurchasableMenuItem(plugin, "LEATHER_LEGGINGS", "IRON", 48));
//        setItem(13, new PurchasableMenuItem(plugin, "LEATHER_CHESTPLATE", "IRON", 64));
//        setItem(14, new PurchasableMenuItem(plugin, "LEATHER_HELMET", "IRON", 32));
//
//        setItem(20, new PurchasableMenuItem(plugin, "IRON_BOOTS", "GOLD", 2));
//        setItem(21, new PurchasableMenuItem(plugin, "IRON_LEGGINGS", "GOLD", 4));
//        setItem(22, new PurchasableMenuItem(plugin, "IRON_CHESTPLATE", "GOLD", 6));
//        setItem(23, new PurchasableMenuItem(plugin, "IRON_HELMET", "GOLD", 2));
//
//        setItem(25, new PurchasableMenuItem(plugin, "HEALTH_POTION", "WOOD", 2));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        plugin.getLogger().info("Item was clicked with unhandled logic! " + menuItem.getId());
    }


}
