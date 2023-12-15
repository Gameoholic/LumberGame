package xyz.gameoholic.lumbergame.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.mob.Mob;
import xyz.gameoholic.lumbergame.util.MobUtil;

import javax.annotation.Nullable;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class EntityDamageListener implements Listener {
    private LumberGamePlugin plugin;
    public EntityDamageListener(LumberGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        @Nullable Mob mob = plugin.getGameManager().getWaveManager().getMob(e.getEntity().getUniqueId());
        if (mob == null)
            return;

        mob.onTakeDamage();
    }


}
