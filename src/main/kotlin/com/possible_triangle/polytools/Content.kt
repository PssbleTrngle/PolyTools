package com.possible_triangle.polytools

import com.possible_triangle.polytools.PolytoolsMod.ID
import com.possible_triangle.polytools.block.DeMagnetizer
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.item.MagnetItem
import com.possible_triangle.polytools.item.WrenchItem
import eu.pb4.polymer.core.api.item.PolymerBlockItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntityType

object Content : Registrar() {

    private fun String.modded(): String = "$ID:$this"

    val WRENCH = "wrench".modded().createItem(WrenchItem())
    val MAGNET = "magnet".modded().createItem(MagnetItem())

    val DEMAGNETIZER = "demagnetizer".modded() createBlock DeMagnetizer()
    val DEMAGNETIZER_ITEM =
        "demagnetizer".modded() createItem PolymerBlockItem(DEMAGNETIZER, Item.Properties(), Items.LODESTONE)

    val DEMAGNETIZER_TILE = "demagnetizer".modded()
        .create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            BlockEntityType.Builder.of(::DeMagnetizerTile, DEMAGNETIZER).build(null)
        )


}