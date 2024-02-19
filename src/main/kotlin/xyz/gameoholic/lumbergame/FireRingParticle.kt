package xyz.gameoholic.lumbergame

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Entity
import xyz.gameoholic.partigon.particle.MultiParticle
import xyz.gameoholic.partigon.particle.MultiParticle.Companion.multiParticle
import xyz.gameoholic.partigon.particle.SingularParticle
import xyz.gameoholic.partigon.particle.SingularParticle.Companion.singularParticleBuilder
import xyz.gameoholic.partigon.particle.envelope.EnvelopeGroup
import xyz.gameoholic.partigon.particle.envelope.LinearEnvelope
import xyz.gameoholic.partigon.particle.envelope.TrigonometricEnvelope
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper
import xyz.gameoholic.partigon.particle.envelope.wrapper.CircleEnvelopeWrapper.circleEnvelopeGroup
import xyz.gameoholic.partigon.particle.location.ConstantLocation
import xyz.gameoholic.partigon.particle.location.EntityLocation
import xyz.gameoholic.partigon.particle.loop.BounceLoop
import xyz.gameoholic.partigon.particle.loop.RepeatLoop
import xyz.gameoholic.partigon.util.EnvelopePair
import xyz.gameoholic.partigon.util.EnvelopeTriple
import xyz.gameoholic.partigon.util.Utils.envelope
import xyz.gameoholic.partigon.util.rotation.RotationOptions
import xyz.gameoholic.partigon.util.rotation.RotationType

object FireRingParticle {
    fun getParticle(entity: Entity, yaw: Float) = multiParticle {
        fun builder() =
                singularParticleBuilder {
                    particleType = Particle.FLAME
                    animationFrameAmount = 2
                    originLocation = EntityLocation(entity)

                    circleEnvelopeGroup(
                            EnvelopeGroup.EnvelopeGroupType.POSITION,
                            EnvelopePair(0.0.envelope, 0.0.envelope), // center
                            0.9.envelope,
                            RepeatLoop(60)
                    ).add()

                    extra = 0.05.envelope
                }


        var newYaw: Float = yaw;
        if (newYaw <= 0)
            newYaw = Math.abs(newYaw);
        else {
            // todo: fix this
        }
        newYaw = newYaw;


        repeat(3) {
            builder().apply {
                // Rotation to be in correct yaw
                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                        90.envelope, RotationType.X_AXIS).addToGroups()

                RotationOptions(EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                        (newYaw).envelope, RotationType.Y_AXIS).addToGroups()

                // Duplicate 3 times symmetrically:
                RotationOptions(
                        EnvelopeTriple(0.0.envelope, 0.0.envelope, 0.0.envelope),
                        (it * 120.0).envelope, RotationType.X_AXIS // Z LOOKS COOL, Y LOOKS FINE, X EVEN COOLER
                ).addToGroups()
            }.build().add()
        }
    }
}