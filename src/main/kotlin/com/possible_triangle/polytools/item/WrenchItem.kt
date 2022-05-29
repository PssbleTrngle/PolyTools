package com.possible_triangle.polytools.item

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.SlabBlock
import net.minecraft.block.enums.SlabType
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties.AXIS
import net.minecraft.state.property.Properties.FACING
import net.minecraft.state.property.Property
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class WrenchItem : ModelledPolymerItem(Settings(), Items.STICK, 0) {

    private data class Lock(val key: String, val value: NbtElement)

    private fun ItemStack.getLock(): Lock? {
        return nbt?.getCompound(LOCK_KEY)?.let {
            Lock(it.getString("key"), it.get("value") ?: return null)
        }
    }

    companion object {
        private const val LOCK_KEY = "locked_property"
        val PROPERTIES = listOf(AXIS, SlabBlock.TYPE, FACING, HorizontalFacingBlock.FACING)
        val FORBIDDEN = listOf<Comparable<*>>(SlabType.DOUBLE)
    }

    override fun getPolymerItemStack(stack: ItemStack, player: ServerPlayerEntity?): ItemStack {
        return super.getPolymerItemStack(stack, player).apply {
            if (stack.getLock() != null) {
                addEnchantment(Enchantments.LURE, 1)
            }
        }
    }

    override fun modifyClientTooltip(tooltip: MutableList<Text>, stack: ItemStack, player: ServerPlayerEntity?) {
        val lock = stack.getLock()
        if(lock != null) {
            tooltip.add(LiteralText("Locked ")
                .append(LiteralText(lock.key).setStyle(Style.EMPTY.withItalic(true)))
                .append(LiteralText(" to "))
                .append(LiteralText(lock.value.asString()).setStyle(Style.EMPTY.withItalic(true)))
            )
        }
    }

    private fun getProperty(state: BlockState): Property<*>? {
        return PROPERTIES.firstOrNull { state.properties.contains(it) }
    }

    private fun <T : Comparable<T>> applyLock(
        property: Property<T>,
        state: BlockState,
        lock: Lock,
    ): BlockState? {
        if (property.name != lock.key) return null
        val decoded = property.codec.parse(NbtOps.INSTANCE, lock.value).result()
        return if (decoded.isPresent) {
            state.with(property, decoded.get());
        } else {
            null
        }
    }

    private fun <T : Comparable<T>> cycle(
        property: Property<T>,
        state: BlockState,
    ): BlockState {
        val values = property.values.filterNot { FORBIDDEN.contains(it) }
        val current = values.indexOf(state.get(property))
        val next = values[(current + 1) % values.size]
        return state.with(property, next)
    }

    private fun <T : Comparable<T>> lock(property: Property<T>, state: BlockState, stack: ItemStack) {
        val current = state.get(property)
        stack.orCreateNbt.put(LOCK_KEY, NbtCompound().apply {
            val encoded = property.codec.encodeStart(NbtOps.INSTANCE, current)
            encoded.result().ifPresent {
                putString("key", property.name)
                put("value", it)
            }
        })
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)

        val hit = user.raycast(4.5, 0F, false)

        return when (hit.type) {
            HitResult.Type.BLOCK -> {
                val pos = (hit as BlockHitResult).blockPos
                val result = rotateBlock(world as ServerWorld, pos, user, stack)
                TypedActionResult(result, stack)
            }
            HitResult.Type.MISS -> {
                if (stack.getLock() != null && user.isSneaking) {
                    stack.removeSubNbt(LOCK_KEY)
                    user.sendMessage(LiteralText("Cleared Wrench lock"), true)
                    TypedActionResult.success(stack)
                } else {
                    TypedActionResult.pass(stack)
                }
            }
            else -> {
                user.sendMessage(LiteralText(hit.type.name), true)
                super.use(world, user, hand)
            }
        }
    }

    private fun rotateBlock(world: ServerWorld, pos: BlockPos, player: PlayerEntity, stack: ItemStack): ActionResult {

        fun sound(event: SoundEvent) {
            world.playSound(
                player,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                event,
                SoundCategory.BLOCKS,
                1F,
                1F
            )
        }

        val state = world.getBlockState(pos)
        val property = getProperty(state) ?: return ActionResult.PASS
        val lock = stack.getLock()

        if (FORBIDDEN.contains(state.get(property))) {
            return ActionResult.PASS
        }

        if (player.isSneaking) {

            lock(property, state, stack)
            sound(SoundEvents.ITEM_CROSSBOW_HIT)
            player.sendMessage(LiteralText("Locked Wrench to targeted rotation"), true)

        } else {
            val rotated = if (lock == null) {
                cycle(property, state)
            } else {
                applyLock(property, state, lock)
            } ?: return ActionResult.FAIL

            world.setBlockState(pos, rotated, 2)
            world.updateNeighbor(pos, Blocks.AIR, pos);
            sound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK)
        }

        return ActionResult.SUCCESS
    }

}