package com.possible_triangle.polytools.block

import eu.pb4.polymer.core.api.block.PolymerBlockUtils
import eu.pb4.polymer.core.api.block.SimplePolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class PolymerPortalBlock(properties: Properties, virtual: Block, private val type: BlockEntityType<*>) : SimplePolymerBlock(properties, virtual) {

    override fun onPolymerBlockSend(blockState: BlockState, pos: BlockPos.MutableBlockPos, player: ServerPlayer) {
        val packet = PolymerBlockUtils.createBlockEntityPacket(pos, type, CompoundTag())
        player.connection.send(packet)
    }

}