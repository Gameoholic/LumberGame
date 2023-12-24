package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;

import static net.kyori.adventure.text.Component.text;

public class ShopArmorMenu extends Menu {

    public ShopArmorMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Armor"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(11, new PurchasableMenuItem(plugin, "LEATHER_BOOTS", "IRON", 32));
        setItem(12, new PurchasableMenuItem(plugin, "LEATHER_LEGGINGS", "IRON", 48));
        setItem(13, new PurchasableMenuItem(plugin, "LEATHER_CHESTPLATE", "IRON", 64));
        setItem(14, new PurchasableMenuItem(plugin, "LEATHER_HELMET", "IRON", 32));

        setItem(20, new PurchasableMenuItem(plugin, "IRON_BOOTS", "GOLD", 2));
        setItem(21, new PurchasableMenuItem(plugin, "IRON_LEGGINGS", "GOLD", 4));
        setItem(22, new PurchasableMenuItem(plugin, "IRON_CHESTPLATE", "GOLD", 6));
        setItem(23, new PurchasableMenuItem(plugin, "IRON_HELMET", "GOLD", 2));

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
