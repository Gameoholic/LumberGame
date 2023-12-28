package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasablePerkMenuItem;
import xyz.gameoholic.lumbergame.game.player.perk.RegenerationPerk;

import static net.kyori.adventure.text.Component.text;

public class ShopPerksMenu extends Menu {

    public ShopPerksMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Utility"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(13, new PurchasablePerkMenuItem(plugin, new RegenerationPerk()));

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
