package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

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
        if (e.getBlock().getType() != Material.BONE_BLOCK)
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeLevelUpByPlayer(e.getPlayer());
    }
}
