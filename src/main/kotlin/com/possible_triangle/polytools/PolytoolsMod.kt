package com.possible_triangle.polytools

import com.possible_triangle.polytools.modules.Backport
import com.possible_triangle.polytools.modules.Multiblocks
import com.possible_triangle.polytools.modules.Tools
import eu.pb4.polymer.core.api.block.BlockMapper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.level.GameRules
import org.slf4j.LoggerFactory

object PolytoolsMod : ModInitializer {

    const val ID = "polytools"
    val LOGGER = LoggerFactory.getLogger(ID)!!

    val ANVIL_FALL_DAMAGE_RULE = GameRuleRegistry.register("anvilFallDamage", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true))

    override fun onInitialize() {
        Tools.register()
        Multiblocks.register()
        Backport.register()

        Events.register()

        BlockMapper.DEFAULT_MAPPER_EVENT.register { _, base ->
            ExtendedBlockMapper(base)
        }
    }
}