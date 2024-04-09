package com.possible_triangle.polytools.item

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

open class ModelledPolymerItem(properties: Properties, item: Item, private val id: Int) :
    SimplePolymerItem(properties, item) {

    companion object {
        const val BASE_ID = 1289422
    }

    override fun getPolymerCustomModelData(stack: ItemStack, player: ServerPlayer?): Int {
        return BASE_ID + id
    }

}