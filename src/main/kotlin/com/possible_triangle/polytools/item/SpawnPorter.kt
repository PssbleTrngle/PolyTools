package com.possible_triangle.polytools.item

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level

class SpawnPorter : ModelledPolymerItem(Properties().rarity(Rarity.EPIC).durability(3), Items.BRUSH, 0) {

    override fun use(
        level: Level,
        player: Player,
        hand: InteractionHand,
    ): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)

        stack.hurtAndBreak(1, player) {
            it.broadcastBreakEvent(hand)
        }

        if (player is ServerPlayer) respawn(player)

        return InteractionResultHolder.consume(stack)
    }

    companion object {
        fun respawn(player: ServerPlayer) {
            player.connection.player = player.server.playerList.respawn(player, true)
        }
    }

}