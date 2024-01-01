package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import static net.kyori.adventure.text.Component.text;

public class RegenerationPerk extends PotionEffectPerk {
    public RegenerationPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * LEVEL * 2 + 2"; // {1, 2, 3} -> {4, 10, 20}
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
        return PerkType.EFFECT_REGEN;
    }

    @Override
    PotionEffectType getPotionEffectType() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    protected String getEffectName() {
        return "Regeneration";
    }
    @Override
    public Component getName() {
        return text(getEffectName());
    }
}
