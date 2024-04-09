package com.possible_triangle.polytools.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WeatheringCopper
import net.minecraft.world.level.block.WeatheringCopper.WeatherState
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType

class WeatheringPolymerTrapdoorBlock(
    properties: Properties,
    blockSet: BlockSetType,
    virtualBlock: Block,
    private val state: WeatherState,
) :
    PolymerTrapDoorBlock(properties, blockSet, virtualBlock), WeatheringCopper {

    override fun randomTick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource,
    ) {
        this.onRandomTick(blockState, serverLevel, blockPos, randomSource)
    }

    override fun isRandomlyTicking(blockState: BlockState): Boolean {
        return WeatheringCopper.getNext(blockState.block).isPresent
    }

    override fun getAge() = state

}