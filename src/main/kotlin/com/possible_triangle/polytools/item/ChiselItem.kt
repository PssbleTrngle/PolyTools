package com.possible_triangle.polytools.item

import com.possible_triangle.polytools.Biomes
import com.possible_triangle.polytools.PolytoolsMod
import com.possible_triangle.polytools.gui.PaintEntry
import com.possible_triangle.polytools.gui.PaintGui
import net.minecraft.block.Blocks
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult

class ChiselItem : ModelledPolymerItem(Settings(), Items.STICK, 3) {

    companion object {
        val BLOCKS = mapOf(
            Blocks.BRICKS to listOf(
                "sandy_bricks",
                "broken_bricks",
            )
        )
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val user = context.player
        if (user !is ServerPlayerEntity) return super.useOnBlock(context)

        val state = context.world.getBlockState(context.blockPos)
        return if (BLOCKS.contains(state.block)) {
            val entries = BLOCKS[state.block]!!.map {
                val biome = Biomes.CHISEL_BIOMES[it] ?: throw NullPointerException("PaintEntry biome missing")
                PaintEntry(it, biome, state.block, TranslatableText("${PolytoolsMod.ID}.block.$name"))
            }

            PaintGui.open(user, entries, context.blockPos)
            ActionResult.SUCCESS
        } else {
            ActionResult.FAIL
        }

    }

}