package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType

open class PolymerStairBlock(
    properties: Properties,
    base: BlockState,
    private val virtualBlock: Block,
) :
    StairBlock(base, properties), PolymerBlock {

    override fun getPolymerBlock(state: BlockState) = virtualBlock

    override fun getPolymerBlockState(state: BlockState): BlockState {
        return virtualBlock.withPropertiesOf(state)
    }

}