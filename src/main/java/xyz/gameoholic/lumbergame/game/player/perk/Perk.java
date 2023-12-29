package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;

import java.util.Map;

public abstract class Perk {
    private int level = 1;
    public abstract void apply(Player player);
    public abstract void onRespawn(Player player);

    public int getCost() {
        return (int) ExpressionUtil.evaluateExpression(getCostExpression(), Map.of("LEVEL", (double) level));
    }
    public abstract String getCostExpression();
    public abstract int getMaxLevel();

    /**
     * @return The item ID of the currency used to purchase this perk.
     */
    public abstract String getCurrencyId();


    public abstract PerkType getType();
    public int getLevel() {
        return level;
    }

    public void incrementLevel() {
        level++;
    }
}
