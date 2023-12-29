package xyz.gameoholic.lumbergame.game.menu;


import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.player.perk.Perk;
import xyz.gameoholic.lumbergame.game.player.perk.PerkType;

import javax.annotation.Nullable;


public class PurchasablePerkMenuItem extends MenuItem {
    private final PerkType type;
    public PurchasablePerkMenuItem(LumberGamePlugin plugin, String itemId, PerkType type) {
        super(plugin, itemId);
        this.type = type;
    }


    public PerkType getType() {
        return type;
    }



}
