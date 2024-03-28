package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.block.tile.TickingTile
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

interface TickingBlock<T : TickingTile> {

    val blockEntityType: BlockEntityType<T>

    fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != blockEntityType) return null
        if (level.isClientSide) return null
        return BlockEntityTicker { level, pos, state, tile: T ->
            if (tile !is TickingTile) return@BlockEntityTicker
            if (level !is ServerLevel) return@BlockEntityTicker
            tile.serverTick(level, pos, state)
        }
    }

}