package com.possible_triangle.polytools.block.tile

import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.entity.BlockEntity

fun BlockEntity.sound(sound: Holder<SoundEvent>) {
    val level = level
    if (level is ServerLevel) {
        level.players().forEach {
            it.connection.send(
                ClientboundSoundPacket(
                    sound,
                    SoundSource.BLOCKS,
                    blockPos.x.toDouble(),
                    blockPos.y.toDouble(),
                    blockPos.z.toDouble(),
                    1F, 1F,
                    level.random.nextLong()
                )
            )

        }
    }
}

