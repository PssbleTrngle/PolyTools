package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.modules.Multiblocks
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import kotlin.math.absoluteValue


class SkyConduitTile(pos: BlockPos, state: BlockState) : TickingTile(Multiblocks.SKY_CONDUIT_TILE, pos, state) {

    companion object {
        private const val DEFAULT_RANGE_FACTOR = 1.0
    }

    private val blocks = mutableListOf<BlockPos>()
    private var active = false

    private var customRangeFactor: Double? = null

    val rangeFactor
        get() = customRangeFactor ?: DEFAULT_RANGE_FACTOR

    override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
        if (level.gameTime % 40L == 0L) {
            val staysActive = searchBlocks(level, pos)
            if (staysActive != active) {
                val sound = if (staysActive) SoundEvents.CONDUIT_ACTIVATE else SoundEvents.CONDUIT_DEACTIVATE
                level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.2f)
            }

            active = staysActive
            if (active) giveEffects(level, pos)
        }
    }

    private fun searchBlocks(level: ServerLevel, origin: BlockPos): Boolean {
        blocks.clear()

        for (x in -1..1) for (y in -1..1) for (z in -1..1) {
            if (x == 0 && y == 0 && z == 0) continue
            if (!level.getBlockState(origin.offset(x, y, z)).isAir) return false
        }

        for (x in -2..2) for (y in -2..2) for (z in -2..2) {
            if (x != 0 && y != 0 && z != 0) continue
            if (x.absoluteValue < 2 && y.absoluteValue < 2 && z.absoluteValue < 2) continue
            val pos = origin.offset(x, y, z)
            val state = level.getBlockState(pos)
            if (state.`is`(Multiblocks.SKY_CONDUIT_BASE_BLOCKS)) blocks.add(pos)
        }

        return blocks.size >= 16
    }

    private fun giveEffects(level: ServerLevel, origin: BlockPos) {
        val range = blocks.size * rangeFactor
        val rangeSquared = range * range
        val box = AABB(origin).inflate(range)

        val predicate = { it: LivingEntity ->
            !it.isInWaterRainOrBubble && origin.distSqr(it.blockPosition()) <= rangeSquared
        }

        val players = level.getEntitiesOfClass(ServerPlayer::class.java, box) { !it.isSpectator }
        val enemies = level.getEntitiesOfClass(LivingEntity::class.java, box) { it is Monster && predicate(it) }

        players.filter(predicate).forEach {
            val effect = MobEffectInstance(Multiblocks.SKY_CONDUIT_POWER, 100, 0, true, true)
            it.addEffect(effect)
        }

        enemies.forEach {
            val levitation = MobEffectInstance(MobEffects.LEVITATION, 100, 0, true, true)
            it.addEffect(levitation)
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        customRangeFactor?.let { nbt.putDouble("custom_range_factor", it) }
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        customRangeFactor = nbt.getDouble("custom_range_factor").takeIf { it > 0 }
    }

}