package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;

import static net.kyori.adventure.text.Component.text;

public class ShopMainMenu extends Menu {

    public ShopMainMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, player, text("Shop"), 45);
    }

    @Override
    protected void setInventoryItems() {
        setItem(21, new MenuItem(plugin, "MENU_WEAPONS"));
        setItem(22, new MenuItem(plugin, "MENU_ARMOR"));
        setItem(23, new MenuItem(plugin, "MENU_UTILITY"));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        switch (menuItem.getId()) {
            case "MENU_WEAPONS":
                new WeaponsShopMenu(plugin, player);
        }


    }


}
