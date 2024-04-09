package com.possible_triangle.polytools.datagen

import com.possible_triangle.polytools.modules.Backport
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
import net.minecraft.world.level.block.WeatheringCopper
import java.util.*

class ItemModels(output: FabricDataOutput) : FabricModelProvider(output) {

    override fun generateBlockStateModels(generator: BlockModelGenerators) {}

    override fun generateItemModels(
        generator: ItemModelGenerators,
    ) {
        fun <T> customModel(
            fake: Item, imposters: List<T>,
            parent: ResourceLocation = ResourceLocation("item/generated"),
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

        fun <T> customBlockModel(
            fake: Item, imposters: List<T>,
        ) where T : Item, T : PolymerItem {
            val id = BuiltInRegistries.ITEM.getKey(fake)
            val model = CustomModel(id.withPrefix("block/").withPath { it.replace("waxed_", "") })

            imposters.forEach {
                val id = BuiltInRegistries.ITEM.getKey(it)
                val data = it.getPolymerCustomModelData(ItemStack(it), null)
                model.add(
                    id,
                    data,
                    ModelTemplate(Optional.of(id.withPrefix("block/")), Optional.empty()), TextureSlot.TEXTURE
                )
            }

            model.create(
                ModelLocationUtils.getModelLocation(fake),
                TextureMapping(), generator.output
            )
        }

        val modeledItems = listOf(
            Tools.MAGNET,
            Tools.WRENCH,
            Tools.SPAWN_PORTER,
            Backport.TRIAL_KEY,
            Multiblocks.SKY_CORE,
        )

        val customModels = modeledItems.groupBy {
            val stack = ItemStack(it)
            it.getPolymerItem(stack, null)
        }

        customModels.forEach { (fake, imposters) ->
            customModel(fake, imposters)
        }

        Backport.COPPER_DOORS.forEach { _, (virtual, item) ->
            customModel(virtual.asItem(), listOf(item))
        }

        Backport.CHISELED_COPPER.forEach { _, (virtual, item) ->
            customBlockModel(virtual.asItem(), listOf(item))
        }

        customBlockModel(
            Backport.COPPER_GRATES[WeatheringCopper.WeatherState.UNAFFECTED]!!.first.asItem(),
            Backport.COPPER_GRATES.map { it.value.second }
        )

        Backport.COPPER_TRAPDOORS.forEach { _, (virtual, item) ->
            customModel(
                virtual.asItem(), listOf(item),
                parent = ResourceLocation("block/template_trapdoor_bottom"),
                textureSlot = TextureSlot.TEXTURE
            )
        }

        listOf(
            Backport.TUFF_SLAB,
            Backport.TUFF_STAIRS,
            Backport.TUFF_BRICKS,
            Backport.TUFF_BRICK_SLAB,
            Backport.TUFF_BRICK_STAIRS,
            Backport.POLISHED_TUFF,
            Backport.POLISHED_TUFF_SLAB,
            Backport.POLISHED_TUFF_STAIRS,
            Backport.CHISELED_TUFF_BRICKS,
            Backport.CHISELED_TUFF
        ).forEach {
            customBlockModel(it.getPolymerReplacement(null), listOf(it))
        }
    }

}