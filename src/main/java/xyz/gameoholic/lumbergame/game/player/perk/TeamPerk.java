package xyz.gameoholic.lumbergame.game.player.perk;

import net.kyori.adventure.text.Component;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;

public abstract class TeamPerk extends Perk {
    /**
     * @return The display name used for broadcasting the purchase of the perk
     */
    public abstract Component getName();
}
