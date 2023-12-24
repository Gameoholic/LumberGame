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
        setItem(2, new PurchasableMenuItem(plugin, "STONE_SWORD", "IRON", 32));
        setItem(4, new PurchasableMenuItem(plugin, "IRON_SWORD", "IRON", 96));
        setItem(6, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", "IRON", 256));
        setItem(8, new PurchasableMenuItem(plugin, "BOW", "IRON", 32));
        setItem(9, new PurchasableMenuItem(plugin, "ARROW", "IRON", 1));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        plugin.getLogger().info("Item was clicked with unhandled logic! " + menuItem.getId());
    }


}
