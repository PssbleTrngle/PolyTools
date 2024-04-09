package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.SimplePolymerBlock
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.LeavesBlock.WATERLOGGED
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.WeatheringCopper.WeatherState
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

class PolymerGrateBlock(properties: Properties, virtual: Block, private val state: WeatherState) :
    SimplePolymerBlock(properties, virtual), SimpleWaterloggedBlock {

    init {
        this.registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false))
    }

    override fun getPolymerBlockState(state: BlockState): BlockState {
        return super.getPolymerBlockState(state).setValue(LeavesBlock.DISTANCE, this.state.ordinal + 2)
            .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED)
    }

    override fun getFluidState(blockState: BlockState): FluidState {
        return if (blockState.getValue(WATERLOGGED) as Boolean) Fluids.WATER.getSource(false)
        else super.getFluidState(blockState)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        val fluidState = blockPlaceContext.level.getFluidState(blockPlaceContext.clickedPos)
        val blockState = defaultBlockState().setValue(WATERLOGGED,fluidState.type === Fluids.WATER)
        return blockState
    }

}