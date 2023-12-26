package xyz.gameoholic.lumbergame

import org.bukkit.Location
import org.bukkit.Particle
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticleBuilder
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.LinearEnvelope
import xyz.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.loop.BounceLoop
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.EnvelopeTriple
import xyz.gameoholic.partigon.util.Utils.envelope
import xyz.gameoholic.partigon.util.rotation.RotationOptions
import xyz.gameoholic.partigon.util.rotation.RotationType

object TreeLevelUpParticle {
    fun getParticle(location: Location) = multiParticle {
        fun builder() =
            singularParticleBuilder {
                particleType = Particle.VILLAGER_HAPPY
                animationFrameAmount = 2
                maxFrameAmount = 100
                originLocation = ConstantLocation(location)

                circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.POSITION,
                    EnvelopePair(0.0.envelope, 0.0.envelope), // center
                    TrigonometricEnvelope(
                        4.0.envelope,
                        3.0.envelope,
                        TrigonometricEnvelope.TrigFunc.COS,
                        BounceLoop(100)
                    ), // radius
                    RepeatLoop(100)
                ).add()

                circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.OFFSET,
                    EnvelopePair(0.0.envelope, 0.0.envelope), // center
                    LinearEnvelope(3.0.envelope, 0.0.envelope, BounceLoop(100)), // radius
                    RepeatLoop(100),
                    listOf(
                        RotationOptions(
                            EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                            180.0.envelope, RotationType.Y_AXIS
                        )
                    )
                ).add()

                positionY = LinearEnvelope(0.0.envelope, 6.0.envelope, RepeatLoop(100))

                extra = 0.05.envelope
            }


        repeat(6) {
            builder().apply {
                RotationOptions(
                    EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    (it * 60.0).envelope, RotationType.Y_AXIS
                ).addToGroups()
            }.build().add()
        }
    }
}