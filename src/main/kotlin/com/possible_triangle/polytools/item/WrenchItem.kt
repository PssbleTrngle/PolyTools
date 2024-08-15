package com.possible_triangle.polytools.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.possible_triangle.polytools.modules.Tools
import com.possible_triangle.polytools.modules.Tools.LOCK_COMPONENT
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.PaintingVariantTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.Painting
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult


class WrenchItem : ModelledPolymerItem(Properties(), Items.STICK, 0) {

    data class Lock(val key: String, val value: String) {
        companion object {
            val CODEC: Codec<Lock> = RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.STRING.fieldOf("key").forGetter { it.key },
                    Codec.STRING.fieldOf("value").forGetter { it.value },
                ).apply(builder, ::Lock)
            }
        }
    }

    companion object {
        val PROPERTIES = listOf(AXIS, SlabBlock.TYPE, FACING, HorizontalDirectionalBlock.FACING)
        val FORBIDDEN = listOf<Comparable<*>>(SlabType.DOUBLE)

        fun useOnEntity(
            player: Player,
            level: Level,
            hand: InteractionHand,
            entity: Entity,
            hit: EntityHitResult?,
        ): InteractionResult {
            val stack = player.getItemInHand(hand)
            if (!stack.`is`(Tools.WRENCH)) return InteractionResult.PASS

            return if (entity is Painting) {
                val paintings = level.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT)
                val variants = paintings.getTagOrEmpty(PaintingVariantTags.PLACEABLE)
                    .filter { it.value().height == entity.variant.value().height }
                    .filter { it.value().width == entity.variant.value().width }

                if (variants.size < 2) return InteractionResult.FAIL

                val currentIndex = variants.indexOf(entity.variant)
                val i = if (player.isCrouching) -1 else 1
                entity.variant = variants[(currentIndex + i + variants.size) % variants.size]

                level.playSound(player, entity.pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1F, 1F)

                InteractionResult.SUCCESS
            } else {
                InteractionResult.PASS
            }
        }
    }

    override fun getPolymerItemStack(
        stack: ItemStack,
        context: TooltipFlag?,
        lookup: HolderLookup.Provider,
        player: ServerPlayer?,
    ): ItemStack {
        val enchantments = lookup.lookupOrThrow(Registries.ENCHANTMENT)
        val enchantment = enchantments.getOrThrow(Enchantments.LURE)
        return super.getPolymerItemStack(stack, context, lookup, player).apply {
            if (stack.get(LOCK_COMPONENT) != null) {
                enchant(enchantment, 1)
            }
        }
    }

    override fun modifyClientTooltip(tooltip: MutableList<Component>, stack: ItemStack, player: ServerPlayer?) {
        val lock = stack.get(LOCK_COMPONENT)
        if (lock != null) {
            tooltip.add(
                Component.literal("Locked ")
                    .append(Component.literal(lock.key).setStyle(Style.EMPTY.withItalic(true)))
                    .append(Component.literal(" to "))
                    .append(Component.literal(lock.value).setStyle(Style.EMPTY.withItalic(true)))
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
        val decoded = property.codec().parse(NbtOps.INSTANCE, StringTag.valueOf(lock.value)).result()
        return if (decoded.isPresent) {
            state.setValue(property, decoded.get());
        } else {
            null
        }
    }

    private fun <T : Comparable<T>> cycle(
        property: Property<T>,
        state: BlockState,
    ): BlockState {
        val values = property.possibleValues.filterNot { FORBIDDEN.contains(it) }
        val current = values.indexOf(state.getValue(property))
        val next = values[(current + 1) % values.size]
        return state.setValue(property, next)
    }

    private fun <T : Comparable<T>> lock(property: Property<T>, state: BlockState, stack: ItemStack) {
        val current = state.getValue(property)
        val encoded = property.codec().encodeStart(NbtOps.INSTANCE, current)
        encoded.result().ifPresent {
            stack.set(LOCK_COMPONENT, Lock(property.name, it.asString))
        }
    }

    override fun use(level: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = user.getItemInHand(hand)

        val hit = user.pick(4.5, 0F, false)

        return when (hit.type) {
            HitResult.Type.BLOCK -> {
                val pos = (hit as BlockHitResult).blockPos
                val result = rotateBlock(level as ServerLevel, pos, user, stack)
                InteractionResultHolder(result, stack)
            }

            HitResult.Type.MISS -> {
                if (stack.get(LOCK_COMPONENT) != null && user.isShiftKeyDown) {
                    stack.remove(LOCK_COMPONENT)
                    user.sendSystemMessage(Component.literal("Cleared Wrench lock"))
                    InteractionResultHolder.success(stack)
                } else {
                    InteractionResultHolder.pass(stack)
                }
            }

            else -> {
                user.sendSystemMessage(Component.literal(hit.type.name))
                super.use(level, user, hand)
            }
        }
    }

    private fun rotateBlock(world: ServerLevel, pos: BlockPos, player: Player, stack: ItemStack): InteractionResult {

        fun sound(event: SoundEvent) {
            world.playSound(
                player,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                event,
                SoundSource.BLOCKS,
                1F,
                1F
            )
        }

        val state = world.getBlockState(pos)
        val property = getProperty(state) ?: return InteractionResult.PASS
        val lock = stack.get(LOCK_COMPONENT)

        if (FORBIDDEN.contains(state.getValue(property))) {
            return InteractionResult.PASS
        }

        if (player.isShiftKeyDown) {

            lock(property, state, stack)
            sound(SoundEvents.CROSSBOW_HIT)
            player.sendSystemMessage(Component.literal("Locked Wrench to targeted rotation"))

        } else {
            val rotated = if (lock == null) {
                cycle(property, state)
            } else {
                applyLock(property, state, lock)
            } ?: return InteractionResult.FAIL

            world.setBlock(pos, rotated, 2)
            world.updateNeighborsAt(pos, Blocks.AIR);
            sound(SoundEvents.LODESTONE_COMPASS_LOCK)
        }

        return InteractionResult.SUCCESS
    }

}