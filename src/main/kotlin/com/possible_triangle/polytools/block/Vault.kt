package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.Content
import com.possible_triangle.polytools.block.tile.VaultTile
import eu.pb4.polymer.core.api.block.PolymerBlock
import eu.pb4.polymer.core.impl.networking.ServerPackets
import eu.pb4.polymer.networking.api.PolymerServerNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RespawnAnchorBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class Vault : BaseEntityBlock(Properties.copy(Blocks.BEDROCK)), PolymerBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VaultTile(pos, state)
    }

    override fun getPolymerBlock(state: BlockState): Block {
        return Blocks.RESPAWN_ANCHOR
    }

    override fun use(
        blockState: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        val tile = level.getBlockEntity(pos)
        if (tile !is VaultTile) return InteractionResult.PASS

        val stack = player.getItemInHand(hand)
        return tile.unlock(player, stack)
    }

    override fun onPolymerBlockSend(polymerState: BlockState, pos: BlockPos.MutableBlockPos, player: ServerPlayer?) {
        if (player == null) return
        val tile = player.level().getBlockEntity(pos)
        if (tile !is VaultTile) return

        val protocolVersion =
            PolymerServerNetworking.getSupportedVersion(player.connection, ServerPackets.WORLD_SET_BLOCK_UPDATE)
        val buf = PolymerServerNetworking.buf(protocolVersion)

        val charge = if (tile.hasUnlocked(player)) RespawnAnchorBlock.MAX_CHARGES.minus(1) else 0
        val state = getPolymerBlockState(polymerState, player)
            .setValue(RespawnAnchorBlock.CHARGE, charge)

        buf.writeBlockPos(pos)
        buf.writeVarInt(BLOCK_STATE_REGISTRY.getId(state))

        player.connection.send(ClientboundCustomPayloadPacket(ServerPackets.WORLD_SET_BLOCK_UPDATE, buf))
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != Content.VAULT_TILE) return null
        if (level.isClientSide) return null
        return BlockEntityTicker(VaultTile::serverTick) as BlockEntityTicker<T>
    }

}