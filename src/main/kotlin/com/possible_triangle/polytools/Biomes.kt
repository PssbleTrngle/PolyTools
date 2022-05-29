package com.possible_triangle.polytools

import com.possible_triangle.polytools.item.ChiselItem
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.biome.SpawnSettings

object Biomes : Registrar() {

    val CHISEL_BIOMES = ChiselItem.BLOCKS.map { (_, entries) ->
        entries.associateWith { Identifier(PolytoolsMod.ID, it) }
    }.reduce { a, b -> a + b }.mapValues { (_, id) ->
        val biome = Biome.Builder()
            .precipitation(Biome.Precipitation.NONE)
            .downfall(0F)
            .temperature(0.7F)
            .spawnSettings(SpawnSettings.INSTANCE)
            .generationSettings(GenerationSettings.INSTANCE)
            .category(Biome.Category.MUSHROOM)
            .effects(BiomeEffects.Builder()
                .fogColor(0)
                .waterColor(0)
                .skyColor(0)
                .waterFogColor(0)
                .build())
            .build()
        val key = RegistryKey.of(Registry.BIOME_KEY, id)
        key.create(BuiltinRegistries.BIOME, biome)
        key
    }

    fun setBiome(pos: BlockPos, world: World, biome: RegistryKey<Biome>) {
        val chunk = world.getChunk(pos.x shr 4, pos.z shr 4)
        val sectionIndex = world.getSectionIndex(pos.y)
        val section = chunk.getSection(sectionIndex)
        val biomes = world.registryManager.get(Registry.BIOME_KEY)
        biomes.getEntry(biome).ifPresent {
            val sectionPos = BlockPos(
                pos.x - chunk.pos.startX,
                pos.y - section.yOffset,
                pos.z - chunk.pos.startZ)
            section.biomeContainer.set(pos.x and 3, pos.y and 3, pos.z and 3, it)
            chunk.setNeedsSaving(true)
        }
    }

}