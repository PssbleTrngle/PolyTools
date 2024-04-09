package com.possible_triangle.polytools.modules

import com.possible_triangle.polytools.Registrar
import com.possible_triangle.polytools.block.*
import com.possible_triangle.polytools.block.tile.TrialSpawnerTile
import com.possible_triangle.polytools.block.tile.VaultTile
import com.possible_triangle.polytools.item.ModelledPolymerBlockItem
import com.possible_triangle.polytools.item.ModelledPolymerItem
import com.possible_triangle.polytools.item.PolymerDoubleBlockItem
import eu.pb4.polymer.core.api.block.SimplePolymerBlock
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.WeatheringCopper.WeatherState
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockSetType

object Backport : Registrar("minecraft") {

    val TRIAL_KEY = "trial_key".createItem(ModelledPolymerItem(Properties().rarity(Rarity.UNCOMMON), Items.STICK, 11))

    val TRIAL_SPAWNER = "trial_spawner".createBlock(TrialSpawner())
    val TRIAL_SPAWNER_TILE = "trial_spawner".createTile(::TrialSpawnerTile, TRIAL_SPAWNER)

    val VAULT = "vault".createBlock(Vault())
    val VAULT_TILE = "vault".createTile(::VaultTile, VAULT)

    val TRIAL_SPAWNER_DETECT_PLAYER_SOUND = "block.trial_spawner.detect_player".createSound()
    val TRIAL_SPAWNER_EJECT_SOUND = "block.trial_spawner.eject_item".createSound()
    val TRIAL_SPAWNER_SPAWN_MOB_SOUND = "block.trial_spawner.spawn_mob".createSound()

    val VAULT_EJECT_SOUND = "block.vault.eject_item".createSound()
    val VAULT_INSERT_KEY_SOUND = "block.vault.insert_item".createSound()
    val VAULT_INSERT_KEY_FAIL_SOUND = "block.vault.insert_item_fail".createSound()

    val copperSet = BlockSetType(
        "copper",
        true,
        SoundType.COPPER,
        SoundEvents.IRON_DOOR_CLOSE,
        SoundEvents.IRON_DOOR_OPEN,
        SoundEvents.IRON_TRAPDOOR_CLOSE,
        SoundEvents.IRON_TRAPDOOR_OPEN,
        SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
        SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
        SoundEvents.STONE_BUTTON_CLICK_OFF,
        SoundEvents.STONE_BUTTON_CLICK_ON
    )

    fun <I : Item> registerWeathering(
        name: String,
        virtuals: Map<WeatherState, Block>,
        blockFactory: (Block, WeatherState) -> Block,
        waxedBlockFactory: (Block, WeatherState) -> Block,
        itemFactory: (Item, Block, WeatherState) -> I,
    ): Map<WeatherState, Pair<Block, I>> {
        var previous: Block? = null
        return WeatherState.values().associateWith { state ->
            val fullName = if (state == WeatherState.UNAFFECTED) name
            else "${state.name.lowercase()}_$name"

            val virtual = virtuals[state] ?: throw NullPointerException("Missing virtual block for $fullName")
            val block = fullName createBlock blockFactory(virtual, state)
            val item = fullName createItem itemFactory(virtual.asItem(), block, state)

            val waxedName = "waxed_$fullName"
            val waxedBlock = waxedName createBlock waxedBlockFactory(virtual, state)
            waxedName createItem itemFactory(virtual.asItem(), waxedBlock, state)

            OxidizableBlocksRegistry.registerWaxableBlockPair(block, waxedBlock)
            previous?.let {
                OxidizableBlocksRegistry.registerOxidizableBlockPair(previous, block)
            }

            previous = block

            virtual to item
        }
    }

    val COPPER_DOORS = registerWeathering("copper_door", mapOf(
        WeatherState.UNAFFECTED to Blocks.ACACIA_DOOR,
        WeatherState.EXPOSED to Blocks.BIRCH_DOOR,
        WeatherState.WEATHERED to Blocks.OAK_DOOR,
        WeatherState.OXIDIZED to Blocks.JUNGLE_DOOR,
    ), { virtual, state ->
        WeatheringPolymerDoorBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_DOOR), copperSet, virtual, state
        )
    }, { virtual, _ ->
        PolymerDoorBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_DOOR), copperSet, virtual
        )
    }, { virtual, block, _ ->
        PolymerDoubleBlockItem(block, Properties(), virtual)
    })

    val COPPER_TRAPDOORS = registerWeathering("copper_trapdoor", mapOf(
        WeatherState.UNAFFECTED to Blocks.ACACIA_TRAPDOOR,
        WeatherState.EXPOSED to Blocks.BIRCH_TRAPDOOR,
        WeatherState.WEATHERED to Blocks.OAK_TRAPDOOR,
        WeatherState.OXIDIZED to Blocks.JUNGLE_TRAPDOOR,
    ), { virtual, state ->
        WeatheringPolymerTrapdoorBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_TRAPDOOR), copperSet, virtual, state
        )
    }, { virtual, _ ->
        PolymerTrapDoorBlock(
            BlockBehaviour.Properties.copy(Blocks.IRON_TRAPDOOR), copperSet, virtual
        )
    }, { virtual, block, _ ->
        ModelledPolymerBlockItem(block, Properties(), virtual)
    })

    val COPPER_GRATES = registerWeathering("copper_grate", mapOf(
        WeatherState.UNAFFECTED to Blocks.BIRCH_LEAVES,
        WeatherState.EXPOSED to Blocks.BIRCH_LEAVES,
        WeatherState.WEATHERED to Blocks.BIRCH_LEAVES,
        WeatherState.OXIDIZED to Blocks.BIRCH_LEAVES,
    ), { virtual, state ->
        WeatheringPolymerGrateBlock(
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK), virtual, state
        )
    }, { virtual, state ->
        PolymerGrateBlock(
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK), virtual, state
        )
    }, { virtual, block, state ->
        ModelledPolymerBlockItem(block, Properties(), virtual, state.ordinal)
    })

    val CHISELED_COPPER = registerWeathering("chiseled_copper", mapOf(
        WeatherState.UNAFFECTED to Blocks.WAXED_CUT_COPPER,
        WeatherState.EXPOSED to Blocks.WAXED_EXPOSED_CUT_COPPER,
        WeatherState.WEATHERED to Blocks.WAXED_WEATHERED_CUT_COPPER,
        WeatherState.OXIDIZED to Blocks.WAXED_OXIDIZED_CUT_COPPER,
    ), { virtual, state ->
        WeatheringPolymerBlock(
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK), virtual, state
        )
    }, { virtual, _ ->
        SimplePolymerBlock(
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK), virtual
        )
    }, { virtual, block, _ ->
        ModelledPolymerBlockItem(block, Properties(), virtual)
    })

    val TUFF_SLAB = "tuff_slab".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerSlabBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_SLAB),
                Blocks.WAXED_CUT_COPPER_SLAB
            ),
            Properties(), Items.WAXED_CUT_COPPER_SLAB
        )
    }

    val TUFF_STAIRS = "tuff_stairs".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerStairBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS),
                Blocks.TUFF.defaultBlockState(),
                Blocks.WAXED_CUT_COPPER_STAIRS
            ),
            Properties(), Items.WAXED_CUT_COPPER_STAIRS
        )
    }

    val CHISELED_TUFF = "chiseled_tuff".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock SimplePolymerBlock(
                BlockBehaviour.Properties.copy(Blocks.TUFF),
                Blocks.WAXED_COPPER_BLOCK
            ),
            Properties(), Items.WAXED_COPPER_BLOCK
        )
    }

    val CHISELED_TUFF_BRICKS = "chiseled_tuff_bricks".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock SimplePolymerBlock(
                BlockBehaviour.Properties.copy(Blocks.TUFF),
                Blocks.WAXED_OXIDIZED_COPPER
            ),
            Properties(), Items.WAXED_OXIDIZED_COPPER
        )
    }

    val TUFF_BRICKS = "tuff_bricks".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock SimplePolymerBlock(
                BlockBehaviour.Properties.copy(Blocks.TUFF),
                Blocks.WAXED_EXPOSED_COPPER
            ),
            Properties(), Items.WAXED_EXPOSED_COPPER
        )
    }

    val TUFF_BRICK_SLAB = "tuff_brick_slab".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerSlabBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_SLAB),
                Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB
            ),
            Properties(), Items.WAXED_EXPOSED_CUT_COPPER_SLAB
        )
    }

    val TUFF_BRICK_STAIRS = "tuff_brick_stairs".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerStairBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS),
                TUFF_BRICKS.block.defaultBlockState(),
                Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS
            ),
            Properties(), Items.WAXED_EXPOSED_CUT_COPPER_STAIRS
        )
    }

    val POLISHED_TUFF = "polished_tuff".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock SimplePolymerBlock(
                BlockBehaviour.Properties.copy(Blocks.TUFF),
                Blocks.WAXED_WEATHERED_COPPER
            ),
            Properties(), Items.WAXED_WEATHERED_COPPER
        )
    }

    val POLISHED_TUFF_SLAB = "polished_tuff_slab".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerSlabBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_SLAB),
                Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB
            ),
            Properties(), Items.WAXED_WEATHERED_CUT_COPPER_SLAB
        )
    }

    val POLISHED_TUFF_STAIRS = "polished_tuff_stairs".let {
        it createItem ModelledPolymerBlockItem(
            it createBlock PolymerStairBlock(
                BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS),
                POLISHED_TUFF.block.defaultBlockState(),
                Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS
            ),
            Properties(), Items.WAXED_WEATHERED_CUT_COPPER_STAIRS
        )
    }

}