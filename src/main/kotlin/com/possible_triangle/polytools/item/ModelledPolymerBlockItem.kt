package com.possible_triangle.polytools.item

import eu.pb4.polymer.core.api.item.PolymerBlockItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

open class ModelledPolymerBlockItem(block: Block, properties: Properties, item: Item, private val id: Int = 0) :
    PolymerBlockItem(block, properties, item) {

    override fun getPolymerCustomModelData(stack: ItemStack, player: ServerPlayer?): Int {
        return ModelledPolymerItem.BASE_ID + id
    }

}