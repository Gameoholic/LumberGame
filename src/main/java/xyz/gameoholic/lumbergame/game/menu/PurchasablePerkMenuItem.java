package xyz.gameoholic.lumbergame.game.menu;

import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;

public class PurchasablePerkMenuItem extends MenuItem {
    private final Perk perk;
    public PurchasablePerkMenuItem(LumberGamePlugin plugin, Perk perk) {
        super(plugin, perk.getItemId());
        this.perk = perk;
    }


    public Perk getPerk() {
        return perk;
    }

}
