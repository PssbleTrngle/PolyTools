package com.possible_triangle.polytools.block

import com.mojang.serialization.MapCodec
import com.possible_triangle.polytools.block.tile.SkyConduitTile
import com.possible_triangle.polytools.modules.Multiblocks
import eu.pb4.polymer.core.api.block.PolymerHeadBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ConduitBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class SkyConduit : ConduitBlock(Properties.ofFullCopy(Blocks.CONDUIT)), PolymerHeadBlock, TickingBlock<SkyConduitTile> {

    companion object {
        private val CODEC: MapCodec<ConduitBlock> = simpleCodec { SkyConduit() }
    }

    override val blockEntityType get() = Multiblocks.SKY_CONDUIT_TILE

    override fun codec() = CODEC

    override fun getPolymerSkinValue(state: BlockState, pos: BlockPos, player: ServerPlayer?): String {
        return "ewogICJ0aW1lc3RhbXAiIDogMTY1MTQzNDY3OTY4OCwKICAicHJvZmlsZUlkIiA6ICJhOGJhMGY1YTFmNjQ0MTgzODZkZGI3OWExZmY5ZWRlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmVlcGVyOTA3NSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYTgxODQwM2Y4OWFlNGE0NjhjODNiNDJiNDBmODk4NzZmNzNiMGFlMGM2ZmQ3YjczNTYwMTBmZDY2YTU2ZjUxIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SkyConduitTile(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        return super<TickingBlock>.getTicker(level, state, type)
    }

}