package com.possible_triangle.polytools.item

import com.possible_triangle.polytools.Content
import eu.pb4.polymer.api.item.SimplePolymerItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

open class ModelledPolymerItem(settings: Settings, item: Item, private val id: Int) :
    SimplePolymerItem(settings.group(Content.TAB), item) {

    companion object {
        private const val BASE_ID = 1289422
    }

    override fun getPolymerCustomModelData(stack: ItemStack, player: ServerPlayerEntity?): Int {
        return BASE_ID + id
    }

}