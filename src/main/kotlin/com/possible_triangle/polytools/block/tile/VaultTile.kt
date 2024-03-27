package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.Content
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.StringTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import kotlin.jvm.optionals.getOrNull

class VaultTile(pos: BlockPos, state: BlockState) : BlockEntity(Content.VAULT_TILE, pos, state) {

    private var lootTable: ResourceLocation? = null

    private var keyItem: Item? = null
    private var requiredLore = emptyList<String>()

    private var rewardedPlayers = mutableListOf<UUID>()

    private val ejector = LootEjectorBehaviour {
        sound(Content.VAULT_EJECT_SOUND)
    }

    companion object {
        fun serverTick(level: Level, pos: BlockPos, state: BlockState, tile: VaultTile) = with(tile) {
            if (level !is ServerLevel) return
            ejector.tick(level, pos)
        }
    }

    private fun test(stack: ItemStack): Boolean {
        if (keyItem == null) return false
        if (!stack.`is`(keyItem)) return false

        if (requiredLore.isNotEmpty()) {
            val lore =
                stack.tag?.getCompound(ItemStack.TAG_DISPLAY)?.getList(ItemStack.TAG_LORE, 8)?.map { it.asString }
                    ?: return false
            return requiredLore.all { lore.contains(it) }
        }

        return true
    }

    fun unlock(player: Player, stack: ItemStack): InteractionResult {
        if (lootTable == null) return InteractionResult.PASS
        val level = getLevel()

        if (rewardedPlayers.contains(player.uuid)) return InteractionResult.FAIL

        return if (test(stack)) {
            sound(Content.VAULT_INSERT_KEY_SOUND)
            if (!player.abilities.instabuild) stack.shrink(1)

            if (level is ServerLevel) ejector.generate(lootTable!!, level, blockPos, player)

            rewardedPlayers.add(player.uuid)

            InteractionResult.CONSUME
        } else {
            sound(Content.VAULT_INSERT_KEY_FAIL_SOUND)

            InteractionResult.FAIL
        }

    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        ejector.read(nbt)

        nbt.getCompound("config").apply {
            lootTable = ResourceLocation(getString("loot_table"))

            val keyItemTag = getCompound("key_item")
            keyItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation(keyItemTag.getString("id"))).getOrNull()

            val components = keyItemTag.getCompound("components")
            requiredLore = components.getList("minecraft:lore", 8).map { it.asString }
        }

        rewardedPlayers = nbt.getList("rewarded_players", 11).map {
            NbtUtils.loadUUID(it)
        }.toMutableList()
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        ejector.write(nbt)

        nbt.put("config", CompoundTag().apply {
            if (lootTable != null) putString("loot_table", lootTable.toString())

            put("key_item", CompoundTag().apply {
                if (keyItem != null) putString("id", BuiltInRegistries.ITEM.getKey(keyItem!!).toString())

                if (requiredLore.isNotEmpty()) {
                    put("components", CompoundTag().apply {
                        put("minecraft:lore", requiredLore.mapTo(ListTag()) { StringTag.valueOf(it) })
                    })
                }
            })
        })

        nbt.put("rewarded_players", rewardedPlayers.mapTo(ListTag()) {
            NbtUtils.createUUID(it)
        })
    }

}
