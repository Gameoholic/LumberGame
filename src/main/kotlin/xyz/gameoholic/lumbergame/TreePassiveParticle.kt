package xyz.gameoholic.lumbergame

import org.bukkit.Location
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticleBuilder
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.EnvelopeTriple
import xyz.gameoholic.partigon.util.Utils.envelope
import xyz.gameoholic.partigon.util.rotation.RotationOptions
import xyz.gameoholic.partigon.util.rotation.RotationType

object TreePassiveParticle {
    fun getParticle(location: Location) = multiParticle {
        fun builder() =
            singularParticleBuilder {
                originLocation = ConstantLocation(location)

                circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.POSITION,
                    EnvelopePair(0.0.envelope, 0.0.envelope),
                    0.3.envelope,
                    RepeatLoop(100)
                ).add()

                circleEnvelopeGroup(
                    EnvelopeGroup.EnvelopeGroupType.OFFSET,
                    EnvelopePair(0.0.envelope, 0.0.envelope),
                    3.0.envelope,
                    RepeatLoop(100),
                    listOf(
                        RotationOptions(
                            EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                            180.0.envelope,
                            RotationType.Z_AXIS
                        )
                    )
                ).add()


                extra = 0.05.envelope
            }


        // Mirror all particles:
        repeat(3) {
            builder().apply {
                RotationOptions(
                    EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    (it * 120.0).envelope, RotationType.Y_AXIS
                ).addToGroups()
            }.build().add()
        }

        repeat(3) {
            builder().apply {
                RotationOptions(
                    EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    (it * 120.0).envelope, RotationType.Y_AXIS
                ).addToGroups()
                RotationOptions(
                    EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                    (180.0).envelope, RotationType.Z_AXIS
                ).addToGroups()
            }.build().add()
        }
    }
}