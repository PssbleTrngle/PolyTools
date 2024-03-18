package com.possible_triangle.polytools.item

import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import org.joml.Vector3f

class MagnetItem : ModelledPolymerItem(Properties().rarity(Rarity.RARE), Items.STICK, 1) {

    companion object {
        const val RANGE = 5.0
        const val IGNORE_TAG = "PreventRemoteMovement"
    }

    private fun suckItems(entity: Entity, filter: (ItemEntity) -> Boolean = { true }) {
        if (entity !is ServerPlayer) return
        val level = entity.serverLevel()

        val box = AABB(entity.blockPosition()).inflate(RANGE)
        val items = level.getEntitiesOfClass(ItemEntity::class.java, box, filter)
        val ignored = items.filter { it.tags.contains(IGNORE_TAG) }

        val dust = DustParticleOptions(Vector3f(1F, 0F, 0F), 1F)
        items.filterNot(ignored::contains).forEach {
            level.sendParticles(dust, it.x, it.y + 0.5, it.z, 5, .0, .0, .0, .0)
            it.setPickUpDelay(0)
            it.setPos(entity.x, entity.y, entity.z)
        }

        ignored.forEach {
            level.sendParticles(entity, ParticleTypes.SMOKE, false, it.x, it.y + 0.5, it.z, 5, .0, .0, .0, .0)
        }
    }

    override fun use(level: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = user.getItemInHand(hand)
        suckItems(user)
        return InteractionResultHolder.success(stack)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, level, entity, slot, selected)
        if (level.gameTime % 10 != 0L) return

        if (slot in 0..8) {
            suckItems(entity) {
                it.owner != entity
            }
        }
    }

}