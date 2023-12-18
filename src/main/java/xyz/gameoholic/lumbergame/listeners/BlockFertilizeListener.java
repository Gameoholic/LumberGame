package xyz.gameoholic.lumbergame.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.InventoryUtil;

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
        InventoryUtil.removeItemFromInventory(e.getPlayer(), Material.BONE_MEAL, 1);
        plugin.getGameManager().getTreeManager().onTreeHealByPlayer(e.getPlayer());
    }
}
