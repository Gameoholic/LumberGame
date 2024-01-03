package xyz.gameoholic.lumbergame

import org.bukkit.Location
import org.bukkit.Particle
import xyz.gameoholic.partigon.particle.MultiParticle
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticle
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.Utils.envelope

object DoubleJumpLandParticle {
    fun getParticle(location: Location, radius: Double) = multiParticle {
        singularParticle {
            val frameAmount = (30 * (radius / 2.0)).toInt() // Default particle amount is 20, but if radius doubles, we double it as well
            animationFrameAmount = frameAmount
            maxFrameAmount = frameAmount

            particleType = Particle.SMOKE_LARGE
            originLocation = ConstantLocation(location)

            circleEnvelopeGroup(
                EnvelopeGroup.EnvelopeGroupType.POSITION,
                EnvelopePair(0.0.envelope, 0.0.envelope),
                (radius / 1.25).envelope,
                RepeatLoop(frameAmount)
            ).add()

            circleEnvelopeGroup(
                EnvelopeGroup.EnvelopeGroupType.OFFSET,
                EnvelopePair(0.0.envelope, 0.0.envelope),
                (radius / 2.0).envelope,
                RepeatLoop(frameAmount)
            ).add()

            extra = 0.125.envelope
        }.add()
    }
}