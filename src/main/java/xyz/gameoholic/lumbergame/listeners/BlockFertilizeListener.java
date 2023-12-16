package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

public class BlockFertilizeListener implements Listener {
    private LumberGamePlugin plugin;
    public BlockFertilizeListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBlockFertilizeEvent(BlockFertilizeEvent e) {
        // Must be near Tree
        if (e.getBlock().getLocation().distanceSquared(plugin.getLumberConfig().mapConfig().treeLocation())
            > plugin.getLumberConfig().mapConfig().treeRadius())
            return;

        e.setCancelled(true);
        plugin.getGameManager().getTreeManager().onTreeHealByPlayer(e.getPlayer());
    }
}
