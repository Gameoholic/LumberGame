package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

public class StrengthPerk extends PotionEffectPerk {
    public StrengthPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * LEVEL * 3 + 7"; // {1, 2, 3} -> {10, 19, 34}
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public String getCurrencyId() {
        return "GOLD";
    }

    @Override
    public PerkType getType() {
        return PerkType.EFFECT_STRENGTH;
    }

    @Override
    PotionEffectType getPotionEffectType() {
        return PotionEffectType.INCREASE_DAMAGE;
    }

    @Override
    protected String getEffectName() {
        return "Strength";
    }
}
