package com.possible_triangle.polytools

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

abstract class Registrar {

    private val entries = arrayListOf<() -> Unit>()

    fun <T, R : T> String.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    fun <T, R : T> Identifier.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    fun <T, R : T> RegistryKey<T>.create(registry: Registry<T>, value: R): R {
        entries.add { Registry.register(registry, this, value) }
        return value
    }

    infix fun <T : Block> String.createBlock(block: T): T = create(Registry.BLOCK, block)

    infix fun <T : Item> String.createItem(item: T): T = create(Registry.ITEM, item)

    fun register() {
        entries.forEach { it() }
    }

}