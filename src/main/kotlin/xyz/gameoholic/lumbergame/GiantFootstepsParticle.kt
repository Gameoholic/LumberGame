package xyz.gameoholic.lumbergame

import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticle
import xyz.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Particle
import org.bukkit.entity.Entity
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.location.EntityLocation

object GiantFootstepsParticle {

    fun getParticle(entity: Entity) = multiParticle {
        singularParticle {
            maxFrameAmount = 2
            particleType = Particle.SMOKE_NORMAL
            originLocation = EntityLocation(entity)

            offsetX = 1.0.envelope
            offsetY = 0.0.envelope
            offsetZ = 1.0.envelope

            extra = 0.2.envelope

            count = 10.envelope
        }.add()

        singularParticle {
            maxFrameAmount = 2
            particleType = Particle.EXPLOSION_HUGE
            originLocation = EntityLocation(entity)

            offsetX = 0.0.envelope
            offsetY = 0.0.envelope
            offsetZ = 0.0.envelope

            extra = 0.2.envelope

            count = 1.envelope
        }.add()
    }


}