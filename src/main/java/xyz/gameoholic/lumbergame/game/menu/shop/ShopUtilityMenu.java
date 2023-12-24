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
        setItem(25, new PurchasableMenuItem(plugin, "HEALTH_POTION", "WOOD", 2));

    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem, Player player) {
        plugin.getLogger().info("Item was clicked with unhandled logic! " + menuItem.getId());
    }


}
