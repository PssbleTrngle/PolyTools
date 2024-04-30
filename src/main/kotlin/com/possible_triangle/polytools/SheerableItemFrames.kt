package com.possible_triangle.polytools

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

object SheerableItemFrames {

    fun useOn(
        player: Player,
        level: Level,
        hand: InteractionHand,
        entity: Entity,
        hit: EntityHitResult?,
    ): InteractionResult {
        val stack = player.getItemInHand(hand)
        if (!stack.`is`(Items.SHEARS)) return InteractionResult.PASS
        if (!player.isCrouching) return InteractionResult.PASS

        return if (entity is ItemFrame && !entity.isInvisible) {
            entity.isInvisible = true
            level.playSound(player, entity.pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1F, 1F)
            InteractionResult.SUCCESS
        } else {
            InteractionResult.PASS
        }
    }

}