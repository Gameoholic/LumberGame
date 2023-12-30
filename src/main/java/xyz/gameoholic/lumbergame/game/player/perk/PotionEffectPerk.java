package xyz.gameoholic.lumbergame.game.player.perk;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PotionEffectPerk extends Perk {
    @Override
    public void activate(Player player) {
        player.addPotionEffect(new PotionEffect(getPotionEffectType(), 10000000, getLevel() - 1, false, false));
    }

    @Override
    public void onRespawn(Player player) {
        // When player respawns, loses potion effect. Reapply it.
        activate(player);
    }
    abstract PotionEffectType getPotionEffectType();

    @Override
    public String getDescription() {
        return (switch (level) {
            case 0 -> "<green>Grants you PERMANENT EFFECT_NAME I."; //todo: customize nmae
            case 1 -> "<gray>Grants you PERMANENT EFFECT_NAME I <green>-> II</green>.";
            case 2 -> "<gray>Grants you PERMANENT EFFECT_NAME II <green>-> III</green>.";
            default -> "<gray>Grants you PERMANENT EFFECT_NAME III.";
        }).replace("EFFECT_NAME", getEffectName());
    }

    /**
     * @return The name of the potion effect, to display in the description.
     */
    protected abstract String getEffectName();
}
