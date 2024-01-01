package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

public class SpeedPerk extends PotionEffectPerk {
    public SpeedPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * LEVEL"; // {1, 2, 3} -> {1, 4, 9}
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
        return PerkType.EFFECT_SPEED;
    }

    @Override
    PotionEffectType getPotionEffectType() {
        return PotionEffectType.SPEED;
    }

    @Override
    protected String getEffectName() {
        return "Speed";
    }
}