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
        @Nullable Mob mob = getMob(e.getEntity());
        if (mob == null)
            return;

        org.bukkit.entity.Mob bukkitMob = (org.bukkit.entity.Mob) e.getEntity();

        bukkitMob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(mob.getCR())),
            Placeholder.component("health", text((int) bukkitMob.getHealth())),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mob.getMobType().displayName()))
        ));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        @Nullable Mob mob = getMob(e.getEntity());
        if (mob == null)
            return;

        Mob.mobs.remove(mob.getMob().getUniqueId());
    }

    /**
     * Provided a mob entity, returns the Lumber mob instance for which it belongs, null if not found.
     */
    private @Nullable Mob getMob(Entity entity) {
        @Nullable String entityId = entity.getPersistentDataContainer()
            .get(new NamespacedKey(plugin, "entity_id"), PersistentDataType.STRING);
        if (entityId == null)
            return null;

        @Nullable Mob mob = Mob.mobs.get(UUID.fromString(entityId));
        if (mob == null)
            return null;

        if (!(entity instanceof org.bukkit.entity.Mob))
            return null;

        return mob;
    }
}
