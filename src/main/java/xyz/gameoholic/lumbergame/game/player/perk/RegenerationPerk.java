package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.potion.PotionEffectType;

public class RegenerationPerk extends PotionEffectPerk {
    public RegenerationPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "2 + (2 * LEVEL) * LEVEL"; // 2*x^2 + 2
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
}
