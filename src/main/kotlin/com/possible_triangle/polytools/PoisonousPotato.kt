package com.possible_triangle.polytools

import com.possible_triangle.polytools.extensions.PreventableAgeEntity
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

object PoisonousPotato {

    fun onUse(
        player: Player,
        level: Level,
        hand: InteractionHand,
        entity: Entity,
        hit: EntityHitResult?,
    ): InteractionResult {
        val stack = player.getItemInHand(hand)

        if (!stack.`is`(Items.POISONOUS_POTATO)) return InteractionResult.PASS
        if (entity !is AgeableMob || entity !is PreventableAgeEntity) return InteractionResult.PASS
        if (!entity.isBaby) return InteractionResult.FAIL
        if (entity.`polytools$isAgingPrevented`()) return InteractionResult.FAIL

        entity.playSound(SoundEvents.GENERIC_EAT, 0.5F, 0.25F)
        level.addParticle(ParticleTypes.ENTITY_EFFECT, entity.x, entity.y, entity.z, 0.2, 0.8, 0.0)

        if (!player.abilities.instabuild) {
            stack.shrink(1)
        }

        entity.`polytools$preventAging`()

        return InteractionResult.CONSUME

    }

}