package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockState

open class PolymerSlabBlock(
    properties: Properties,
    private val virtualBlock: Block,
) :
    SlabBlock(properties), PolymerBlock {

    override fun getPolymerBlock(state: BlockState) = virtualBlock

    override fun getPolymerBlockState(state: BlockState): BlockState {
        return virtualBlock.withPropertiesOf(state)
    }

}