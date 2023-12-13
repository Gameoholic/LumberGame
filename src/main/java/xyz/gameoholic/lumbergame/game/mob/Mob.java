package xyz.gameoholic.lumbergame.game.mob;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import xyz.gameoholic.lumbergame.LumberGamePlugin;

import static net.kyori.adventure.text.Component.text;

public class Mob {
    private LumberGamePlugin plugin;
    private MobType mobType;
    private int CR; // Challenge Rating
    private org.bukkit.entity.Mob mob;

    public Mob(LumberGamePlugin plugin, MobType mobType, int CR, Location location) {
        this.plugin = plugin;
        this.mobType = mobType;
        this.CR = CR;

        mob = (org.bukkit.entity.Mob) location.getWorld().spawnEntity(location, mobType.entityType());
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
            new ExpressionBuilder(mobType.healthExpression())
                .variables("CR")
                .build()
                .setVariable("CR", CR).evaluate()
        );
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
            new ExpressionBuilder(mobType.playerDamageExpression())
                .variables("CR")
                .build()
                .setVariable("CR", CR).evaluate()
        );

        mob.setCustomNameVisible(true);
        mob.customName(MiniMessage.miniMessage().deserialize(plugin.getLumberConfig().strings().mobDisplayname(),
            Placeholder.component("cr", text(CR)),
            Placeholder.component("health", text(mob.getHealth())),
            Placeholder.component("name", MiniMessage.miniMessage().deserialize(mobType.displayName()))
        ));


    }
}
