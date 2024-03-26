package com.possible_triangle.polytools.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.possible_triangle.polytools.item.SpawnPorter
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.arguments.EntityArgument

object RespawnCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal("respawn").requires { it.hasPermission(2) }
            .then(argument("target", EntityArgument.player())
                .executes(::execute)
            )
        )
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        val target = EntityArgument.getPlayer(context, "target")
        SpawnPorter.respawn(target)
        return 1
    }

}