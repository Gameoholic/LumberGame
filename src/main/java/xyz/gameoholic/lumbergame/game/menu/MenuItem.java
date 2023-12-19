package xyz.gameoholic.lumbergame.game.menu;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import javax.annotation.Nullable;

public class MenuItem {
    protected final ItemStack item;
    protected final String id;
    public MenuItem(LumberGamePlugin plugin, String id) {
        item = plugin.getItemManager().getItem(id);
        this.id = id;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getId() {
        return id;
    }
}
