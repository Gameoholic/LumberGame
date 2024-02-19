package xyz.gameoholic.lumbergame.game.menu.shop;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.Menu;
import xyz.gameoholic.lumbergame.game.menu.MenuItem;
import xyz.gameoholic.lumbergame.game.menu.PurchasableMenuItem;

import java.util.HashMap;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;

public class ShopArmorMenu extends Menu {

    public ShopArmorMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, text("Armor"), 45);
        createInventory(player, this);
    }

    @Override
    protected void setInventoryItems() {
        setItem(3, new PurchasableMenuItem(plugin, "LEATHER_BOOTS", Map.of("IRON", 16), 1));
        setItem(12, new PurchasableMenuItem(plugin, "LEATHER_LEGGINGS", Map.of("IRON", 32), 1));
        setItem(21, new PurchasableMenuItem(plugin, "LEATHER_CHESTPLATE",Map.of("IRON", 48), 1));
        setItem(30, new PurchasableMenuItem(plugin, "LEATHER_HELMET",Map.of("IRON", 32), 1));

        setItem(4, new PurchasableMenuItem(plugin, "IRON_BOOTS", Map.of("IRON", 64, "WOOD", 5), 1));
        setItem(13, new PurchasableMenuItem(plugin, "IRON_LEGGINGS", Map.of("IRON", 128, "WOOD", 10), 1));
        setItem(22, new PurchasableMenuItem(plugin, "IRON_CHESTPLATE", Map.of("IRON", 192, "WOOD", 15), 1));
        setItem(31, new PurchasableMenuItem(plugin, "IRON_HELMET", Map.of("IRON", 128, "WOOD", 8), 1));

        setItem(5, new PurchasableMenuItem(plugin, "DIAMOND_BOOTS", Map.of("WOOD", 32), 1));
        setItem(14, new PurchasableMenuItem(plugin, "DIAMOND_LEGGINGS", Map.of("WOOD", 64), 1));
        setItem(23, new PurchasableMenuItem(plugin, "DIAMOND_CHESTPLATE", Map.of("WOOD", 96), 1));
        setItem(32, new PurchasableMenuItem(plugin, "DIAMOND_HELMET", Map.of("WOOD", 64), 1));

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
