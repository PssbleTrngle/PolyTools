package com.possible_triangle.polytools

import com.possible_triangle.polytools.modules.Multiblocks
import com.possible_triangle.polytools.modules.Tools
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object PolytoolsMod : ModInitializer {

    const val ID = "polytools"
    val LOGGER = LoggerFactory.getLogger(ID)!!

    override fun onInitialize() {
        GameRules.register()

        Tools.register()
        Multiblocks.register()

        Events.register()
    }
}