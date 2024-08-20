package com.possible_triangle.polytools.modules

import com.possible_triangle.polytools.Registrar
import com.possible_triangle.polytools.block.CutVinesBlock
import com.possible_triangle.polytools.block.DeMagnetizer
import com.possible_triangle.polytools.block.PolymerPortalBlock
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.item.MagnetItem
import com.possible_triangle.polytools.item.SpawnPorter
import com.possible_triangle.polytools.item.WrenchItem
import com.possible_triangle.polytools.item.WrenchItem.Lock
import eu.pb4.polymer.core.api.item.PolymerBlockItem
import eu.pb4.polymer.rsm.api.RegistrySyncUtils
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour

object Tools : Registrar() {

    val WRENCH = "wrench" createItem WrenchItem()
    val MAGNET = "magnet" createItem MagnetItem()

    val DEMAGNETIZER = "demagnetizer" createBlock DeMagnetizer()
    val DEMAGNETIZER_ITEM =
        "demagnetizer" createItem PolymerBlockItem(DEMAGNETIZER, Item.Properties(), Items.LODESTONE)

    val DEMAGNETIZER_TILE = "demagnetizer".createTile(::DeMagnetizerTile, DEMAGNETIZER)

    val CUT_VINES = "cut_vines" createBlock CutVinesBlock()

    val SPAWN_PORTER = "spawn_porter" createItem SpawnPorter()

    val FAKE_GATEWAY = "fake_gateway" createBlock PolymerPortalBlock(
        BlockBehaviour.Properties.ofFullCopy(Blocks.END_GATEWAY),
        Blocks.END_GATEWAY,
        BlockEntityType.END_GATEWAY
    )
    val FAKE_PORTAL = "fake_portal" createBlock PolymerPortalBlock(
        BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL),
        Blocks.END_PORTAL,
        BlockEntityType.END_PORTAL
    )

    val LOCK_COMPONENT = "wrench_lock".create(
        BuiltInRegistries.DATA_COMPONENT_TYPE, DataComponentType.builder<Lock>()
            .persistent(Lock.CODEC)
            .cacheEncoding()
            .build()
    )

    init {
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.DATA_COMPONENT_TYPE, LOCK_COMPONENT)
    }

}