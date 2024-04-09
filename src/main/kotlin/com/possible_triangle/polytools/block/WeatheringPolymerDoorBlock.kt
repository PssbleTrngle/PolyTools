package com.possible_triangle.polytools.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.HoneycombItem
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WeatheringCopper
import net.minecraft.world.level.block.WeatheringCopper.WeatherState
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf

class WeatheringPolymerDoorBlock(
    properties: Properties,
    blockSet: BlockSetType,
    virtualBlock: Block,
    private val state: WeatherState,
) :
    PolymerDoorBlock(properties, blockSet, virtualBlock), WeatheringCopper {

    override fun randomTick(
        state: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource,
    ) {
        this.onRandomTick(state, serverLevel, blockPos, randomSource)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER && WeatheringCopper.getNext(state.block).isPresent
    }

    override fun getAge() = state

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos,
    ): BlockState {
        val changing = WeatheringCopper.getNext(this).filter(neighborState::`is`).isPresent
                || WeatheringCopper.getPrevious(this).filter(neighborState::`is`).isPresent
                || neighborState.`is`(HoneycombItem.WAXABLES.get().get(this))
                || neighborState.`is`(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(this))

        return if (changing) {
            neighborState.block.withPropertiesOf(super.updateShape(state, direction, withPropertiesOf(neighborState), level, pos, neighborPos))
        } else {
            super.updateShape(state, direction, neighborState, level, pos, neighborPos)
        }
    }

}