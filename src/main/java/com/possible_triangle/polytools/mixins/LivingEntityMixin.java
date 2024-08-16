package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.effect.RemovedableEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
            method = "removeEffectNoUpdate",
            at = @At(value = "HEAD")
    )
    private void callOnEffectRemoved(Holder<MobEffect> holder, CallbackInfoReturnable<MobEffectInstance> cir) {
        if(holder.value() instanceof RemovedableEffect effect) {
            effect.onEffectRemoved((LivingEntity) (Object) this);
        }
    }

}
