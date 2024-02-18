package xyz.gameoholic.lumbergame.game.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.UUID;

public class FireStaffItem extends SpecialItem {


    public FireStaffItem(LumberGamePlugin plugin, Player player) {
        super(plugin, player);
    }

    @Override
    protected void activateItem() {
        Bukkit.broadcastMessage("Activated fire staff");
    }


}
