package xyz.gameoholic.lumbergame

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import xyz.gameoholic.partigon.particle.MultiParticle
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.SingularParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticleBuilder
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.location.EntityLocation
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope

object DoubleJumpParticle {
    fun getParticle(player: Player) = multiParticle {

        val frameAmount = 20
        fun builder() = singularParticleBuilder {
            originLocation = EntityLocation(player)

            circleEnvelopeGroup(
                EnvelopeGroup.EnvelopeGroupType.POSITION,
                EnvelopePair(0.0.envelope, 0.0.envelope),
                1.0.envelope,
                RepeatLoop(frameAmount)
            ).add()

            circleEnvelopeGroup(
                EnvelopeGroup.EnvelopeGroupType.OFFSET,
                EnvelopePair(0.0.envelope, 0.0.envelope),
                1.0.envelope,
                RepeatLoop(frameAmount)
            ).add()

            offsetY = (-2.0).envelope

            extra = 0.125.envelope
        }

        // Ring particles
        builder().apply {
            animationFrameAmount = frameAmount
            animationInterval = 5 // Reanimate every 20 ticks

            particleType = Particle.SMOKE_NORMAL
        }.build().add()

        // Spiral particle
        builder().apply {
            animationFrameAmount = 1
            animationInterval = 1

            particleType = Particle.WHITE_SMOKE
        }.build().add()

    }
}