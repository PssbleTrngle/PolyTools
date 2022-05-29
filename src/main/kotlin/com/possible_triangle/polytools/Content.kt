package com.possible_triangle.polytools

import com.possible_triangle.polytools.PolytoolsMod.ID
import com.possible_triangle.polytools.block.DeMagnetizer
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.item.BrushItem
import com.possible_triangle.polytools.item.ChiselItem
import com.possible_triangle.polytools.item.MagnetItem
import com.possible_triangle.polytools.item.WrenchItem
import eu.pb4.polymer.api.item.PolymerBlockItem
import eu.pb4.polymer.api.item.PolymerItemGroup
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object Content : Registrar() {

    private fun String.modded(): String = "$ID:$this"

    val TAB = PolymerItemGroup.create(
        Identifier(ID, ID),
        TranslatableText("itemGroup.$ID.$ID")
    ) { ItemStack(WRENCH) }

    val WRENCH = "wrench".modded().createItem(WrenchItem())
    val MAGNET = "magnet".modded().createItem(MagnetItem())
    val CHISEL = "chisel".modded().createItem(ChiselItem())
    val BRUSH = "brush".modded().createItem(BrushItem())

    val DEMAGNETIZER = "demagnetizer".modded() createBlock DeMagnetizer()
    val DEMAGNETIZER_ITEM =
        "demagnetizer".modded() createItem PolymerBlockItem(DEMAGNETIZER, Item.Settings().group(TAB), Items.LODESTONE)

    val DEMAGNETIZER_TILE = "demagnetizer".modded()
        .create(
            Registry.BLOCK_ENTITY_TYPE,
            BlockEntityType.Builder.create(::DeMagnetizerTile, DEMAGNETIZER).build(null)
        )


}