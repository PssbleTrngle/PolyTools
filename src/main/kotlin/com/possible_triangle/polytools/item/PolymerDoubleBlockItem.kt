package com.possible_triangle.polytools.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.DoubleHighBlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

class PolymerDoubleBlockItem(block: Block, properties: Properties, private val virtualItem: Item) : DoubleHighBlockItem(block, properties), PolymerItem {

    override fun getPolymerItem(stack: ItemStack, player: ServerPlayer?) = virtualItem

    override fun getPolymerCustomModelData(stack: ItemStack, player: ServerPlayer?): Int {
        return ModelledPolymerItem.BASE_ID
    }

}