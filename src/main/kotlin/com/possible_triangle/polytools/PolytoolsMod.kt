package com.possible_triangle.polytools

import eu.pb4.polymer.core.api.block.PolymerBlockUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object PolytoolsMod : ModInitializer {

    const val ID = "polytools"
    val LOGGER = LoggerFactory.getLogger(ID)!!

    override fun onInitialize() {
        PolymerBlockUtils.registerBlockEntity(Content.DEMAGNETIZER_TILE)
        Content.register()
        Events.register()
    }
}