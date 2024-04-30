package com.possible_triangle.polytools

import com.possible_triangle.polytools.block.CutVinesBlock
import com.possible_triangle.polytools.command.RespawnCommand
import com.possible_triangle.polytools.item.WrenchItem
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.server.commands.ResetChunksCommand

object Events {

    fun register() {
        UseBlockCallback.EVENT.register(CutVinesBlock::onBlockUse)

        UseEntityCallback.EVENT.register(PoisonousPotato::onUse)
        UseEntityCallback.EVENT.register(WrenchItem::useOnEntity)
        UseEntityCallback.EVENT.register(SheerableItemFrames::useOn)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            RespawnCommand.register(dispatcher)
            ResetChunksCommand.register(dispatcher)
        }
    }



}