package com.possible_triangle.polytools.block.tile

import com.possible_triangle.polytools.Content
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.random.SimpleWeightedRandomList
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.level.Level
import net.minecraft.world.level.SpawnData
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.jvm.optionals.getOrNull

class TrialSpawnerTile(pos: BlockPos, state: BlockState) : BlockEntity(Content.TRIAL_SPAWNER_TILE, pos, state) {

    private var spawnDelay = 40

    private var simultaneousMobs = 0
    private var simultaneousMobsPerPlayer = 0
    private var totalMobs = 0
    private var totalMobsPerPlayer = 0

    private var lootTables: SimpleWeightedRandomList<ResourceLocation> = SimpleWeightedRandomList.empty()

    var spawnData: SpawnData? = null
        private set;

    private var registeredPlayers = mutableSetOf<UUID>()
    private var currentMobs = mutableListOf<UUID>()
    private var mobsSpawned = 0

    private val ejector = LootEjectorBehaviour {
        sound(Content.TRIAL_SPAWNER_EJECT_SOUND)
    }

    companion object {
        private val SPAWN_DELAY = 50
        private val SLEEP_DELAY = 36000
        private val PLAYER_RANGE = 14
        private val SPAWN_RANGE = 4.0

        fun serverTick(level: Level, pos: BlockPos, state: BlockState, tile: TrialSpawnerTile) = with(tile) {
            if (level !is ServerLevel) return

            ejector.tick(level, pos)

            if (spawnDelay > 0) {
                spawnDelay--
                return
            }

            if (level.getCurrentDifficultyAt(pos).difficulty == Difficulty.PEACEFUL) return

            val players = level.players().filter {
                !it.isSpectator && it.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) <= PLAYER_RANGE
            }

            if (players.isEmpty()) return

            players.forEach {
                if (registeredPlayers.add(it.uuid)) {
                    sound(Content.TRIAL_SPAWNER_DETECT_PLAYER_SOUND)
                }
            }

            val total = totalMobs + totalMobsPerPlayer * registeredPlayers.size
            val count = simultaneousMobs + simultaneousMobsPerPlayer * registeredPlayers.size

            if (mobsSpawned >= total) {
                val living = currentMobs
                    .mapNotNull { level.getEntity(it) }
                    .filter { it.isAlive }

                return if (living.isEmpty()) {
                    ejectLoot(level)
                    sleep()
                } else {
                    delay()
                }
            }

            repeat(count) {
                attemptSpawn(level, spawnData ?: return)
            }
        }
    }

    private fun sleep() {
        spawnDelay = SLEEP_DELAY
        registeredPlayers = mutableSetOf()
        currentMobs = mutableListOf()
        mobsSpawned = 0
    }

    private fun delay() {
        spawnDelay = SPAWN_DELAY
    }

    private fun ejectLoot(level: ServerLevel) {
        val vec = Vec3.upFromBottomCenterOf(blockPos, 1.0)

        level.sendParticles(ParticleTypes.FIREWORK, vec.x, vec.y, vec.z, 16, 0.1, 0.4, 0.1, 0.2)

        registeredPlayers.forEach { uuid ->
            val player = level.getPlayerByUUID(uuid)

            val lootTableId = lootTables.getRandomValue(level.random).getOrNull() ?: return
            ejector.generate(lootTableId, level, blockPos, player)
        }
    }

    private fun attemptSpawn(level: ServerLevel, spawnData: SpawnData) {
        val type = EntityType.by(spawnData.entityToSpawn).getOrNull() ?: return delay()

        val randomSource = level.random

        val x = blockPos.x + (randomSource.nextDouble() - randomSource.nextDouble()) * SPAWN_RANGE + 0.5
        val y = (blockPos.y + randomSource.nextInt(3) - 1).toDouble()
        val z = blockPos.z + (randomSource.nextDouble() - randomSource.nextDouble()) * SPAWN_RANGE + 0.5

        if (level.noCollision(type.getAABB(x, y, z))) {
            val entity = EntityType.loadEntityRecursive(spawnData.entityToSpawn, level) {
                it.moveTo(x, y, z, it.yRot, it.xRot)
                it
            }

            if (entity == null) return delay()

            if (entity is Mob) {
                if (spawnData.entityToSpawn.size() == 1 && spawnData.entityToSpawn.contains("id", 8)) {
                    entity.finalizeSpawn(
                        level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, null
                    )
                }
            }

            if (!level.tryAddFreshEntityWithPassengers(entity)) return delay()

            level.levelEvent(2004, blockPos, 0)
            level.gameEvent(entity, GameEvent.ENTITY_PLACE, BlockPos.containing(x, y, z))
            if (entity is Mob) {
                entity.spawnAnim()
                entity.setPersistenceRequired()
            }

            mobsSpawned++
            currentMobs.add(entity.uuid)

            sound(Content.TRIAL_SPAWNER_SPAWN_MOB_SOUND)

            delay()
        }
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)

        ejector.read(nbt)

        nbt.putInt("simultaneous_mobs", simultaneousMobs)

        nbt.putInt("simultaneous_mobs_added_per_player", simultaneousMobsPerPlayer)
        nbt.putInt("total_mobs", totalMobs)
        nbt.putInt("total_mobs_added_per_player", totalMobsPerPlayer)

        nbt.put("loot_tables_to_eject", lootTables.unwrap().mapTo(ListTag()) { wrapper ->
            CompoundTag().apply {
                putString("data", wrapper.data.toString())
                putInt("weight", wrapper.weight.asInt())
            }
        })

        if (spawnData != null) nbt.put(
            "spawn_data",
            SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, spawnData).result().get()
        )

        nbt.putInt("spawn_delay", spawnDelay)
        nbt.putInt("total_mobs_spawned", mobsSpawned)

        registeredPlayers = nbt.getList("registered_players", 11).map {
            NbtUtils.loadUUID(it)
        }.toMutableSet()

        currentMobs = nbt.getList("current_mobs", 11).map {
            NbtUtils.loadUUID(it)
        }.toMutableList()
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)

        ejector.write(nbt)

        simultaneousMobs = nbt.getInt("simultaneous_mobs")
        simultaneousMobsPerPlayer = nbt.getInt("simultaneous_mobs_added_per_player")
        totalMobs = nbt.getInt("total_mobs")
        totalMobsPerPlayer = nbt.getInt("total_mobs_added_per_player")

        val lootTablesBuilder = SimpleWeightedRandomList.builder<ResourceLocation>()

        nbt.getList("loot_tables_to_eject", 10).map { it as CompoundTag }.forEach {
            val id = ResourceLocation(it.getString("data"))
            val weight = it.getInt("weight")
            lootTablesBuilder.add(id, weight)
        }

        lootTables = lootTablesBuilder.build()

        spawnData = SpawnData.CODEC.parse(NbtOps.INSTANCE, nbt.get("spawn_data")).result().getOrNull()

        spawnDelay = nbt.getInt("spawn_delay")
        mobsSpawned = nbt.getInt("total_mobs_spawned")

        nbt.put("registered_players", registeredPlayers.mapTo(ListTag()) {
            NbtUtils.createUUID(it)
        })

        nbt.put("current_mobs", currentMobs.mapTo(ListTag()) {
            NbtUtils.createUUID(it)
        })
    }

}