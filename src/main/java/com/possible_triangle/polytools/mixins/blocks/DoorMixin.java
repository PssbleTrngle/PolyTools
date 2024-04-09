package com.possible_triangle.polytools.mixins.blocks;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class)
public class DoorMixin implements PolymerBlock {

    @Override
    public Block getPolymerBlock(BlockState state) {
        return state.getBlock();
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.CRIMSON_DOOR.withPropertiesOf(state);
    }
}
