package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockSetType

open class PolymerTrapDoorBlock(
    properties: Properties,
    blockSet: BlockSetType,
    private val virtualBlock: Block,
    private val powered: Boolean = true
) :
    TrapDoorBlock(properties, blockSet), PolymerBlock {

    override fun getPolymerBlock(state: BlockState) = virtualBlock

    override fun getPolymerBlockState(state: BlockState): BlockState {
        return virtualBlock.withPropertiesOf(state).setValue(POWERED, powered)
    }

}