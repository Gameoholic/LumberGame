package xyz.gameoholic.lumbergame.game.mob;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import org.bukkit.entity.Creeper;
import org.bukkit.persistence.PersistentDataType;
import xyz.gameoholic.lumbergame.LumberGamePlugin;
import xyz.gameoholic.lumbergame.game.goal.AttackTreeGoal;


import static net.kyori.adventure.text.Component.text;

public class Mob {
    private LumberGamePlugin plugin;
    private MobType mobType;
    private int CR; // Challenge Rating

    protected org.bukkit.entity.Mob mob;

    /**
     * Use MobSpawnerUtil to spawn, don't use this constructor directly.
     * @param mobType The Lumber MobType of the mob
     * @param CR The challenge rating to spawn the mob with
     * @param location The location to spawn the mob at
     */
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

        mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mobType.speed());

        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
            new ExpressionBuilder(mobType.damageExpression())
                .variables("CR")
                .build()
                .setVariable("CR", CR).evaluate()
        );

        mob.getPersistentDataContainer().set(new NamespacedKey(plugin, "lumber_mob"), PersistentDataType.BOOLEAN, true);

        mob.setCustomNameVisible(true);
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text((int) mob.getHealth())),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));
    }


    /**
     * Should be called when the mob takes damage.
     */
    public void onTakeDamage(double damageDealt) {
        updateMobCustomName(mob.getHealth() - damageDealt);
    }
    /**
     * Should be called when the mob dies.
     */
    public void onDeath() {
        plugin.getGameManager().getWaveManager().onMobDeath(this);
    }
    private void updateMobCustomName(double newHealth) {
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text((int) Math.max(newHealth, 0))),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));
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

}
