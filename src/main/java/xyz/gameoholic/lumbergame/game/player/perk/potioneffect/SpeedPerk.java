package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import static net.kyori.adventure.text.Component.text;

public class SpeedPerk extends PotionEffectPerk {
    public SpeedPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * 2 - 1"; // {1, 2, 3} -> {1, 3, 5}
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
    @Override
    public Component getName() {
        return text(getEffectName());
    }
}
