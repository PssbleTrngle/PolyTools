package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.block.tile.VaultTile
import com.possible_triangle.polytools.modules.Backport
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LeavesBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult

class Vault : BaseEntityBlock(Properties.copy(Blocks.BEDROCK)), PolymerBlock {

    enum class State : StringRepresentable {
        ACTIVE, INACTIVE;

        override fun getSerializedName() = name.lowercase()
    }

    companion object {
        val STATE = EnumProperty.create("vault_state", State::class.java)
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(STATE, State.INACTIVE))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(STATE, HORIZONTAL_FACING)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return VaultTile(pos, state)
    }

    override fun getPolymerBlock(state: BlockState): Block {
        return Blocks.DARK_OAK_LEAVES
    }

    override fun getPolymerBlockState(state: BlockState, player: ServerPlayer?): BlockState {
        val distance = if (state.getValue(STATE) == State.ACTIVE) 3 else 2
        return super.getPolymerBlockState(state, player).setValue(LeavesBlock.DISTANCE, distance)
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

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        if (type != Backport.VAULT_TILE) return null
        if (level.isClientSide) return null
        return BlockEntityTicker(VaultTile::serverTick) as BlockEntityTicker<T>
    }

}