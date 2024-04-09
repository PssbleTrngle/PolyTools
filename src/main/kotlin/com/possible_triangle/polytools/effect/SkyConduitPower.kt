package com.possible_triangle.polytools.effect

import com.possible_triangle.polytools.modules.Multiblocks
import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeMap

class SkyConduitPower : MobEffect(MobEffectCategory.BENEFICIAL, 0xf7f08d), PolymerStatusEffect {

    override fun getPolymerReplacement(player: ServerPlayer?): MobEffect {
        return MobEffects.CONDUIT_POWER
    }

    override fun addAttributeModifiers(entity: LivingEntity, attributes: AttributeMap, amplifier: Int) {
        super.addAttributeModifiers(entity, attributes, amplifier)
        if (entity is ServerPlayer && !entity.abilities.mayfly) {
            entity.abilities.mayfly = true
            entity.onUpdateAbilities()
        }
    }

    override fun removeAttributeModifiers(entity: LivingEntity, attributes: AttributeMap, amplifier: Int) {
        super.removeAttributeModifiers(entity, attributes, amplifier)
        if (entity is ServerPlayer && entity.abilities.mayfly && !entity.hasEffect(Multiblocks.SKY_CONDUIT_POWER)) {
            entity.gameMode.gameModeForPlayer.updatePlayerAbilities(entity.abilities)
            entity.onUpdateAbilities()
        }
    }

}