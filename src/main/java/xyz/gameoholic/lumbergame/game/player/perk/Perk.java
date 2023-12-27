package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;

import java.util.Map;

public abstract class Perk {
    private int level = 1;
    abstract void apply(Player player);
    abstract void onRespawn(Player player);

    int getCost() {
        return (int) ExpressionUtil.evaluateExpression(getCostExpression(), Map.of("LEVEL", (double) level));
    }
    abstract String getCostExpression();
    abstract int getMaxLevel();

    public int getLevel() {
        return level;
    }

}
