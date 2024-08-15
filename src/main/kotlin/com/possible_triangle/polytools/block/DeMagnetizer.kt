package com.possible_triangle.polytools.block

import com.mojang.serialization.MapCodec
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.modules.Tools
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HopperBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.ENABLED

class DeMagnetizer : BaseEntityBlock(Properties.ofFullCopy(Blocks.LODESTONE)), PolymerBlock {

    companion object {
        private val CODEC: MapCodec<DeMagnetizer> = simpleCodec { DeMagnetizer() }
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(ENABLED, true))
    }

    override fun codec() = CODEC

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DeMagnetizerTile(pos, state)
    }

    override fun getPolymerBlockState(state: BlockState?) = Blocks.LODESTONE.defaultBlockState()

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(ENABLED)
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

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean,
    ) {
        if (!blockState2.`is`(blockState.block)) {
            this.checkPoweredState(level, blockPos, blockState, 2)
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean,
    ) {
        checkPoweredState(level, blockPos, blockState, 4)
    }

    private fun checkPoweredState(level: Level, blockPos: BlockPos, blockState: BlockState, i: Int) {
        val powered = level.hasNeighborSignal(blockPos)
        if (powered == blockState.getValue(HopperBlock.ENABLED)) {
            level.setBlock(blockPos, blockState.setValue(HopperBlock.ENABLED, !powered), i)
        }
    }

}