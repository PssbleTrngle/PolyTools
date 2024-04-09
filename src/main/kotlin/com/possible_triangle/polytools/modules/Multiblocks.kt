package com.possible_triangle.polytools.modules

import com.possible_triangle.polytools.Registrar
import com.possible_triangle.polytools.block.SkyConduit
import com.possible_triangle.polytools.block.tile.SkyConduitTile
import com.possible_triangle.polytools.effect.SkyConduitPower
import com.possible_triangle.polytools.item.ModelledPolymerItem
import eu.pb4.polymer.core.api.item.PolymerBlockItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity

object Multiblocks : Registrar() {

    val SKY_CORE = "sky_core" createItem ModelledPolymerItem(Item.Properties().rarity(Rarity.UNCOMMON), Items.HEART_OF_THE_SEA, 1)

    val SKY_CONDUIT = "sky_conduit" createBlock SkyConduit()
    val SKY_CONDUIT_ITEM = "sky_conduit"  createItem PolymerBlockItem(SKY_CONDUIT, Item.Properties().rarity(Rarity.RARE), Items.CONDUIT)
    val SKY_CONDUIT_TILE = "sky_conduit".createTile(::SkyConduitTile, SKY_CONDUIT)
    val SKY_CONDUIT_BASE_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation("sky_conduit_base_blocks"))
    val SKY_CONDUIT_POWER  = "sky_conduit_power".create(BuiltInRegistries.MOB_EFFECT, SkyConduitPower())

}