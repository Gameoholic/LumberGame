package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;
import xyz.gameoholic.lumbergame.game.player.perk.potioneffect.HealthBoostPerk;
import xyz.gameoholic.lumbergame.game.player.perk.potioneffect.RegenerationPerk;
import xyz.gameoholic.lumbergame.game.player.perk.potioneffect.SpeedPerk;
import xyz.gameoholic.lumbergame.game.player.perk.potioneffect.StrengthPerk;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class Perk {
    protected int level = 1;

    /**
     * Activates the perk. Triggered on purchase or level up of perk.
     * @param player The Player this perk belongs to.
     */
    public abstract void activate(Player player);
    /**
     * Triggered on respawn of the player.
     * @param player The Player this perk belongs to.
     */
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
    public abstract String getDescription();

    public void incrementLevel() {
        level++;
    }


    /**
     * Gets a player's perk.
     * @param player The player.
     * @param perkType The perk type.
     * @return The perk belonging to the player, or a new perk with level 0.
     */
    public static Perk getPerk(LumberPlayer player, PerkType perkType) {
        @Nullable Perk foundPerk = player.getPerks().stream()
            .filter(filteredPerk -> filteredPerk.getType() == perkType).findFirst().orElse(null);

        return (foundPerk != null) ? foundPerk : switch (perkType) {
            case EFFECT_REGEN -> new RegenerationPerk(0);
            case EFFECT_SPEED -> new SpeedPerk(0);
            case EFFECT_STRENGTH -> new StrengthPerk(0);
            case EFFECT_HEALTH_BOOST -> new HealthBoostPerk(0);
        };
    }
}
