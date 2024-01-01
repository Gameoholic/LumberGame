package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import static net.kyori.adventure.text.Component.text;
import static xyz.gameoholic.lumbergame.util.OtherUtil.intToRoman;

public class HealthBoostPerk extends PotionEffectPerk {
    public HealthBoostPerk(int level) {
        this.level = level;
    }
    @Override
    public String getCostExpression() {
        return "LEVEL * LEVEL + 3"; // {1, 2, 3, 4, 5} -> {4, 7, 12, 19, 28}
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public String getCurrencyId() {
        return "GOLD";
    }

    @Override
    public PerkType getType() {
        return PerkType.EFFECT_HEALTH_BOOST;
    }

    @Override
    PotionEffectType getPotionEffectType() {
        return PotionEffectType.HEALTH_BOOST;
    }

    @Override
    protected String getEffectName() {
        return "Health Boost";
    }

    @Override
    public String getDescription() {
        if (level == 0)
            return "<green>Grants you PERMANENT 2 hearts.";
        if (level == getMaxLevel())
            return "<gray>Grants you PERMANENT <hearts> hearts."
                .replace("<hearts>", level * 2 + "");
        return "<gray>Grants you PERMANENT <hearts> <green> -> <new_hearts></green> hearts."
            .replace("<hearts>", level * 2 + "")
            .replace("<new_hearts>", (level + 1) * 2 + "");
    }

    @Override
    public Component getName() {
        return text(getEffectName());
    }
}
