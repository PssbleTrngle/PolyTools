package com.possible_triangle.polytools.gui

import com.possible_triangle.polytools.Biomes
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

data class PaintEntry(val name: String, val biome: RegistryKey<Biome>, val block: Block, val display: Text) {
    companion object {
        const val BASE_DATA = 128912
    }
}

class PaintGui(player: ServerPlayerEntity, canTake: Boolean, private val select: (PaintEntry) -> Unit) :
    SimpleGui(ScreenHandlerType.GENERIC_9X5, player, canTake) {

    companion object {
        fun open(player: ServerPlayerEntity, entries: List<PaintEntry>, pos: BlockPos) {
            val gui = PaintGui(player, false) { entry ->
                Biomes.setBiome(pos, player.world, entry.biome)
            }
            gui.update(entries)
            gui.open()
        }
    }

    fun update(entries: List<PaintEntry>) {
        entries.forEachIndexed { i, entry ->
            val name = entry.display.copy().setStyle(Style.EMPTY.withItalic(false))
            setSlot(i,
                GuiElementBuilder
                    .from(ItemStack(entry.block))
                    .setName(name)
                    .hideFlags()
                    .setCustomModelData(PaintEntry.BASE_DATA + i)
                    .setCallback { _, _, _ ->
                        try {
                            select(entry)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        close()
                    }
            )
        }
    }

}