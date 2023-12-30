package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;

import static net.kyori.adventure.text.Component.text;

public class ShopMainMenu extends Menu implements InventoryHolder {

    public ShopMainMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Shop"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(21, new MenuItem(plugin, "MENU_WEAPONS"));
        setItem(22, new MenuItem(plugin, "MENU_ARMOR"));
        setItem(23, new MenuItem(plugin, "MENU_UTILITY"));
        setItem(31, new MenuItem(plugin, "MENU_PERKS"));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        switch (menuItem.getId()) {
            case "MENU_WEAPONS":
                new ShopWeaponsMenu(plugin, player);
                break;
            case "MENU_ARMOR":
                new ShopArmorMenu(plugin, player);
                break;
            case "MENU_UTILITY":
                new ShopUtilityMenu(plugin, player);
                break;
            case "MENU_PERKS":
                new ShopPerksMenu(plugin, player);
                break;
        }


    }


}
