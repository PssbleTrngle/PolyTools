package com.possible_triangle.polytools.item

import com.possible_triangle.polytools.gui.PaintEntry
import com.possible_triangle.polytools.gui.PaintGui
import net.minecraft.block.Blocks
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.world.biome.BiomeKeys

class BrushItem : ModelledPolymerItem(Settings(), Items.STICK, 3) {

    companion object {
        private val BIOMES = mapOf(
            Blocks.GRASS_BLOCK to BiomeKeys.PLAINS,
            Blocks.SAND to BiomeKeys.DESERT,
            Blocks.MYCELIUM to BiomeKeys.MUSHROOM_FIELDS,
        ).map {
            val display = TranslatableText("biome.${it.value.value.namespace}.${it.value.value.path}")
            PaintEntry("", it.value, it.key, display)
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val user = context.player
        if (user !is ServerPlayerEntity) return super.useOnBlock(context)

        PaintGui.open(user, BIOMES, context.blockPos)
        return ActionResult.SUCCESS

    }

}