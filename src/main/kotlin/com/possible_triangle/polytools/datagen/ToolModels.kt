package com.possible_triangle.polytools.datagen

import com.possible_triangle.polytools.Content
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplate
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.*

class ToolModels(private val output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {}

    override fun generateItemModels(generator: ItemModelGenerators) {
        val modeledItems = listOf(Content.MAGNET, Content.WRENCH, Content.SPAWN_PORTER, Content.TRIAL_KEY, Content.SKY_CORE)

        val customModels = modeledItems.groupBy {
            val stack = ItemStack(it)
            it.getPolymerItem(stack, null)
        }

        customModels.forEach { (fake, imposters) ->
            val parent = ResourceLocation("item/generated")
            val model = CustomModel(parent, TextureSlot.LAYER0)

            imposters.forEach {
                val id = BuiltInRegistries.ITEM.getKey(it)
                val data = it.getPolymerCustomModelData(ItemStack(it), null)
                model.add(
                    id,
                    data,
                    ModelTemplate(Optional.of(parent), Optional.empty(), TextureSlot.LAYER0),
                    TextureSlot.LAYER0
                )
            }

            generator.generateFlatItem(fake, model)
        }
    }

}