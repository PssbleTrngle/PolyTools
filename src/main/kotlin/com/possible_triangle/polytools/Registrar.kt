package com.possible_triangle.polytools

import eu.pb4.polymer.core.api.block.PolymerBlockUtils
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier

abstract class Registrar {

    private val entries = arrayListOf<() -> Unit>()

    fun <T, R : T> String.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    fun <T, R : T> ResourceLocation.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    fun <T : Any, R : T> ResourceKey<T>.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    infix fun <T : Block> String.createBlock(block: T): T = create(BuiltInRegistries.BLOCK, block)

    infix fun <T : Item> String.createItem(item: T): T = create(BuiltInRegistries.ITEM, item)

    fun <T : BlockEntity> String.createTile(supplier: BlockEntitySupplier<out T> , vararg blocks: Block): BlockEntityType<T> = create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        BlockEntityType.Builder.of(supplier, *blocks).build(null)
    ).also {
        PolymerBlockUtils.registerBlockEntity(it)
    }

    fun String.createSound() = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation(this)))

    fun register() {
        entries.forEach { it() }
    }

}