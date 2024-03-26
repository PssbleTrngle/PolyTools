package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.block.CutVinesBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VineBlock.class)
public class VineBlockMixin {

    @Unique
    private static final String CHECK_TARGET = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z";

    @Redirect(
            method = "canSupportAtFace(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$canSupportAtFace(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

    @Redirect(
            method = "getUpdatedState(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$getUpdatedState(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

    @Redirect(
            method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$randomTick(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

    @Redirect(
            method = "canSpread(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$canSpread(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

    @Redirect(
            method = "canBeReplaced(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/context/BlockPlaceContext;)Z",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$canBeReplaced(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

    @Redirect(
            method = "getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At(value = "INVOKE", target = CHECK_TARGET)
    )
    private boolean inject$getStateForPlacement(BlockState state, Block block) {
        return CutVinesBlock.Companion.checkBlock(state);
    }

}
