package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;


import java.util.Map;

import static net.kyori.adventure.text.Component.text;

public class ShopWeaponsMenu extends Menu {

    public ShopWeaponsMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Weapons"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(12, new PurchasableMenuItem(plugin, "STONE_SWORD", Map.of("IRON", 32)));
        setItem(13, new PurchasableMenuItem(plugin, "IRON_SWORD", Map.of("IRON", 128, "WOOD", 5)));
        setItem(14, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", Map.of("WOOD", 80)));

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
