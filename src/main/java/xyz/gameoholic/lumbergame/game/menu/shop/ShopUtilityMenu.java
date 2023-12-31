package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;

import static net.kyori.adventure.text.Component.text;

public class ShopUtilityMenu extends Menu {

    public ShopUtilityMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Utility"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(22, new PurchasableMenuItem(plugin, "HEALTH_POTION", "WOOD", 2));
        setItem(13, new PurchasableMenuItem(plugin, "GOLD", "WOOD", 5));
        setItem(13, new PurchasableMenuItem(plugin, "GOLD", "IRON", 64));

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
