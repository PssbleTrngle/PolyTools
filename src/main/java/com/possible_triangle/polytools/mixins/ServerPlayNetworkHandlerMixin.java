package com.possible_triangle.polytools.mixins;

import com.possible_triangle.polytools.ExtendedBlockMapper;
import eu.pb4.polymer.core.api.block.BlockMapper;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overwriteBlockMapper(MinecraftServer server, Connection connection, ServerPlayer player, CallbackInfo ci) {
        var self = (ServerGamePacketListenerImpl) (Object) this;
        BlockMapper.set(self, new ExtendedBlockMapper(BlockMapper.getDefault(player)));
    }

}
