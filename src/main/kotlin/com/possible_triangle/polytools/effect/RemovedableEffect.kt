package com.possible_triangle.polytools.effect

import net.minecraft.world.entity.LivingEntity

interface RemovedableEffect {

    fun onEffectRemoved(entity: LivingEntity)

}