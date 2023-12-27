package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.LumberPlayer;

public class RegenerationPerk extends PotionEffectPerk {
    @Override
    String getCostExpression() {
        return "2 + (2 * LEVEL) * LEVEL"; // 2*x^2 + 2
    }

    @Override
    int getMaxLevel() {
        return 3;
    }

    @Override
    PotionEffectType getPotionEffectType() {
        return PotionEffectType.REGENERATION;
    }
}
