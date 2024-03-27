package com.possible_triangle.polytools

import com.possible_triangle.polytools.PolytoolsMod.ID
import com.possible_triangle.polytools.block.CutVinesBlock
import com.possible_triangle.polytools.block.DeMagnetizer
import com.possible_triangle.polytools.block.TrialSpawner
import com.possible_triangle.polytools.block.Vault
import com.possible_triangle.polytools.block.tile.DeMagnetizerTile
import com.possible_triangle.polytools.block.tile.TrialSpawnerTile
import com.possible_triangle.polytools.block.tile.VaultTile
import com.possible_triangle.polytools.item.MagnetItem
import com.possible_triangle.polytools.item.ModelledPolymerItem
import com.possible_triangle.polytools.item.SpawnPorter
import com.possible_triangle.polytools.item.WrenchItem
import eu.pb4.polymer.core.api.item.PolymerBlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity

object Content : Registrar() {

    private fun String.modded(): String = "$ID:$this"

    val WRENCH = "wrench".modded().createItem(WrenchItem())
    val MAGNET = "magnet".modded().createItem(MagnetItem())

    val DEMAGNETIZER = "demagnetizer".modded() createBlock DeMagnetizer()
    val DEMAGNETIZER_ITEM =
        "demagnetizer".modded() createItem PolymerBlockItem(DEMAGNETIZER, Item.Properties(), Items.LODESTONE)

    val DEMAGNETIZER_TILE = "demagnetizer".modded().createTile(::DeMagnetizerTile, DEMAGNETIZER)

    val CUT_VINES = "cut_vines".modded() createBlock CutVinesBlock()

    val SPAWN_PORTER = "spawn_porter".modded().createItem(SpawnPorter())

    val TRIAL_KEY = "trial_key".createItem(ModelledPolymerItem(Item.Properties().rarity(Rarity.UNCOMMON), Items.STICK, 11))

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

}