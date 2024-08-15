package com.possible_triangle.polytools.block

import com.possible_triangle.polytools.modules.Tools.CUT_VINES
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.VineBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult

class CutVinesBlock : VineBlock(Properties.ofFullCopy(Blocks.VINE)), PolymerBlock {

    override fun getPolymerBlockState(state: BlockState) = Blocks.VINE.withPropertiesOf(state)

    override fun isRandomlyTicking(blockState: BlockState) = false

    override fun randomTick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource,
    ) {
        // Nothing
    }

    companion object {
        fun checkBlock(state: BlockState): Boolean {
            return state.`is`(Blocks.VINE) || state.`is`(CUT_VINES)
        }

        fun onBlockUse(
            player: Player?,
            level: Level,
            hand: InteractionHand,
            hit: BlockHitResult,
        ): InteractionResult {
            if (player == null || player.isSpectator) return InteractionResult.PASS

            val state = level.getBlockState(hit.blockPos)
            if (!state.`is`(Blocks.VINE)) return InteractionResult.PASS

            val stack = player.getItemInHand(hand)
            if (!stack.`is`(Items.SHEARS)) return InteractionResult.PASS

            if (player is ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(player, hit.blockPos, stack)
            }

            level.playSound(player, hit.blockPos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0f, 1.0f)

            level.setBlockAndUpdate(hit.blockPos, Blocks.AIR.defaultBlockState())

            val cutState = CUT_VINES.withPropertiesOf(state)
            level.setBlockAndUpdate(hit.blockPos, cutState)
            level.gameEvent(GameEvent.BLOCK_CHANGE, hit.blockPos, GameEvent.Context.of(player, cutState))

            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand))

            return InteractionResult.SUCCESS
        }
    }

}
