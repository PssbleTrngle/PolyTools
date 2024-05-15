package com.possible_triangle.polytools

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.level.GameRules

object GameRules {

    val ANVIL_FALL_DAMAGE_RULE = GameRuleRegistry.register("anvilFallDamage", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true))

    val LOCKED_PAINTINGS_VARIANTS = GameRuleRegistry.register("lockedPaintingsVariants", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true))

}