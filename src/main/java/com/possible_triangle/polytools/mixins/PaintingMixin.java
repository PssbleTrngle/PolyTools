package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.GameRules;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Painting.class)
public class PaintingMixin {

    @Redirect(
            method = "dropItem(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/Painting;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity overwriteDrop(Painting instance, ItemLike item) {
        var stack = new ItemStack(item);
        if (instance.level().getGameRules().getBoolean(GameRules.INSTANCE.getLOCKED_PAINTINGS_VARIANTS())) {
            instance.getVariant().unwrapKey().ifPresent(key -> {
                var nbt = new CompoundTag();
                nbt.putString("variant", key.location().toString());
                stack.set(DataComponents.ENTITY_DATA, CustomData.of(nbt));
            });
        }
        return instance.spawnAtLocation(stack);
    }

}
