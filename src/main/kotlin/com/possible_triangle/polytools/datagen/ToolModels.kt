package com.possible_triangle.polytools.datagen

import com.possible_triangle.polytools.Content
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.*

class ToolModels(generator: FabricDataGenerator) : FabricModelProvider(generator) {

    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {}

    override fun generateItemModels(generator: ItemModelGenerator) {
        val customModels = listOf(Content.MAGNET, Content.WRENCH, Content.CHISEL).groupBy {
            val stack = ItemStack(it)
            it.getPolymerItem(stack, null)
        }

        customModels.forEach { (fake, imposters) ->
            val parent = Identifier("item/generated")
            val model = CustomModel(parent, TextureKey.LAYER0)

            imposters.forEach {
                val id = Registry.ITEM.getId(it)
                val data = it.getPolymerCustomModelData(ItemStack(it), null)
                model.add(id, data, Model(Optional.of(parent), Optional.empty(), TextureKey.LAYER0), TextureKey.LAYER0)
            }

            generator.register(fake, fake, model)
        }
    }

}