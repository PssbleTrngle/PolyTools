package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock
import net.minecraft.world.level.block.state.BlockState

class PolymerGrassBlock(properties: Properties, private val virtual: Block) : SpreadingSnowyDirtBlock(properties),
    PolymerBlock {

    override fun getPolymerBlock(state: BlockState): Block {
        return virtual
    }

    override fun getPolymerBlockState(state: BlockState): BlockState {
        return virtual.defaultBlockState().setValue(SNOWY, true)
    }

}
