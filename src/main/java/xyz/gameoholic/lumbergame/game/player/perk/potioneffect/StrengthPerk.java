package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import static net.kyori.adventure.text.Component.text;

public class StrengthPerk extends PotionEffectPerk {
    public StrengthPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * LEVEL * 3 + 5"; // {1, 2, 3} -> {8, 17, 32}
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
    @Override
    public Component getName() {
        return text(getEffectName());
    }
}
