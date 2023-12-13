package xyz.gameoholic.lumbergame.game.mob;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class Mob {
    private LumberGamePlugin plugin;
    private MobType mobType;
    private int CR; // Challenge Rating
    private org.bukkit.entity.Mob mob;
    private UUID entityId = UUID.randomUUID();

    public static Map<UUID, Mob> mobs = new HashMap();

    public Mob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        this.plugin = plugin;
        this.mobType = mobType;
        this.CR = CR;

        mob = (org.bukkit.entity.Mob) location.getWorld().spawnEntity(location, mobType.entityType());

        int health = (int) Math.min(2000, new ExpressionBuilder(mobType.healthExpression())
            .variables("CR")
            .build()
            .setVariable("CR", CR).evaluate()); // Health cannot be above 2,048 in MC

        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        mob.setHealth(health);


        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
            new ExpressionBuilder(mobType.playerDamageExpression())
                .variables("CR")
                .build()
                .setVariable("CR", CR).evaluate()
        );

        mob.setCustomNameVisible(true);
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text((int) mob.getHealth())),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));

        mob.getPersistentDataContainer().set(new NamespacedKey(plugin, "entity_id"), PersistentDataType.STRING, entityId.toString());

        mobs.put(entityId, this);
    }

    public MobType getMobType() {
        return mobType;
    }

    public int getCR() {
        return CR;
    }

    public org.bukkit.entity.Mob getMob() {
        return mob;
    }

    public UUID getEntityId() {
        return entityId;
    }
}
