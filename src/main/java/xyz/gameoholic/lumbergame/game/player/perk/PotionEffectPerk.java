package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PotionEffectPerk extends Perk {
    @Override
    void apply(Player player) {
        player.addPotionEffect(new PotionEffect(getPotionEffectType(), 10000000, getLevel(), false, false));
    }

    @Override
    void onRespawn(Player player) {
        // When player respawns, loses potion effect. Reapply it.
        apply(player);
    }
    abstract PotionEffectType getPotionEffectType();

}
