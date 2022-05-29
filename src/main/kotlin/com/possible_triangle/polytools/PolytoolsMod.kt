package com.possible_triangle.polytools

import com.possible_triangle.polytools.command.BiomeFillCommand
import eu.pb4.polymer.api.block.PolymerBlockUtils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import org.slf4j.LoggerFactory

object PolytoolsMod : ModInitializer {

    const val ID = "polytools"
    val LOGGER = LoggerFactory.getLogger(ID)!!

    override fun onInitialize() {
        PolymerBlockUtils.registerBlockEntity(Content.DEMAGNETIZER_TILE)
        Content.register()
        Biomes.register()

        CommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            BiomeFillCommand.register(dispatcher)
        }
    }
}