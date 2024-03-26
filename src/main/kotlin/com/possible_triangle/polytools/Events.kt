package com.possible_triangle.polytools

import com.possible_triangle.polytools.block.CutVinesBlock
import com.possible_triangle.polytools.command.RespawnCommand
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.commands.ResetChunksCommand
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

object Events {

    fun register() {
        UseBlockCallback.EVENT.register(CutVinesBlock::onBlockUse)

        UseEntityCallback.EVENT.register(PoisonousPotato::onUse)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            RespawnCommand.register(dispatcher)
            ResetChunksCommand.register(dispatcher)
        }
    }



}