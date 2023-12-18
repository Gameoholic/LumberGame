package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;

public class BlockPlaceListener implements Listener {
    private LumberGamePlugin plugin;

    public BlockPlaceListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;
        // Must be lumber bone meal. Technically, this doesn't check if it's the item that was used, but that's fine.
        @Nullable ItemStack item = plugin.getItemManager().getItemInInventory(e.getPlayer(), "bone_block");
        if (item == null)
            return;

        e.setCancelled(true);
        e.getPlayer().getInventory().remove(item);
        plugin.getGameManager().getTreeManager().onTreeLevelUpByPlayer(e.getPlayer());
    }
}
