package xyz.gameoholic.lumbergame.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.util.ItemUtil;

import javax.annotation.Nullable;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class GoldVaultManager {
    private LumberGamePlugin plugin;
    private int goldDeposited = 0;
    private BukkitTask goldTask;
    /**
     * Location of gold vault.
     */
    private Location loc;
    /**
     * Displays information about the vault. 1.5 blocks above the gold vault location.
     */
    private TextDisplay textDisplay;
    private int ticksPassed = 0;

    public GoldVaultManager(LumberGamePlugin plugin) {
        this.plugin = plugin;
        loc = plugin.getLumberConfig().mapConfig().goldVaultLocation();

        Location textLoc = loc.clone();
        textLoc.setY(loc.getY() + 1.5);
        textDisplay = (TextDisplay) textLoc.getWorld().spawnEntity(textLoc, EntityType.TEXT_DISPLAY);
        updateTextDisplayText();
        textDisplay.setBillboard(Display.Billboard.FIXED);
        textDisplay.setRotation(180f, 0f);
        textDisplay.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN, true);
        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(0.9, 0.9, 0.9);
        textDisplay.setTransformation(transformation);

        goldTask = new BukkitRunnable() {
            @Override
            public void run() {
                onTick();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void onTick() {
        depositGold();
        collectGold();
        updateTextDisplayText();
        ticksPassed++;
    }

    private void collectGold() {
        for (Player nearPlayer : loc.getNearbyEntitiesByType(Player.class, 1.5f, 0.1f)) {
            // Player must be shifting to collect gold
            if (!nearPlayer.isSneaking())
                continue;
            if (goldDeposited == 0)
                return;
            nearPlayer.getInventory().addItem(ItemUtil.getGoldItem(plugin));
            goldDeposited--;

            nearPlayer.playSound(plugin.getLumberConfig().soundsConfig().goldVaultCollectSound(), loc.getX(), loc.getY(), loc.getZ());
        }
    }

    private void depositGold() {
        for (Item droppedItem : loc.getNearbyEntitiesByType(Item.class, 1.5f, 0.1f)) {
            ItemStack itemStack = droppedItem.getItemStack();
            @Nullable UUID dropperUUID = droppedItem.getThrower();
            @Nullable Player dropper = null;
            if (dropperUUID != null) {
                dropper = Bukkit.getPlayer(dropperUUID);
            }
            // If item isn't gold, destroy it
            if (!ItemUtil.isLumberItem(plugin, itemStack, "GOLD")) {
                // Return item to owner, if there is one
                if (dropper != null)
                    dropper.getInventory().addItem(itemStack);
                droppedItem.remove();
            }
            // Else, deposit it
            else {
                if (dropper != null) {
                    Location loc = plugin.getLumberConfig().mapConfig().goldVaultLocation();
                    dropper.playSound(plugin.getLumberConfig().soundsConfig().goldVaultDepositSound(), loc.getX(), loc.getY(), loc.getZ());
                }
                goldDeposited += itemStack.getAmount();
                droppedItem.remove();
            }
        }
    }

    private void updateTextDisplayText() {
        int colorTransitionSpeedMultiplier = 5;
        textDisplay.text(
                MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().goldVaultText(),
                        Placeholder.component("gold_deposited", text(goldDeposited)),
                        Placeholder.parsed("color_transition", -1 + (ticksPassed * colorTransitionSpeedMultiplier % 200) / 100.0  + "")
                )
        );
    }

    /**
     * Cleans up resources (task).
     */
    public void cleanup() {
        goldTask.cancel();
    }

}
