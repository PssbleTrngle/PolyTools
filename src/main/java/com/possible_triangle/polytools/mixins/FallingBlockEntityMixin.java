package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.PolytoolsMod;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

    @Redirect(
            method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AnvilBlock;damage(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState shouldAnvilDamage(BlockState blockState) {
        var self = (FallingBlockEntity) (Object) this;
        if(self.level().getGameRules().getBoolean(PolytoolsMod.INSTANCE.getANVIL_FALL_DAMAGE_RULE())) {
            return AnvilBlock.damage(blockState);
        } else {
            return blockState;
        }
    }

}
