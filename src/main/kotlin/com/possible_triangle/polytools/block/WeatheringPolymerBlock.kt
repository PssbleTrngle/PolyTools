package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.SimplePolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WeatheringCopper
import net.minecraft.world.level.block.WeatheringCopper.WeatherState
import net.minecraft.world.level.block.state.BlockState

open class WeatheringPolymerBlock(properties: Properties, virtual: Block, protected val state: WeatherState) :
    SimplePolymerBlock(properties, virtual), WeatheringCopper {

    override fun randomTick(
        state: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource,
    ) {
        this.onRandomTick(state, serverLevel, blockPos, randomSource)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return WeatheringCopper.getNext(state.block).isPresent
    }

    override fun getAge() = state


}