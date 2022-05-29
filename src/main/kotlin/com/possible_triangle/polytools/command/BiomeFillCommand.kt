package com.possible_triangle.polytools.command

import com.mojang.brigadier.CommandDispatcher
import com.possible_triangle.polytools.Biomes
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.RegistryKeyArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

object BiomeFillCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("fillbiome")
            .requires { it.hasPermissionLevel(4) }
            .then(argument("from", BlockPosArgumentType.blockPos())
                .then(argument("to", BlockPosArgumentType.blockPos())
                    .then(argument("biome", RegistryKeyArgumentType.registryKey(Registry.BIOME_KEY))
                        .executes { ctx ->
                            val from = BlockPosArgumentType.getLoadedBlockPos(ctx, "from")
                            val to = BlockPosArgumentType.getLoadedBlockPos(ctx, "to")
                            val key = ctx.getArgument("biome", RegistryKey::class.java).tryCast(Registry.BIOME_KEY)
                            val biome = key.orElseThrow()
                            BlockPos.Mutable.iterate(from, to).map {
                                Biomes.setBiome(it, ctx.source.world, biome)
                            }.size
                        }
                    )
                )
            )
        )
    }

}