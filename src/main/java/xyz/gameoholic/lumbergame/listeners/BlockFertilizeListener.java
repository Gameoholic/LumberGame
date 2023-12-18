package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;

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
        // Must be lumber bone meal. Technically, this doesn't check if it's the item that was used, but that's fine.
        @Nullable ItemStack item = plugin.getItemManager().getItemInInventory(e.getPlayer(), "bone_meal");
        if (item == null)
            return;

        e.setCancelled(true);
        e.getPlayer().getInventory().remove(item);
        plugin.getGameManager().getTreeManager().onTreeHealByPlayer(e.getPlayer());
    }
}
