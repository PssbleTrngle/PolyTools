package com.possible_triangle.polytools.datagen

import com.possible_triangle.polytools.modules.Multiblocks
import com.possible_triangle.polytools.modules.Tools
import eu.pb4.polymer.core.api.item.PolymerItem
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.data.models.model.ModelTemplate
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.*

class ItemModels(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {}

    override fun generateItemModels(
        generator: ItemModelGenerators,
    ) {
        fun <T> customModel(
            fake: Item, imposters: List<T>,
            parent: ResourceLocation = ResourceLocation.parse("item/generated"),
            textureSlot: TextureSlot = TextureSlot.LAYER0,
        ) where T : Item, T : PolymerItem {
            val model = CustomModel(parent, textureSlot)

            imposters.forEach {
                val id = BuiltInRegistries.ITEM.getKey(it)
                val data = it.getPolymerCustomModelData(ItemStack(it), null)
                model.add(
                    id,
                    data,
                    ModelTemplate(Optional.of(parent), Optional.empty(), textureSlot),
                    textureSlot
                )
            }

            val key = BuiltInRegistries.ITEM.getKey(fake)
            val texture = if (parent.path.startsWith("item/"))
                key.withPrefix("item/")
            else
                key.withPrefix("block/")

            model.create(
                ModelLocationUtils.getModelLocation(fake),
                TextureMapping().put(textureSlot, texture.withPath { it.replace("waxed_", "") }), generator.output
            )
        }

        val modeledItems = listOf(
            Tools.MAGNET,
            Tools.WRENCH,
            Tools.SPAWN_PORTER,
            Multiblocks.SKY_CORE,
        )

        val customModels = modeledItems.groupBy {
            val stack = ItemStack(it)
            it.getPolymerItem(stack, null)
        }

        customModels.forEach { (fake, imposters) ->
            customModel(fake, imposters)
        }
    }
}