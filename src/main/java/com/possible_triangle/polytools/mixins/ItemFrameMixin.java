package com.possible_triangle.polytools.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {


    @Inject(
            method = "dropItem(Lnet/minecraft/world/entity/Entity;Z)V",
            at = @At(value = "HEAD")
    )
    private void inject$canSupportAtFace(Entity entity, boolean bl, CallbackInfo ci) {
        var self = (ItemFrame) (Object) this;
        if(self.isInvisible()) self.setInvisible(false);
    }

}
