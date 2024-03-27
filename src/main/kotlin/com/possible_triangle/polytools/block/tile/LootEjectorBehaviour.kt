package com.possible_triangle.polytools.block.tile

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3

class LootEjectorBehaviour(private val onEject: () -> Unit) {

    private var queue = mutableListOf<ItemStack>()

    fun generate(lootTableId: ResourceLocation, level: ServerLevel, origin: BlockPos, entity: Entity? = null) {
        val lootTable = level.server.lootData.getLootTable(lootTableId)

        val params = LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin))
            .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
            .create(LootContextParamSets.CHEST)

        add(lootTable.getRandomItems(params))
    }

    fun add(stacks: Collection<ItemStack>) {
        queue.addAll(stacks)
    }

    fun tick(level: ServerLevel, pos: BlockPos) {
        val vec = Vec3.upFromBottomCenterOf(pos, 1.0)

        if (queue.isEmpty()) return
        if (level.gameTime % 5 != 0L) return

        val stack = queue.removeFirst()

        val entity = ItemEntity(level, vec.x, vec.y, vec.z, stack, 0.0, 0.2, 0.0)
        level.tryAddFreshEntityWithPassengers(entity)

        onEject()
    }

    fun read(nbt: CompoundTag) {
        queue = nbt.getList("queued_items", 10).map {
            ItemStack.CODEC.parse(NbtOps.INSTANCE, it).result().get()
        }.toMutableList()
    }

    fun write(nbt: CompoundTag) {
        nbt.put("queued_items", queue.mapTo(ListTag()) {
            ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, it).result().get()
        })
    }

}