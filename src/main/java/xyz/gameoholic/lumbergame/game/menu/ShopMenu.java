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
        setItem(2, new PurchasableMenuItem(plugin, "STONE_SWORD", "IRON", 32));
        setItem(4, new PurchasableMenuItem(plugin, "IRON_SWORD", "IRON", 96));
        setItem(6, new PurchasableMenuItem(plugin, "DIAMOND_SWORD", "IRON", 192));
        setItem(8, new PurchasableMenuItem(plugin, "BOW", "GOLD", 2));
        setItem(9, new PurchasableMenuItem(plugin, "ARROW", "IRON", 1));

        setItem(11, new PurchasableMenuItem(plugin, "LEATHER_BOOTS", "IRON", 32));
        setItem(12, new PurchasableMenuItem(plugin, "LEATHER_LEGGINGS", "IRON", 48));
        setItem(13, new PurchasableMenuItem(plugin, "LEATHER_CHESTPLATE", "IRON", 64));
        setItem(14, new PurchasableMenuItem(plugin, "LEATHER_HELMET", "IRON", 32));

        setItem(20, new PurchasableMenuItem(plugin, "IRON_BOOTS", "GOLD", 4));
        setItem(21, new PurchasableMenuItem(plugin, "IRON_LEGGINGS", "GOLD", 8));
        setItem(22, new PurchasableMenuItem(plugin, "IRON_CHESTPLATE", "GOLD", 10));
        setItem(23, new PurchasableMenuItem(plugin, "IRON_HELMET", "GOLD", 4));
    }//when upgrading tree, not all breakness udpatesa!
    // less kelleys, more attackign mobs,  cheaper amor/more ogld
    // new sensation song
    // heal 50% on round end ||  wave 10 harder, faster spawn speed for round 10

    @Override
    protected void onUnhandledClick(MenuItem menuItem) {
        plugin.getLogger().info("Item was clicked with unhandled logic! " + menuItem.getId());
    }


}
