package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.modules.Tools
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class DeMagnetizer : BaseEntityBlock(Properties.copy(Blocks.LODESTONE)), PolymerBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DeMagnetizerTile(pos, state)
    }

    override fun getPolymerBlock(state: BlockState): Block {
        return Blocks.LODESTONE
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != Tools.DEMAGNETIZER_TILE) return null
        if (level.isClientSide) return null
        return BlockEntityTicker(DeMagnetizerTile::serverTick) as BlockEntityTicker<T>
    }

}