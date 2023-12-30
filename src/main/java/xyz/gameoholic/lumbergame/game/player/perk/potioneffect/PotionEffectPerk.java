package xyz.gameoholic.lumbergame.game.player.perk.potioneffect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;

import static xyz.gameoholic.lumbergame.util.OtherUtil.intToRoman;

public abstract class PotionEffectPerk extends Perk {
    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(getPotionEffectType(), Integer.MAX_VALUE, getLevel() - 1, false, false));
    }

    @Override
    public void onRespawn(Player player) {
        // When player respawns, loses potion effect. Reapply it.
        activate(player);
    }
    abstract PotionEffectType getPotionEffectType();

    @Override
    public String getDescription() {
        if (level == 0)
            return "<green>Grants you PERMANENT <effect_name> I."
                .replace("<effect_name>", getEffectName());
        if (level == getMaxLevel())
            return "<gray>Grants you PERMANENT <effect_name> <level>."
                .replace("<effect_name>", getEffectName())
                .replace("<level>", intToRoman(level));
        return "<gray>Grants you PERMANENT <effect_name> <level> <green> -> <new_level></green>."
            .replace("<effect_name>", getEffectName())
            .replace("<level>", intToRoman(level))
            .replace("<new_level>", intToRoman(level + 1));
    }

    /**
     * @return The name of the potion effect, to display in the description.
     */
    protected abstract String getEffectName();
}
