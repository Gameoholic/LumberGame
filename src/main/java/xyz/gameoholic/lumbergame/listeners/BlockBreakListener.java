package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

public class BlockBreakListener implements Listener {
    private LumberGamePlugin plugin;

    public BlockBreakListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        // Must be near Tree
        if (e.getBlock().getLocation().distance(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;
        // Must be one of the tree block types
        if (!(plugin.getLumberConfig().mapConfig().treeBlockTypes().contains(e.getBlock().getType())))
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeChopByPlayer(e.getPlayer(), e.getBlock().getLocation());
    }
}
