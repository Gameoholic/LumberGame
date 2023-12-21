package xyz.gameoholic.lumbergame

import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticle
import xyz.gameoholic.partigon.particle.envelope.LinearEnvelope
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.Utils.envelope
import org.bukkit.Location
import org.bukkit.Particle
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle

object TreeDeadParticle {

    fun getParticle(location: Location) = multiParticle {
        singularParticle {
            particleType = Particle.SMOKE_NORMAL
            originLocation = ConstantLocation(location)

            offsetX = 1.0.envelope
            offsetY = 0.0.envelope
            offsetZ = 1.0.envelope

            extra = 0.2.envelope

            count = 100.envelope
        }.add()

        singularParticle {
            particleType = Particle.EXPLOSION_HUGE
            originLocation = ConstantLocation(location)

            offsetX = 0.0.envelope
            offsetY = 0.0.envelope
            offsetZ = 0.0.envelope

            extra = 0.2.envelope

            count = 1.envelope
        }.add()
    }


}