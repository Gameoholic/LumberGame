package xyz.gameoholic.lumbergame.game.menu;

import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.Map;

public class PurchasableMenuItem extends MenuItem {
    /**
     * Currency amounts mapped to currency item ID's.
     */
    private final Map<String, Integer> cost;
    private final int amount;
    public PurchasableMenuItem(LumberGamePlugin plugin, String id, Map<String, Integer> cost, int amount) {
        super(plugin, id);
        this.cost = cost;
        this.amount = amount;
    }

    public Map<String, Integer> getCost() { return cost; }
    public int getAmount() { return amount; }
}
