package com.possible_triangle.polytools

import eu.pb4.polymer.core.api.block.BlockMapper
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.HoneycombItem
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState

class ExtendedBlockMapper(private val base: BlockMapper) : BlockMapper {

    private val SNOWY_GRASS = Blocks.GRASS_BLOCK.defaultBlockState().setValue(SnowyDirtBlock.SNOWY, true)

    override fun getMapperName() = PolytoolsMod.ID

    override fun toClientSideState(state: BlockState, player: ServerPlayer?): BlockState {
        if (state.block is PolymerBlock) return base.toClientSideState(state, player)

        if (state.block is DoorBlock) return state.setValue(DoorBlock.POWERED, false)
        if (state.block is TrapDoorBlock) return state.setValue(TrapDoorBlock.POWERED, false)
        if (state.block is LeavesBlock) return state.setValue(LeavesBlock.DISTANCE, 1)

        if(state.`is`(Blocks.MYCELIUM)) return SNOWY_GRASS
        if(state.`is`(Blocks.PODZOL)) return SNOWY_GRASS

        val unwaxed = HoneycombItem.WAX_OFF_BY_BLOCK.get()[state.block]
        if (unwaxed != null) return unwaxed.withPropertiesOf(state)

        return state
    }

    override fun toClientSideBlock(block: Block, player: ServerPlayer?): Block {
        return base.toClientSideBlock(block, player)
    }

}