package com.possible_triangle.polytools.block.tile

import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.entity.BlockEntity

fun BlockEntity.sound(event: SoundEvent) {
    level?.playSound(null, blockPos, event, SoundSource.BLOCKS, 1F, 1F)
}

