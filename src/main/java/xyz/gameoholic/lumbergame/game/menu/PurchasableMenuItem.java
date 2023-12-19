package xyz.gameoholic.lumbergame.game.menu;

import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;

public class PurchasableMenuItem extends MenuItem {
    private final String currencyItemId;
    private final int currencyAmount;
    public PurchasableMenuItem(LumberGamePlugin plugin, String id, String currencyItemId, int currencyAmount) {
        super(plugin, id);
        this.currencyItemId = currencyItemId;
        this.currencyAmount = currencyAmount;
    }

    public String getCurrencyItemId() {
        return currencyItemId;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }
}
