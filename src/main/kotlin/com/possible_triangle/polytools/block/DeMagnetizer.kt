package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.Content
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import eu.pb4.polymer.api.block.PolymerBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DeMagnetizer : BlockWithEntity(Settings.copy(Blocks.LODESTONE)), PolymerBlock {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DeMagnetizerTile(pos, state)
    }

    override fun getPolymerBlock(state: BlockState): Block {
        return Blocks.LODESTONE
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != Content.DEMAGNETIZER_TILE) return null
        if (world.isClient) return null
        return BlockEntityTicker(DeMagnetizerTile::serverTick) as BlockEntityTicker<T>
    }

}