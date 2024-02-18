package xyz.gameoholic.lumbergame.game.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class SpecialItem {
    private final LumberGamePlugin plugin;
    protected int cooldown;
    private int currentCooldownRemaining;
    private BukkitTask task;
    private UUID ownerUUID;
    private ItemStack itemStack;
    public SpecialItem(LumberGamePlugin plugin, Player player) {
        this.plugin = plugin;
        this.ownerUUID = player.getUniqueId();
        this.itemStack = player.getInventory().getItemInMainHand();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Bukkit.broadcastMessage("Constructed!");
    }

    public void onAttemptUse() {
        if (currentCooldownRemaining > 0) {
            return;
        }
        currentCooldownRemaining = cooldown;
        activateItem();
    }
    //todo when itme instance destroyed
    public void onDestroy() {
        task.cancel();
    }

    protected abstract void activateItem();
    protected void onTick() {
        currentCooldownRemaining = Math.max(0, currentCooldownRemaining - 1);
    }

    /**
     * Checks to see if the item is still used by the player. If it isn't, cleans up this instance (disables task).
     * @return Whether the item is still used in the main hand by the player.
     */
    public boolean isStillUsed() {
        @Nullable Player owner = Bukkit.getPlayer(ownerUUID);
        if (owner == null || !owner.getInventory().getItemInMainHand().equals(itemStack)) {
            task.cancel();
            Bukkit.broadcastMessage("Item Removed!");
            return false;
        }
        return true;
    }
}
