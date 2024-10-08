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

abstract class Registrar(val namespace: String = PolytoolsMod.ID) {

    private val entries = arrayListOf<() -> Unit>()

    protected fun <T : Any, R : T> String.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, "$namespace:$this", value) }
        return value
    }

    protected fun <T : Any, R : T> overwrite(registry: Registry<T>, from: T, value: R): R {
        entries.add { Registry.registerMapping(registry, registry.getId(from),  registry.getKey(from)!!.path, value) }
        return value
    }

    protected fun <T : Any, R : T> ResourceLocation.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    protected fun <T : Any, R : T> ResourceKey<T>.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    protected infix fun <T : Block> String.createBlock(block: T): T = create(BuiltInRegistries.BLOCK, block)

    protected infix fun <T : Item> String.createItem(item: T): T = create(BuiltInRegistries.ITEM, item)

    protected fun <T : Block> overwriteBlock(from: Block, createBlock: (Block) -> T): T =
        overwrite(BuiltInRegistries.BLOCK, from, createBlock(from))

    protected fun <T : BlockEntity> String.createTile(
        supplier: BlockEntitySupplier<out T>,
        vararg blocks: Block,
    ): BlockEntityType<T> = create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        BlockEntityType.Builder.of(supplier, *blocks).build(null)
    ).also {
        PolymerBlockUtils.registerBlockEntity(it)
    }

    protected fun String.createSound() = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation(this)))

    protected open fun onRegister() {}

    fun register() {
        entries.forEach { it() }
        onRegister()
    }

}