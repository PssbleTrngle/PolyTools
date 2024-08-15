package com.possible_triangle.polytools.effect

import com.possible_triangle.polytools.modules.Multiblocks
import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity

class SkyConduitPower : MobEffect(MobEffectCategory.BENEFICIAL, 0xf7f08d), PolymerStatusEffect, RemovedableEffect {

    override fun getPolymerReplacement(player: ServerPlayer?): MobEffect {
        return MobEffects.CONDUIT_POWER.value()
    }

    override fun onEffectAdded(entity: LivingEntity, i: Int) {
        super.onEffectAdded(entity, i)
        if (entity is ServerPlayer && !entity.abilities.mayfly) {
            entity.abilities.mayfly = true
            entity.onUpdateAbilities()
        }
    }

    override fun onEffectRemoved(entity: LivingEntity) {
        if (entity is ServerPlayer && entity.abilities.mayfly && !entity.hasEffect(Multiblocks.SKY_CONDUIT_POWER)) {
            entity.gameMode.gameModeForPlayer.updatePlayerAbilities(entity.abilities)
            entity.onUpdateAbilities()
        }
    }

}