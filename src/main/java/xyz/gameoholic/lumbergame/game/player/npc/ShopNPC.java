package xyz.gameoholic.lumbergame.game.player.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.ShopMenu;

import javax.annotation.Nullable;
import java.util.UUID;

public class ShopNPC extends LumberNPC {
    public ShopNPC(LumberGamePlugin plugin, UUID playerUUID, Location NPCLocation) {
        super(plugin, playerUUID, NPCLocation);
    }

    @Override
    public void onInteract(boolean isAttack) {
        if (isAttack)
            return;
        @Nullable Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            new ShopMenu(plugin, player);
    }
}
