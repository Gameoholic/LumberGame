package xyz.gameoholic.lumbergame.game.menu;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;



import static net.kyori.adventure.text.Component.text;

public class ShopMenu extends Menu {

    public ShopMenu(LumberGamePlugin plugin, Player player) {
        super(plugin, player, text("Shop!"), 36);
    }

    @Override
    protected void setInventoryItems() {
        setItem(1, new PurchasableMenuItem(plugin, "STONE_SWORD", "IRON", 20));
        setItem(3, new PurchasableMenuItem(plugin, "IRON_SWORD", "IRON", 64));
        setItem(5, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", "IRON", 128));
    }

    @Override
    protected void onUnhandledClick(MenuItem menuItem) {
        plugin.getLogger().info("Item was clicked with unhandled logic! " + menuItem.getId());
    }


}
