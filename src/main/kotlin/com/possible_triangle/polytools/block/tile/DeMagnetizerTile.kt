package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.item.MagnetItem.Companion.IGNORE_TAG
import com.possible_triangle.polytools.modules.Tools
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.ENABLED
import net.minecraft.world.phys.AABB

class DeMagnetizerTile(pos: BlockPos, state: BlockState) : BlockEntity(Tools.DEMAGNETIZER_TILE, pos, state) {

    companion object {

        const val DEFAULT_RANGE = 4.0

        fun serverTick(level: Level, pos: BlockPos, state: BlockState, tile: DeMagnetizerTile) = with(tile) {
            if(!state.getValue(ENABLED)) return

            if (level.gameTime % 10L == 0L) {
                val box = AABB(pos).inflate(range)
                val items = level.getEntitiesOfClass(ItemEntity::class.java, box) {
                    !it.tags.contains(IGNORE_TAG)
                }

                items.forEach {
                    it.addTag(IGNORE_TAG)
                }
            }
        }
    }

    private var customRange: Double? = null
    val range get() = customRange ?: DEFAULT_RANGE

    override fun saveAdditional(nbt: CompoundTag, lookup: HolderLookup.Provider) {
        super.saveAdditional(nbt, lookup)
        customRange?.let { nbt.putDouble("custom_range", it) }
    }

    override fun loadAdditional(nbt: CompoundTag, lookup: HolderLookup.Provider) {
        super.loadAdditional(nbt, lookup)
        customRange = nbt.getDouble("custom_range").takeIf { it > 0 }
    }

}