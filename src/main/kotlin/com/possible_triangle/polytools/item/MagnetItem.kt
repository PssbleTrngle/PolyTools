package com.possible_triangle.polytools.item

import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.DustParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3f
import net.minecraft.world.World

class MagnetItem : ModelledPolymerItem(Settings().rarity(Rarity.RARE), Items.STICK, 1) {

    companion object {
        const val RANGE = 5.0
        const val IGNORE_TAG = "PreventRemoteMovement"
    }

    private fun suckItems(entity: Entity, filter: (ItemEntity) -> Boolean = { true }) {
        if (entity !is ServerPlayerEntity) return
        val world = entity.getWorld()

        val box = Box(entity.blockPos).expand(RANGE)
        val items = world.getEntitiesByClass(ItemEntity::class.java, box, filter)
        val ignored = items.filter { it.scoreboardTags.contains(IGNORE_TAG) }

        val dust = DustParticleEffect(Vec3f(1F, 0F, 0F), 1F)
        items.filterNot(ignored::contains).forEach {
            world.spawnParticles(dust, it.x, it.y + 0.5, it.z, 5, .0, .0, .0, .0)
            it.setPickupDelay(0)
            it.refreshPositionAndAngles(entity.x, entity.y, entity.z, it.yaw, it.pitch)
        }

        ignored.forEach {
            world.spawnParticles(entity, ParticleTypes.SMOKE, false, it.x, it.y + 0.5, it.z, 5, .0, .0, .0, .0)
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        suckItems(user)
        return TypedActionResult.success(stack, true)
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (world.time % 10 != 0L) return

        if (slot in 0..8) {
            suckItems(entity) {
                it.thrower != entity.uuid
            }
        }
    }

}