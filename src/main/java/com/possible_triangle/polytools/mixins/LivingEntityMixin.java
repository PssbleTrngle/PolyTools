package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.effect.RemovedableEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
            method = "onEffectRemoved",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V")
    )
    private void callOnEffectRemoved(MobEffectInstance instance, CallbackInfo ci) {
        if(instance.getEffect() instanceof RemovedableEffect effect) {
            effect.onEffectRemoved((LivingEntity) (Object) this);
        }
    }

}
