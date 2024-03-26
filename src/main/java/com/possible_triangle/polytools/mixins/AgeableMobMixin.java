package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.extensions.PreventableAgeEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public class AgeableMobMixin implements PreventableAgeEntity {

    @Unique
    private boolean polytools$agingPrevented;

    @Override
    @Unique
    public void polytools$preventAging() {
        polytools$agingPrevented = true;
    }

    @Override
    @Unique
    public boolean polytools$isAgingPrevented() {
        return polytools$agingPrevented;
    }

    @Redirect(
            method = "aiStep()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AgeableMob;isAlive()Z")
    )
    private boolean interceptAiStep(AgeableMob instance) {
        return instance.isAlive() && !polytools$isAgingPrevented();
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("HEAD")
    )
    private void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        polytools$agingPrevented = nbt.getBoolean("AgingPrevented");
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("HEAD")
    )
    private void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("AgingPrevented", polytools$agingPrevented);
    }

}
