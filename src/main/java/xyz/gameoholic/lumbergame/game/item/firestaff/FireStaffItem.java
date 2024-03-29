package xyz.gameoholic.lumbergame.game.item.firestaff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.item.SpecialItem;
import xyz.gameoholic.lumbergame.util.ExpressionUtil;

import javax.annotation.Nullable;
import java.util.Map;

public class FireStaffItem extends SpecialItem {
    public FireStaffItem(LumberGamePlugin plugin, Player player) {
        super(plugin, player);
        cooldown = plugin.getLumberConfig().gameConfig().fireStaffCooldown();
        ammoItemId = "FIRE_CHARGE";
    }

    @Override
    protected void activateItem() {
        @Nullable Player player = Bukkit.getPlayer(ownerUUID);
        if (player == null)
            return; // Player can't really be null but we do this just in case, we don't like NPE's

        double dps = ExpressionUtil.evaluateExpression(plugin.getLumberConfig().gameConfig().fireStaffDPSExpression(),
                Map.of("WAVE", (double) plugin.getGameManager().getWaveNumber() + 1));
        new FireRing(plugin, player.getLocation().clone(), player.getLocation().getDirection(), dps, ownerUUID);

    }


}
