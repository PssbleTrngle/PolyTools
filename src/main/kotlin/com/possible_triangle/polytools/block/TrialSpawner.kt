package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.Content
import com.possible_triangle.polytools.block.tile.TrialSpawnerTile
import eu.pb4.polymer.core.api.block.PolymerBlock
import eu.pb4.polymer.core.api.block.PolymerBlockUtils
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.SpawnData
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.jvm.optionals.getOrNull

class TrialSpawner : BaseEntityBlock(Properties.copy(Blocks.BEDROCK)), PolymerBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return TrialSpawnerTile(pos, state)
    }

    override fun getPolymerBlock(state: BlockState): Block {
        return Blocks.SPAWNER
    }

    private fun getSpawnerPacket(pos: BlockPos, player: ServerPlayer): Packet<*>? {
        val tile = player.level().getBlockEntity(pos)
        if (tile !is TrialSpawnerTile) return null

        val spawnDataTag =
            SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, tile.spawnData ?: return null)
                .result().getOrNull() ?: return null

        val nbt = CompoundTag()

        nbt.putString("id", "minecraft:mob_spawner")
        nbt.put("SpawnData", spawnDataTag)
        nbt.putInt("x", pos.x)
        nbt.putInt("y", pos.y)
        nbt.putInt("z", pos.z)

        return PolymerBlockUtils.createBlockEntityPacket(pos, BlockEntityType.MOB_SPAWNER, nbt)
    }

    override fun onPolymerBlockSend(blockState: BlockState, pos: BlockPos.MutableBlockPos, player: ServerPlayer) {
        getSpawnerPacket(pos, player)?.let {
            player.connection.send(it)
        }
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != Content.TRIAL_SPAWNER_TILE) return null
        if (level.isClientSide) return null
        return BlockEntityTicker(TrialSpawnerTile::serverTick) as BlockEntityTicker<T>
    }

}