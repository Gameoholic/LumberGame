package xyz.gameoholic.lumbergame.game.player.npc;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.menu.shop.ShopMainMenu;

import javax.annotation.Nullable;
import java.util.UUID;

public class ShopNPC extends LumberNPC {
    public ShopNPC(LumberGamePlugin plugin, UUID playerUUID, Location NPCLocation, Component NPCName) {
        super(plugin, playerUUID, NPCLocation, NPCName, true);
    }

    @Override
    public void onNPCInteract(boolean isAttack) {
        if (isAttack)
            return;
        @Nullable Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            new ShopMainMenu(plugin, player);
    }
}
