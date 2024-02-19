package xyz.gameoholic.lumbergame.game.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.ItemUtil;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public abstract class SpecialItem {
    protected final LumberGamePlugin plugin;
    private final BukkitTask task;
    protected final UUID ownerUUID;
    private final ItemStack itemStack;
    protected int cooldown;
    private int currentCooldownRemaining;
    protected String ammoItemId;
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
    }

    /**
     * Called when the player attempts to use this item, regardless of whether they can.
     */
    public void onAttemptUse() {
        @Nullable Player owner = Bukkit.getPlayer(ownerUUID);

        if (currentCooldownRemaining > 0) {
            return;
        }
        if (owner == null || !ItemUtil.removeItemsFromInventory(plugin, owner, Map.of("FIRE_CHARGE", 1))) // Use up ammo
            return;
        currentCooldownRemaining = cooldown;
        activateItem();
    }

    /**
     * Called when item is activated successfully.
     */
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
            destroy();
            return false;
        }
        return true;
    }

    private void destroy() {
        task.cancel();
    }
}
