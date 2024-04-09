package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.item.MagnetItem.Companion.IGNORE_TAG
import com.possible_triangle.polytools.modules.Tools
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB

class DeMagnetizerTile(pos: BlockPos, state: BlockState) : BlockEntity(Tools.DEMAGNETIZER_TILE, pos, state) {

    companion object {

        const val RANGE = 8.0

        fun serverTick(level: Level, pos: BlockPos, state: BlockState, tile: DeMagnetizerTile) {
            ++tile.ticks

            if (level.gameTime % 10L == 0L) {
                val box = AABB(pos).inflate(RANGE)
                val items = level.getEntitiesOfClass(ItemEntity::class.java, box) {
                    !it.tags.contains(IGNORE_TAG)
                }

                items.forEach {
                    it.addTag(IGNORE_TAG)
                }
            }
        }
    }

    private var ticks = 0

}