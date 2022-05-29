package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.Content
import com.possible_triangle.polytools.item.MagnetItem.Companion.IGNORE_TAG
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World

class DeMagnetizerTile(pos: BlockPos, state: BlockState) : BlockEntity(Content.DEMAGNETIZER_TILE, pos, state) {

    companion object {

        const val RANGE = 8.0

        fun serverTick(world: World, pos: BlockPos, state: BlockState, tile: DeMagnetizerTile) {
            ++tile.ticks

            if (world.time % 10L == 0L) {
                val box = Box(pos).expand(RANGE)
                val items = world.getEntitiesByClass(ItemEntity::class.java, box) {
                    !it.scoreboardTags.contains(IGNORE_TAG)
                }

                items.forEach {
                    it.addScoreboardTag(IGNORE_TAG)
                }
            }
        }
    }

    private var ticks = 0

}