package com.possible_triangle.polytools.datagen

import com.possible_triangle.polytools.PolytoolsMod
import com.possible_triangle.polytools.gui.PaintEntry
import com.possible_triangle.polytools.item.ChiselItem
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class ChiseledModels(generator: FabricDataGenerator) : FabricModelProvider(generator) {

    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {

    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        ChiselItem.BLOCKS.forEach { (block, entries) ->
            val id = Registry.BLOCK.getId(block)
            val parent = Identifier(id.namespace, "block/${id.path}")
            val model = CustomModel(parent)

            entries.forEachIndexed { i, entry ->
                model.add(Identifier(PolytoolsMod.ID, entry),
                    PaintEntry.BASE_DATA + i,
                    Model(Optional.of(Identifier("block/cube_all")),
                        Optional.empty(),
                        TextureKey.TEXTURE), TextureKey.TEXTURE)
            }

            generator.register(block.asItem(), model)
        }
    }

}