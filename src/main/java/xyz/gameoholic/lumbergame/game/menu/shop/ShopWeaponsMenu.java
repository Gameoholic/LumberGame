package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;


import static net.kyori.adventure.text.Component.text;

public class ShopWeaponsMenu extends Menu {

    public ShopWeaponsMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Weapons"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(12, new PurchasableMenuItem(plugin, "STONE_SWORD", "IRON", 32));
        setItem(13, new PurchasableMenuItem(plugin, "IRON_SWORD", "IRON", 96));
        setItem(14, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", "WOOD", 80));
        setItem(22, new PurchasableMenuItem(plugin, "BOW", "IRON", 32));
        setItem(31, new PurchasableMenuItem(plugin, "ARROW", "IRON", 1));

        setItem(40, new MenuItem(plugin, "MENU_BACK"));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        switch (menuItem.getId()) {
            case "MENU_BACK":
                new ShopMainMenu(plugin, player);
                break;
        }
    }



}
